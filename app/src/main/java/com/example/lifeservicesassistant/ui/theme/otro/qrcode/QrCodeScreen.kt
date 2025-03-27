package com.example.lifeservicesassistant.ui.theme.otro.qrcode// QrCodeScreen.kt
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(
    viewModel: QrCodeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // 显示错误消息
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
                // 清除错误状态
                viewModel.uiState = viewModel.uiState.copy(error = null)
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var text by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("输入二维码内容") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // 高级设置区域
                AdvancedSettings(
                    uiState = uiState,
                    onSettingsChanged = { el, bgColor, fgColor, logoUrl, size, margin, logoWidth ->
                        viewModel.updateSettings(el, bgColor, fgColor, logoUrl, size, margin, logoWidth)
                    }
                )
                
                Button(
                    onClick = { viewModel.generateQrCode(text) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = text.isNotEmpty() && !uiState.isLoading
                ) {
                    Text("生成二维码")
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator()
                }
                uiState.base64Image?.let { base64 ->
                    QrCodeImage(base64Image = base64)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QrCodeImage(base64Image: String?) {
    val context = LocalContext.current
    val bitmap = remember(base64Image) {
        base64Image?.let { base64 ->
            try {
                // 清理Base64字符串
                val cleanBase64 = base64
                    .replace("\n", "")
                    .replace(" ", "")
                    .trim()

                // 使用android.util.Base64解码
                val bytes = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                Log.e("QRCode", "解码失败", e)
                null
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(200.dp)
            )
        } else {
            Text("无法加载二维码", color = Color.Red)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSettings(
    uiState: QrCodeUiState,
    onSettingsChanged: (
        el: String?,
        bgColor: String?,
        fgColor: String?,
        logoUrl: String?,
        size: Int?,
        margin: Int?,
        logoWidth: Int?
    ) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (expanded) "隐藏高级设置" else "显示高级设置")
        }
        
        if (expanded) {
            var el by remember { mutableStateOf(uiState.el ?: "") }
            var bgColor by remember { mutableStateOf(uiState.bgColor ?: "") }
            var fgColor by remember { mutableStateOf(uiState.fgColor ?: "") }
            var logoUrl by remember { mutableStateOf(uiState.logoUrl ?: "") }
            var size by remember { mutableStateOf(uiState.size?.toString() ?: "") }
            var margin by remember { mutableStateOf(uiState.margin?.toString() ?: "") }
            var logoWidth by remember { mutableStateOf(uiState.logoWidth?.toString() ?: "") }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = el,
                    onValueChange = {
                        el = it
                        onSettingsChanged(
                            it.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("纠错等级 (h/q/m/l)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = bgColor,
                    onValueChange = {
                        bgColor = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("背景色 (FFFFFF)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = fgColor,
                    onValueChange = {
                        fgColor = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("前景色 (000000)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = logoUrl,
                    onValueChange = {
                        logoUrl = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            it.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("Logo URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = size,
                    onValueChange = {
                        size = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            it.toIntOrNull(),
                            margin.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("尺寸大小 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = margin,
                    onValueChange = {
                        margin = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            it.toIntOrNull(),
                            logoWidth.toIntOrNull()
                        )
                    },
                    label = { Text("边距大小 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = logoWidth,
                    onValueChange = {
                        logoWidth = it
                        onSettingsChanged(
                            el.takeIf { it.isNotEmpty() },
                            bgColor.takeIf { it.isNotEmpty() },
                            fgColor.takeIf { it.isNotEmpty() },
                            logoUrl.takeIf { it.isNotEmpty() },
                            size.toIntOrNull(),
                            margin.toIntOrNull(),
                            it.toIntOrNull()
                        )
                    },
                    label = { Text("Logo宽度 (像素)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}