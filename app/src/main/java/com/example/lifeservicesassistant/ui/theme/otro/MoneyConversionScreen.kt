// MoneyConversionScreen.kt
package com.example.lifeservicesassistant.ui.theme.otro

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading

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
                state.result != null -> ConversionResult(state.result!!)
            }
        }
    }
}

@Composable
private fun ConversionResult(result: MoneyResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 格式化数字
        Text(
            text = "格式化数字: ${result.fnresult}",
            style = MaterialTheme.typography.body1
        )

        // 中文大写
        Card(
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "中文大写:",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.cnresult,
                    style = MaterialTheme.typography.h6
                )
            }
        }

        // 英文大写
        Card(
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "英文大写:",
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.enresult,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}