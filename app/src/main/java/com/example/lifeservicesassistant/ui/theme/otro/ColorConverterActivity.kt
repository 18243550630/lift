package com.example.lifeservicesassistant.ui.theme.otro

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.ClipboardManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeservicesassistant.util.ColorConverter.isValidHex
import java.lang.Math.abs
import java.lang.Math.min


class ColorConverterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ColorConverterScreen(onNavigateBack = { finish() })
                }
            }
        }
    }
}

// ViewModel 和转换逻辑
class ColorViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("ColorPrefs", Context.MODE_PRIVATE)

    var hex = mutableStateOf(prefs.getString("HEX", "#79BD9A") ?: "#79BD9A")
    var rgb = mutableStateOf(prefs.getString("RGB", "121,189,155") ?: "121,189,155")
    var cmyk = mutableStateOf(prefs.getString("CMYK", "35,0,17,25") ?: "35,0,17,25")
    var hsv = mutableStateOf(prefs.getString("HSV", "150%,36%,74%") ?: "150%,36%,74%")

    fun updateFromHex(newHex: String) {
        if (!isValidHex(newHex)) return
        if (hex.value == newHex) return

        hex.value = newHex
        val rgbList = hexToRgb(newHex)
        if (rgbList.size == 3) {
            // 强制触发RGB更新
            updateFromRgb(rgbList[0], rgbList[1], rgbList[2])
        }
    }

    fun updateFromRgb(r: Int, g: Int, b: Int) {
        val newRgb = "$r,$g,$b"
        if (rgb.value == newRgb) return
        rgb.value = newRgb
        prefs.edit().putString("RGB", newRgb).apply()

        hex.value = rgbToHex(r, g, b)
        prefs.edit().putString("HEX", hex.value).apply()

        cmyk.value = rgbToCmyk(r, g, b)
        prefs.edit().putString("CMYK", cmyk.value).apply()

        hsv.value = rgbToHsv(r, g, b)
        prefs.edit().putString("HSV", hsv.value).apply()
    }

    fun updateFromCmyk(c: Int, m: Int, y: Int, k: Int) {
        val r = (1 - min(1.0, c/100.0 * (1 - k/100.0) + k/100.0)) * 255
        val g = (1 - min(1.0, m/100.0 * (1 - k/100.0) + k/100.0)) * 255
        val b = (1 - min(1.0, y/100.0 * (1 - k/100.0) + k/100.0)) * 255
        updateFromRgb(r.toInt(), g.toInt(), b.toInt())
    }

    fun updateFromHsv(h: Float, s: Float, v: Float) {
        val c = v * s
        val x = c * (1 - abs(((h / 60) % 2) - 1))
        val m = v - c

        val (r, g, b) = when {
            h < 60 -> Triple(c, x, 0f)
            h < 120 -> Triple(x, c, 0f)
            h < 180 -> Triple(0f, c, x)
            h < 240 -> Triple(0f, x, c)
            h < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        updateFromRgb(
            ((r + m) * 255).coerceIn(0f, 255f).toInt(),
            ((g + m) * 255).coerceIn(0f, 255f).toInt(),
            ((b + m) * 255).coerceIn(0f, 255f).toInt()
        )
    }

    // 验证函数
    fun isValidHex(hex: String): Boolean =
        hex.matches(Regex("^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$"))

    fun isValidRgb(rgb: String): Boolean {
        val parts = rgb.split(",")
        if (parts.size != 3) return false
        return parts.all { it.trim().toIntOrNull()?.let { v -> v in 0..255 } ?: false }
    }

    fun isValidCmyk(cmyk: String): Boolean {
        val parts = cmyk.split(",")
        if (parts.size != 4) return false
        return parts.all { it.trim().toIntOrNull()?.let { v -> v in 0..100 } ?: false }
    }

    fun isValidHsv(hsv: String): Boolean {
        val parts = hsv.replace("%", "").replace("%", "").split(",")
        if (parts.size != 3) return false
        return parts[0].toFloatOrNull()?.let { h -> h in 0f..360f } ?: false &&
                parts[1].toFloatOrNull()?.let { s -> s in 0f..1f } ?: false &&
                parts[2].toFloatOrNull()?.let { v -> v in 0f..1f } ?: false
    }

    // 转换工具
    private fun hexToRgb(hex: String): List<Int> {
        val cleanHex = hex.replace("#", "")
        return when {
            cleanHex.length == 6 -> cleanHex.chunked(2).map { it.toInt(16) }
            cleanHex.length == 3 -> cleanHex.map { "$it$it".toInt(16) }
            else -> emptyList()
        }
    }
}

    private fun rgbToHex(r: Int, g: Int, b: Int) =
        String.format("#%02X%02X%02X", r, g, b)

    private fun rgbToCmyk(r: Int, g: Int, b: Int): String {
        val rPrime = r / 255.0
        val gPrime = g / 255.0
        val bPrime = b / 255.0

        val k = 1 - maxOf(rPrime, gPrime, bPrime)
        if (k == 1.0) return "0,0,0,100"

        val c = ((1 - rPrime - k) / (1 - k) * 100).toInt()
        val m = ((1 - gPrime - k) / (1 - k) * 100).toInt()
        val y = ((1 - bPrime - k) / (1 - k) * 100).toInt()

        return "$c,$m,$y,${(k * 100).toInt()}"
    }

    private fun rgbToHsv(r: Int, g: Int, b: Int): String {
        val max = maxOf(r, g, b).toFloat()
        val min = minOf(r, g, b).toFloat()
        val delta = max - min

        val h = when {
            delta == 0f -> 0f
            max == r.toFloat() -> ((g - b) / delta) % 6
            max == g.toFloat() -> (b - r) / delta + 2
            else -> (r - g) / delta + 4
        } * 60

        val s = if (max == 0f) 0f else delta / max
        return "${h.toInt()}%,${(s * 100).toInt()}%,${(max / 255 * 100).toInt()}%"
    }


// 界面组件
@Composable
fun ColorConverterScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: ColorViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ColorViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val clipboardManager = remember {
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    Column(modifier = Modifier.padding(16.dp)) {
        com.example.lifeservicesassistant.util.CommonTopBar(
            title = "颜色转换器",
            onNavigateBack = onNavigateBack
        )
        ColorRow(
            label = "HEX",
            value = viewModel.hex.value,
            validate = viewModel::isValidHex,
            onUpdate = viewModel::updateFromHex,
            clipboardManager = clipboardManager
        )

        ColorRow(
            label = "RGB",
            value = viewModel.rgb.value,
            validate = viewModel::isValidRgb,
            onUpdate = { str ->
                str.split(",")
                    .map { it.trim().toInt() }
                    .let { (r, g, b) -> viewModel.updateFromRgb(r, g, b) }
            },
            clipboardManager = clipboardManager
        )

        ColorRow(
            label = "CMYK",
            value = viewModel.cmyk.value,
            validate = viewModel::isValidCmyk,
            onUpdate = { str ->
                str.split(",")
                    .map { it.trim().toInt() }
                    .let { (c, m, y, k) -> viewModel.updateFromCmyk(c, m, y, k) }
            },
            clipboardManager = clipboardManager
        )

        ColorRow(
            label = "HSV",
            value = viewModel.hsv.value,
            validate = viewModel::isValidHsv,
            onUpdate = { str ->
                str.replace("%", "").replace("%", "").split(",")
                    .map { it.trim().toFloat() }
                    .let { (h, s, v) -> viewModel.updateFromHsv(h, s, v) }
            },
            clipboardManager = clipboardManager
        )
    }
}

// ColorConverterScreen.kt
@Composable
fun ColorRow(
    label: String,
    value: String,
    validate: (String) -> Boolean,
    onUpdate: (String) -> Unit,
    clipboardManager: ClipboardManager
) {
    var text by remember { mutableStateOf(value) }
    var lastValidValue by remember { mutableStateOf(value) }

    // 关键：双向绑定状态
    LaunchedEffect(value) {
        if (text != value) {
            text = value
            lastValidValue = value
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(text = label, modifier = Modifier.width(80.dp))
        TextField(
            value = text,
            onValueChange = {
                text = it
                if (validate(it)) {
                    lastValidValue = it
                    onUpdate(it)  // 实时触发转换
                }
            },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused && !validate(text)) {
                        text = lastValidValue
                    }
                },
            singleLine = true,
            isError = !validate(text),
            supportingText = {
                if (!validate(text)) {
                    Text("格式错误", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        IconButton(onClick = {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(label, value)
            )
        }) {
            Icon(Icons.Default.ContentCopy, "复制")
        }
    }
}