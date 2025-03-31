// FileExtensionScreen.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

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
fun FileExtensionScreen(
    viewModel: FileExtensionViewModel,
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
                title = { Text("文件扩展名查询") },
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
                label = { Text("输入文件扩展名") },
                placeholder = { Text("例如: apk、pdf") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.searchExtension(searchText)
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
                            viewModel.searchExtension(searchText)
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
                    onRetry = { viewModel.searchExtension(searchText) }
                )
                state.extensionInfo != null -> ExtensionInfoCard(
                    extensionInfo = state.extensionInfo!!,
                    onCopyClick = { text ->
                        viewModel.copyToClipboard(text, "文件扩展名信息")
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
            text = if (hasSearched) "未找到该扩展名信息" else "请输入文件扩展名查询",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun ExtensionInfoCard(
    extensionInfo: FileExtensionItem,
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
            // 扩展名标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = ".${extensionInfo.targa}",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        onCopyClick(".${extensionInfo.targa}: ${extensionInfo.notes}")
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, "复制")
                }
            }
            
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 扩展名说明
            Text(
                text = extensionInfo.notes,
                style = MaterialTheme.typography.body1
            )
            
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