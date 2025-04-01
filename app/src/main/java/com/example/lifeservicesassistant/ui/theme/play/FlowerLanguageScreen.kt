// FlowerLanguageScreen.kt
package com.example.lifeservicesassistant.ui.theme.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading


@Composable
fun FlowerLanguageScreen(
    viewModel: FlowerLanguageViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var showCopyToast by remember { mutableStateOf(false) }

    LaunchedEffect(showCopyToast) {
        if (showCopyToast) {
            kotlinx.coroutines.delay(2000)
            showCopyToast = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("花语箴言") },
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
            // 搜索框
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("输入花名") },
                placeholder = { Text("例如: 玫瑰花、百合花") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.searchFlower(searchText)
                            focusManager.clearFocus()
                        },
                        enabled = searchText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Search, "搜索")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotBlank()) {
                            viewModel.searchFlower(searchText)
                            focusManager.clearFocus()
                        }
                    }
                ),
                singleLine = true
            )

            // 内容区域
            when {
                state.isLoading -> FullScreenLoading()
                state.error != null -> ErrorMessageWithRetry(
                    error = state.error!!,
                    onRetry = { viewModel.searchFlower(searchText) }
                )
                state.flowerInfo != null -> FlowerInfoCard(
                    flowerInfo = state.flowerInfo!!,
                    onCopyClick = { text ->
                        viewModel.copyToClipboard(text, "花语箴言")
                        showCopyToast = true
                    },
                    showToast = showCopyToast
                )
                else -> Placeholder(searchText.isNotEmpty())
            }
        }
    }
}

@Composable
private fun ErrorMessageWithRetry(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
private fun Placeholder(hasSearched: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasSearched) "未找到该花的花语信息" else "请输入花名查询花语",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun FlowerInfoCard(
    flowerInfo: FlowerLanguageItem,
    onCopyClick: (String) -> Unit,
    showToast: Boolean
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 花名标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = flowerInfo.cnflower,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onCopyClick("${flowerInfo.cnflower}(${flowerInfo.enflower})\n花语: ${flowerInfo.flowerlang}\n箴言: ${flowerInfo.flowerprov}")
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, "复制")
                }
            }
            
            // 英文花名
            Text(
                text = flowerInfo.enflower,
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 花语
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "花语",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = flowerInfo.flowerlang,
                    style = MaterialTheme.typography.body1
                )
            }
            
            // 花语箴言
            Column {
                Text(
                    text = "箴言",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF2196F3),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = flowerInfo.flowerprov,
                    style = MaterialTheme.typography.body1,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            
            // 复制成功提示
            if (showToast) {
                Text(
                    text = "已复制到剪贴板",
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}