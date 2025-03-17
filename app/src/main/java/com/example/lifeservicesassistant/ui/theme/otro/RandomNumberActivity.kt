package com.example.lifeservicesassistant.ui.theme.otro

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

class RandomNumberActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RandomNumberScreen(onNavigateBack = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomNumberScreen(onNavigateBack: () -> Unit) {
    var min by remember { mutableStateOf("1") }
    var max by remember { mutableStateOf("30") }
    var count by remember { mutableStateOf("4") }
    var allowDuplicates by remember { mutableStateOf(true) }
    var numbers by remember { mutableStateOf<List<Int>?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager



    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        com.example.lifeservicesassistant.util.CommonTopBar(
            title = "随机数生成",
            onNavigateBack = onNavigateBack
        )
        // 输入部分

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = min,
                onValueChange = { min = it },
                label = { Text("最小值") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            Text("到")
            TextField(
                value = max,
                onValueChange = { max = it },
                label = { Text("最大值") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        TextField(
            value = count,
            onValueChange = { count = it },
            label = { Text("生成数量") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("允许重复")
            Switch(
                checked = allowDuplicates,
                onCheckedChange = { allowDuplicates = it }
            )
        }

        Button(
            onClick = {
                scope.launch {
                    try {
                        val minVal = min.toInt()
                        val maxVal = max.toInt()
                        val countVal = count.toInt()

                        if (minVal > maxVal) throw IllegalArgumentException("最小值不能大于最大值")
                        if (countVal <= 0) throw IllegalArgumentException("生成数量必须大于0")

                        numbers = generateRandomNumbers(
                            min = minVal,
                            max = maxVal,
                            count = countVal,
                            allowDuplicates = allowDuplicates
                        )
                    } catch (e: Exception) {
                        // 处理错误（可以使用Snackbar显示错误信息）
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("生成随机数")
        }

        // 结果展示部分
        numbers?.let { generatedNumbers ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(generatedNumbers) { number ->
                        Text(
                            text = number.toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                Button(
                    onClick = {
                        clipboardManager.setText(generatedNumbers.joinToString(", "))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("复制所有")
                }
            }
        }
    }
}

private fun generateRandomNumbers(
    min: Int,
    max: Int,
    count: Int,
    allowDuplicates: Boolean
): List<Int> {
    return when {
        allowDuplicates -> {
            List(count) { Random.nextInt(min, max + 1) }
        }
        else -> {
            val range = min..max
            require(count <= range.count()) { "生成数量不能超过范围大小" }
            range.shuffled().take(count)
        }
    }
}