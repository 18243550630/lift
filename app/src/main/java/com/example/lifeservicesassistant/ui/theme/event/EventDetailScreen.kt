package com.example.lifeservicesassistant.ui.theme.event

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifeservicesassistant.ui.theme.event.EventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun EventDetailScreen(viewModel: EventViewModel, listId: Long, navController: NavController) {
    val events by viewModel.eventItems.collectAsState()
    val listTitle = viewModel.eventLists.collectAsState().value.find { it.id == listId }?.title ?: "事件清单"

    var selectedItems by remember { mutableStateOf(setOf<Long>()) }
    var newEventTitle by remember { mutableStateOf("") }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var eventToDelete: EventItem? by remember { mutableStateOf(null) }
    val dismissStates = remember { mutableStateMapOf<Long, DismissState>() }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(listId) {
        viewModel.setCurrentListId(listId)
    }

    if (showDeleteConfirmationDialog && eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除此事件项吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        eventToDelete?.let {
                            viewModel.deleteEvent(it)
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
                    eventToDelete?.id?.let {
                        // 使用协程启动 dismissState 的 reset 操作
                        coroutineScope.launch {
                            dismissStates[it]?.reset()
                        }
                    }
                    eventToDelete = null
                }) {
                    Text("取消")
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            val completed = events.count { it.isCompleted }
            val total = events.size
            Text("✔ 已完成 $completed / $total", modifier = Modifier.padding(bottom = 8.dp))

            LazyColumn {
                items(events, key = { it.id }) { event ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart) {
                                eventToDelete = event
                                showDeleteConfirmationDialog = true
                                false // 阻止自动状态变化
                            } else false
                        }
                    ).also { dismissStates[event.id] = it }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        background = {
                            val alpha by animateFloatAsState(
                                if (dismissState.offset.value != 0f) 1f else 0f
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
                            val eventTextColor by animateColorAsState(
                                if (event.isCompleted) Color(0xFF4CAF50) else Color.Black
                            )

                            Text(
                                text = event.eventTitle,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleEventCompleted(event) }
                                    .padding(12.dp)
                                    .animateContentSize(),
                                style = if (event.isCompleted) TextStyle(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = eventTextColor
                                ) else LocalTextStyle.current
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = newEventTitle,
                onValueChange = { newEventTitle = it },
                label = { Text("添加事件") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (newEventTitle.isNotBlank()) {
                        viewModel.addEvent(listId, newEventTitle)
                        newEventTitle = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("添加")
            }
        }
    }
}