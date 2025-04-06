package com.example.lifeservicesassistant.ui.theme.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FavoriteFolderScreen(
    onFolderClick: (String) -> Unit
) {
    val context = LocalContext.current
    var folders by remember { mutableStateOf(NewsStorage.getFavoriteFolders(context).toSortedMap()) }
    var renameTarget by remember { mutableStateOf<String?>(null) }
    var newName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("我的收藏夹") })
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
                items(folders.keys.toList()) { folder ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = folder,
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onFolderClick(folder) }
                                )
                                TextButton(onClick = {
                                    renameTarget = folder
                                    newName = folder
                                }) {
                                    Text("重命名")
                                }
                            }
                            val count = NewsStorage.getFavoritesInFolder(context, folder).size
                            Text("$count 篇新闻")
                        }
                    }
                }
            }
        }
    }

    if (renameTarget != null) {
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            title = { Text("重命名收藏夹") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("新的收藏夹名称") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val foldersMap = NewsStorage.getFavoriteFolders(context)
                    val content = foldersMap.remove(renameTarget)
                    if (content != null && newName.isNotBlank()) {
                        foldersMap[newName] = content
                        NewsStorage.saveFavoriteFolders(context, foldersMap)
                        folders = foldersMap.toSortedMap()
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

@Composable
fun MultiSelectFolderDialog(
    context: android.content.Context,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val folders = remember { NewsStorage.getFavoriteFolders(context).keys.toList() }
    val selected = remember { mutableStateListOf<String>().apply { if (folders.isNotEmpty()) add(folders[0]) } }
    var newFolderName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择收藏夹 (可多选)") },
        text = {
            Column {
                folders.forEach { folder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selected.contains(folder)) selected.remove(folder)
                                else selected.add(folder)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = selected.contains(folder),
                            onCheckedChange = {
                                if (it) selected.add(folder) else selected.remove(folder)
                            }
                        )
                        Text(folder)
                    }
                }
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("新建收藏夹（可选）") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newFolderName.isNotBlank() && !folders.contains(newFolderName)) {
                    NewsStorage.createFavoriteFolder(context, newFolderName)
                    selected.add(newFolderName)
                }
                onConfirm(selected.toList())
                onDismiss()
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
