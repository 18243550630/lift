// DreamScreen.kt
package com.example.lifeservicesassistant.ui.theme.play

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.healthy.ErrorMessage
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading

@Composable
fun DreamScreen(
    viewModel: DreamViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == state.dreamItems.size - 1 && state.canLoadMore && !state.isLoading) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("周公解梦") },
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
        ) {
            // 搜索框
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("输入梦境关键词，如'苹果'") },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.searchDream(searchText)
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Search, "搜索")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchDream(searchText)
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true
            )

            when {
                state.isLoading && state.dreamItems.isEmpty() -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!) {
                    viewModel.searchDream(searchText)
                }
                state.dreamItems.isEmpty() -> Placeholder(searchText.isNotEmpty())
                else -> DreamList(state.dreamItems, listState, state.isLoading)
            }
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
            text = if (hasSearched) "暂无解梦结果，换个关键词试试" else "请输入梦境关键词查询解梦",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun DreamList(
    items: List<DreamItem>,
    listState: LazyListState,
    isLoading: Boolean
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            DreamCard(item)
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun DreamCard(item: DreamItem) {
    val cleanedContent = remember(item.result) {
        item.result
            .replace("<br>", "\n")  // 替换<br>为换行
            .split("\n")            // 按行分割
            .filter { it.isNotBlank() } // 过滤空行
    }

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "【${item.type}】${item.title}",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))

            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                cleanedContent.forEach { paragraph ->
                    Text(
                        text = "• $paragraph",  // 添加项目符号
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}