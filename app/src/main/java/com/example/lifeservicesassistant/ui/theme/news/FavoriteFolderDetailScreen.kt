package com.example.lifeservicesassistant.ui.theme.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun FavoriteFolderDetailScreen(
    folderName: String,
    onBack: () -> Unit,
    onNewsClick: (News) -> Unit
) {
    val context = LocalContext.current

    // 使用 LaunchedEffect 确保数据更新
    var folderMap by remember { mutableStateOf(NewsStorage.getFavoriteFolders(context).toMutableMap()) }
    var selectedItems by remember { mutableStateOf(setOf<News>()) }
    var showMoveDialog by remember { mutableStateOf(false) }
    var isManageMode by remember { mutableStateOf(false) }

    val newsList = folderMap[folderName] ?: emptyList()

    // 使用 LaunchedEffect 确保每次进入收藏夹时都加载数据
    LaunchedEffect(folderName) {
        folderMap = NewsStorage.getFavoriteFolders(context).toMutableMap()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(folderName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (isManageMode) {
                        if (selectedItems.isNotEmpty()) {
                            IconButton(onClick = { showMoveDialog = true }) {
                                Icon(Icons.Default.MoveToInbox, contentDescription = "移动")
                            }
                            IconButton(onClick = {
                                val newList = newsList.filterNot { selectedItems.contains(it) }
                                folderMap[folderName] = newList.toMutableList()
                                NewsStorage.saveFavoriteFolders(context, folderMap)
                                selectedItems = emptySet()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除选中")
                            }
                        }
                        IconButton(onClick = {
                            isManageMode = false
                            selectedItems = emptySet()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "退出管理")
                        }
                    } else {
                        IconButton(onClick = {
                            isManageMode = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "管理")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (newsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无新闻")
            }
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            ) {
                items(newsList) { news ->
                    val isSelected = selectedItems.contains(news)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                if (isManageMode) {
                                    selectedItems = if (isSelected) selectedItems - news else selectedItems + news
                                } else {
                                    onNewsClick(news)
                                }
                            },
                        elevation = 4.dp
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            if (!news.urlToImage.isNullOrEmpty()) {
                                AsyncImage(
                                    model = news.urlToImage,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 8.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = news.title,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "${news.source} · ${news.publishedAt.formatDate()}",
                                    style = MaterialTheme.typography.caption,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            if (isManageMode) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        selectedItems = if (it) selectedItems + news else selectedItems - news
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showMoveDialog && selectedItems.isNotEmpty()) {
            MultiSelectFolderDialog(
                context = context,
                onDismiss = { showMoveDialog = false },
                onConfirm = { selectedFolders ->
                    val updatedMap = folderMap.toMutableMap()

                    selectedFolders.forEach { folder ->
                        val list = updatedMap[folder] ?: mutableListOf()
                        selectedItems.forEach { news ->
                            if (!list.any { it.id == news.id }) {
                                list.add(news)
                            }
                        }
                        updatedMap[folder] = list
                    }

                    // 从当前收藏夹中移除已移动的
                    val filtered = newsList.filterNot { selectedItems.contains(it) }
                    updatedMap[folderName] = filtered.toMutableList()

                    NewsStorage.saveFavoriteFolders(context, updatedMap)
                    folderMap = updatedMap
                    selectedItems = emptySet()
                    showMoveDialog = false
                    isManageMode = false
                }
            )
        }
    }
}

