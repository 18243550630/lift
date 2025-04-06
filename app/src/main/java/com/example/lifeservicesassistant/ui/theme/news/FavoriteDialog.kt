package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun FavoriteDialog(
    context: Context,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val existingFolders = remember { NewsStorage.getFavoriteFolders(context).keys.toList() }
    var selectedFolder by remember { mutableStateOf("") }
    var newFolderName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium, elevation = 8.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("选择收藏夹", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(existingFolders) { folder ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedFolder = folder
                            }) {
                            RadioButton(
                                selected = selectedFolder == folder,
                                onClick = { selectedFolder = folder }
                            )
                            Text(folder, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("新建收藏夹", style = MaterialTheme.typography.subtitle2)
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("收藏夹名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val folderToUse = when {
                            newFolderName.isNotBlank() -> {
                                NewsStorage.createFavoriteFolder(context, newFolderName)
                                newFolderName
                            }
                            selectedFolder.isNotBlank() -> selectedFolder
                            else -> null
                        }
                        folderToUse?.let { onConfirm(it) }
                        onDismiss()
                    }) {
                        Text("确认")
                    }
                }
            }
        }
    }
}
