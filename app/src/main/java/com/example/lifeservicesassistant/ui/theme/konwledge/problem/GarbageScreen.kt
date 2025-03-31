// GarbageScreen.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.FullScreenLoading

import com.example.lifeservicesassistant.ui.theme.play.ErrorMessage

@Composable
fun GarbageScreen(
    viewModel: GarbageViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == state.items.size - 1 && state.canLoadMore && !state.isLoading) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("垃圾分类查询") },
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
                label = { Text("输入物品名称") },
                placeholder = { Text("例如: 眼镜、电池") },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.searchGarbage(searchText)
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Default.Search, "搜索")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.searchGarbage(searchText)
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true
            )

            // 内容区域
            when {
                state.isLoading && state.items.isEmpty() -> FullScreenLoading()
                state.error != null -> ErrorMessage(state.error!!) {
                    viewModel.searchGarbage(searchText)
                }
                state.items.isEmpty() -> Placeholder(searchText.isNotEmpty())
                else -> GarbageList(state.items, listState, state.isLoading)
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
            text = if (hasSearched) "未找到分类信息，换个名称试试" else "请输入物品名称查询分类",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
private fun GarbageList(
    items: List<GarbageItem>,
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
            GarbageItemCard(item)
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
private fun GarbageItemCard(item: GarbageItem) {
    val typeColor = when (item.type) {
        0 -> Color(0xFF4CAF50) // 可回收 - 绿色
        1 -> Color(0xFFF44336) // 有害 - 红色
        2 -> Color(0xFFFF9800) // 厨余 - 橙色
        3 -> Color(0xFF9E9E9E) // 其他 - 灰色
        else -> Color(0xFF2196F3) // 默认 - 蓝色
    }

    val typeName = when (item.type) {
        0 -> "可回收垃圾"
        1 -> "有害垃圾"
        2 -> "厨余垃圾"
        3 -> "其他垃圾"
        else -> "未知分类"
    }

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
                    text = item.name,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )

                // 修复后的分类标签
                Surface(
                    color = typeColor.copy(alpha = 0.2f),
                    contentColor = typeColor,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = typeName,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.explain,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (item.contain.isNotBlank()) {
                Text(
                    text = "包含: ${item.contain}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (item.tip.isNotBlank()) {
                Text(
                    text = "投放提示: ${item.tip}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }

            if (item.aipre == 1) {
                Text(
                    text = "※ 此为智能预判结果，仅供参考",
                    style = MaterialTheme.typography.caption,
                    color = Color(0xFFFF5722),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}