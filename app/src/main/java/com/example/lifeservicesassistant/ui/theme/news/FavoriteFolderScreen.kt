package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController

@Composable
fun FavoriteFolderScreen(
    navController: NavHostController,
    onFolderClick: (String) -> Unit
) {
    val context = LocalContext.current
    var folders by remember { mutableStateOf<Map<String, MutableList<News>>>(emptyMap()) }
    var folderOrder by remember { mutableStateOf<List<String>>(emptyList()) }
    var showManageDialog by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) } // 用于触发界面更新

    LaunchedEffect(refreshKey) {
        val result = NewsStorage.getFavoriteFolders(context).toMutableMap()
        folders = result
        folderOrder = result.keys.toList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏夹") },
                actions = {
                    IconButton(onClick = { showManageDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "管理")
                    }
                }
            )
        }
    ) { padding ->
        if (folders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无收藏夹，请先收藏新闻")
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(folderOrder) { _, folder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onFolderClick(folder) },
                        elevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = folder,
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold
                                )
                                val count = folders[folder]?.size ?: 0
                                Text("$count 篇新闻")
                            }
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colors.primary)
                        }
                    }
                }
            }
        }
    }

    if (showManageDialog) {
        FavoriteFolderManager(
            context = context,
            folders = folders.toMutableMap(),
            folderOrder = folderOrder.toMutableList(),
            onDismiss = { showManageDialog = false },
            onFoldersUpdated = { updatedFolders, updatedOrder ->
                NewsStorage.saveFavoriteFolders(context, updatedFolders)
                refreshKey++ // 强制刷新
                showManageDialog = false
            }
        )
    }
}

@Composable
fun FavoriteFolderManager(
    context: Context,
    folders: MutableMap<String, MutableList<News>>,
    folderOrder: MutableList<String>,
    onDismiss: () -> Unit,
    onFoldersUpdated: (MutableMap<String, MutableList<News>>, MutableList<String>) -> Unit
) {
    var folderMap by remember { mutableStateOf(folders.toMutableMap()) }
    var order by remember { mutableStateOf(folderOrder.toMutableList()) }
    var newFolderName by remember { mutableStateOf("") }
    var renameTarget by remember { mutableStateOf<String?>(null) }
    var renameValue by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf<String?>(null) } // 用于删除确认

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("管理收藏夹", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    itemsIndexed(order) { index, folder ->
                        val count = folderMap[folder]?.size ?: 0
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            elevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(folder, fontWeight = FontWeight.Bold)
                                    Text("$count 篇")
                                }
                                Row {
                                    // 重命名按钮
                                    IconButton(onClick = {
                                        renameTarget = folder
                                        renameValue = folder
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "重命名")
                                    }

                                    // 删除按钮
                                    IconButton(onClick = {
                                        showDeleteConfirmDialog = folder
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "删除")
                                    }

                                    // 上移按钮
                                    IconButton(enabled = index > 0, onClick = {
                                        val temp = order[index - 1]
                                        order[index - 1] = order[index]
                                        order[index] = temp
                                        // 保存排序后的数据
                                        NewsStorage.saveFavoriteFolders(context, folderMap)
                                    }) {
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "上移")
                                    }

                                    // 下移按钮
                                    IconButton(enabled = index < order.lastIndex, onClick = {
                                        val temp = order[index + 1]
                                        order[index + 1] = order[index]
                                        order[index] = temp
                                        // 保存排序后的数据
                                        NewsStorage.saveFavoriteFolders(context, folderMap)
                                    }) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "下移")
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("新建收藏夹") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank() && !folderMap.containsKey(newFolderName)) {
                            folderMap[newFolderName] = mutableListOf()
                            order.add(newFolderName)
                            newFolderName = ""
                            // 保存新建后的数据
                            NewsStorage.saveFavoriteFolders(context, folderMap)
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("添加")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        onFoldersUpdated(folderMap, order)
                    }) {
                        Text("保存")
                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个收藏夹吗？") },
            confirmButton = {
                TextButton(onClick = {
                    folderMap.remove(showDeleteConfirmDialog)
                    order.remove(showDeleteConfirmDialog)
                    // 删除后的数据保存
                    NewsStorage.saveFavoriteFolders(context, folderMap)
                    showDeleteConfirmDialog = null
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("取消")
                }
            }
        )
    }

    if (renameTarget != null) {
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("重命名收藏夹") },
            text = {
                OutlinedTextField(
                    value = renameValue,
                    onValueChange = { renameValue = it },
                    label = { Text("新的名称") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val content = folderMap.remove(renameTarget)
                    if (content != null && renameValue.isNotBlank()) {
                        folderMap[renameValue] = content
                        order = order.map { if (it == renameTarget) renameValue else it }.toMutableList()
                        // 保存重命名后的数据
                        NewsStorage.saveFavoriteFolders(context, folderMap)
                    }
                    renameTarget = null
                }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("取消")
                }
            }
        )
    }
}

