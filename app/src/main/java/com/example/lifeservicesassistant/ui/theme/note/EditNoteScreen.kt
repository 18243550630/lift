package com.example.lifeservicesassistant.ui.theme.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Long?,
    viewModel: NoteViewModel,
    navController: NavController
) {
    val titleState = remember { mutableStateOf("") }
    val contentState = remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        noteId?.let { id ->
            val note = viewModel.notes.firstOrNull()?.find { it.id == id }
            note?.let {
                titleState.value = it.title
                contentState.value = it.content
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null) "新建笔记" else "编辑笔记") },
                actions = {
                    IconButton(onClick = {
                        if (titleState.value.isNotBlank()) {
                            val note = Note(
                                id = noteId ?: 0,
                                title = titleState.value,
                                content = contentState.value
                            )
                            if (noteId == null) {
                                viewModel.addNote(titleState.value, contentState.value)
                            } else {
                                viewModel.updateNote(note)
                            }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Save, "保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = titleState.value,
                onValueChange = { titleState.value = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = contentState.value,
                onValueChange = { contentState.value = it },
                label = { Text("内容") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}