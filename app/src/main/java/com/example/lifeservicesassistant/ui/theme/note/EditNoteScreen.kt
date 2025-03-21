package com.example.lifeservicesassistant.ui.theme.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val categoryState = remember { mutableStateOf<String?>(null) }

    // ✅ 使用 ViewModel 里的 categories，而不是固定列表
    val categories by viewModel.categories.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        noteId?.let { id ->
            val note = viewModel.notes.firstOrNull()?.find { it.id == id }
            note?.let {
                titleState.value = it.title
                contentState.value = it.content
                categoryState.value = it.category
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
                                content = contentState.value,
                                category = categoryState.value
                            )
                            if (noteId == null) {
                                viewModel.addNote(titleState.value, contentState.value, categoryState.value)
                            } else {
                                viewModel.updateNote(note)
                            }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
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

            // ✅ 分类选择按钮
            Button(onClick = { showCategoryDialog = true }) {
                Text(categoryState.value ?: "选择分类")
            }

            // ✅ 分类选择弹窗
            if (showCategoryDialog) {
                AlertDialog(
                    onDismissRequest = { showCategoryDialog = false },
                    title = { Text("选择分类") },
                    text = {
                        Column {
                            categories.forEach { category ->
                                Text(
                                    text = category,
                                    modifier = Modifier
                                        .clickable {
                                            categoryState.value = category
                                            showCategoryDialog = false
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = { }
                )
            }
        }
    }
}
