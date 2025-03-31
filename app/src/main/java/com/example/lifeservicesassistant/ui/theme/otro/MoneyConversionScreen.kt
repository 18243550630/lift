// MoneyConversionScreen.kt
package com.example.lifeservicesassistant.ui.theme.otro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MoneyConversionScreen(
    viewModel: MoneyConversionViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var amount by remember { mutableStateOf("") }
    var currencyType by remember { mutableStateOf("rmb") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("金额转大写") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 金额输入框
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("输入金额") },
                placeholder = { Text("例如: 12345.67") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 货币类型选择
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "人民币" to "rmb",
                    "美元" to "usd"
                ).forEach { (name, type) ->
                    FilterChip(
                        selected = currencyType == type,
                        onClick = {
                            currencyType = type
                            if (amount.isNotBlank()) {
                                viewModel.convertAmount(amount, type)
                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(name)
                    }
                }
            }

            // 转换按钮
            Button(
                onClick = { viewModel.convertAmount(amount, currencyType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = amount.isNotBlank()
            ) {
                Text("转换")
            }

            // 结果显示
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!)
                state.result != null -> ConversionResult(
                    result = state.result!!,
                    viewModel = viewModel
                )
            }
        }
    }
}

// MoneyConversionScreen.kt
@Composable
private fun ConversionResult(
    result: MoneyResult,
    viewModel: MoneyConversionViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 格式化数字 - 可复制
        CopyableTextItem(
            label = "格式化数字",
            text = result.fnresult,
            onCopy = { viewModel.copyToClipboard(result.fnresult, "格式化金额") }
        )

        // 中文大写 - 可复制
        CopyableCard(
            title = "中文大写",
            content = result.cnresult,
            onCopy = { viewModel.copyToClipboard(result.cnresult, "中文大写金额") }
        )

        // 英文大写 - 可复制
        CopyableCard(
            title = "英文大写",
            content = result.enresult,
            onCopy = { viewModel.copyToClipboard(result.enresult, "英文大写金额") }
        )
    }
}

// 可复制的文本项组件
@Composable
private fun CopyableTextItem(
    label: String,
    text: String,
    onCopy: () -> Unit
) {
    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2000)
            showToast = false
        }
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onCopy()
                showToast = true
            }
        ) {
            Text(
                text = "$label: ",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "复制",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(16.dp)
            )
        }

        if (showToast) {
            Text(
                text = "已复制到剪贴板",
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// 可复制的卡片组件
@Composable
private fun CopyableCard(
    title: String,
    content: String,
    onCopy: () -> Unit
) {
    var showToast by remember { mutableStateOf(false) }

    LaunchedEffect(showToast) {
        if (showToast) {
            delay(2000)
            showToast = false
        }
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCopy()
                showToast = true
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$title:",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "复制",
                    tint = MaterialTheme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = if (title == "中文大写") {
                    MaterialTheme.typography.h6
                } else {
                    MaterialTheme.typography.body1
                }
            )

            if (showToast) {
                Text(
                    text = "已复制到剪贴板",
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}