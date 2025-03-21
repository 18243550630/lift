package com.example.lifeservicesassistant.ui.theme.note

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Date
import java.util.Locale

import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.ExperimentalMaterialApi


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteViewModel,
    navController: NavController
) {
    val notes by viewModel.notes.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchBar(
                query = searchText,
                onQueryChange = { viewModel.onSearchTextChange(it) },
                onSearch = {},
                active = searchActive,
                onActiveChange = { searchActive = it },
                modifier = Modifier.padding(8.dp),
                placeholder = { Text("搜索笔记") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            ) {
                // 你也可以在这里放搜索建议内容
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("edit")
            }) {
                Icon(Icons.Default.Add, contentDescription = "添加笔记")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(notes, key = { it.id }) { note ->
                NoteItem(
                    note = note,
                    onDelete = { viewModel.deleteNote(it) },
                    onClick = { navController.navigate("edit/${note.id}") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NoteItem(
    note: Note,
    onDelete: (Note) -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete(note)
            }
            true
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.Red)
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = onClick)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(note.title, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = note.content.take(100),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(note.updatedTime))
                    )
                }
            }
        }
    )
}