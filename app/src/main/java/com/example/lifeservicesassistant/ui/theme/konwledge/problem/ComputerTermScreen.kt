// ComputerTermScreen.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.lifeservicesassistant.ui.theme.play.ErrorMessage

// ComputerTermScreen.kt
@Composable
fun ComputerTermScreen(
    viewModel: ComputerTermViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计算机术语查询") },
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
                label = { Text("输入术语缩写") },
                placeholder = { Text("例如: ASPI、API") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.searchTerm(searchText)
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
                            viewModel.searchTerm(searchText)
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
                    onRetry = { viewModel.searchTerm(searchText) }
                )
                state.term != null -> TermCard(
                    term = state.term!!,
                    onCopyClick = { text ->
                        viewModel.copyToClipboard(text, "计算机术语")
                    }
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
            text = if (hasSearched) "未找到相关术语" else "请输入术语缩写查询",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun TermCard(
    term: ComputerTermItem,
    onCopyClick: (String) -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = term.abbr,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colors.primary,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = term.type,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = term.notes,
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onCopyClick("${term.abbr} (${term.type}): ${term.notes}")
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("复制术语")
            }
        }
    }
}