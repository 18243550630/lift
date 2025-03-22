package com.example.lifeservicesassistant.ui.theme.event

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifeservicesassistant.ui.theme.event.EventViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventListScreen(viewModel: EventViewModel, navController: NavController) {
    val lists by viewModel.eventLists.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newListTitle by remember { mutableStateOf("") }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var listToDelete: EventList? by remember { mutableStateOf(null) }
    val dismissStates = remember { mutableStateMapOf<Long, DismissState>() }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { TopAppBar(title = { Text("事件清单") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加清单")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (lists.isEmpty()) {
                Text("没有事件清单", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn {
                    items(lists, key = { it.id }) { list ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = { dismissValue ->
                                if (dismissValue == DismissValue.DismissedToStart) {
                                    listToDelete = list
                                    showDeleteConfirmationDialog = true
                                    false // 阻止自动消失
                                } else {
                                    true
                                }
                            }
                        ).also { dismissStates[list.id] = it }

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(DismissDirection.EndToStart),
                            background = {
                                val alpha by animateFloatAsState(
                                    targetValue = if (dismissState.offset.value != 0f) 1f else 0f
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "删除",
                                        tint = Color.Red,
                                        modifier = Modifier.alpha(alpha)
                                    )
                                }
                            },
                            dismissContent = {
                                ListItem(
                                    headlineContent = { Text(list.title) },
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate("event_detail/${list.id}")
                                        }
                                        .padding(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // 删除确认对话框（增加状态重置逻辑）
    if (showDeleteConfirmationDialog && listToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除此事件清单吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        listToDelete?.let {
                            viewModel.deleteList(it)
                            dismissStates.remove(it.id)
                        }
                        showDeleteConfirmationDialog = false
                    }
                ) {
                    Text("确认删除")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteConfirmationDialog = false
                    listToDelete?.id?.let {
                        coroutineScope.launch {
                            dismissStates[it]?.reset()
                        }
                    }
                    listToDelete = null
                }) {
                    Text("取消")
                }
            }
        )
    }

    // 显示创建清单的对话框
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("新建事件清单") },
            text = {
                OutlinedTextField(
                    value = newListTitle,
                    onValueChange = { newListTitle = it },
                    label = { Text("清单名称") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newListTitle.isNotBlank()) {
                        viewModel.addList(newListTitle) { listId ->
                            showDialog = false
                            navController.navigate("event_detail/$listId") // 立即跳转
                        }
                        newListTitle = ""
                    }
                }) {
                    Text("创建")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

}
