package com.example.lifeservicesassistant.ui.theme.note

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NoteViewModel,
    navController: NavController
) {
    val notes by viewModel.notes.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var searchActive by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    query = searchText,
                    onQueryChange = { viewModel.onSearchTextChange(it) },
                    onSearch = {},
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    modifier = Modifier.padding(8.dp),
                    placeholder = { Text("搜索笔记") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )

                {
                    // 你也可以在这里放搜索建议内容
                }

                CategoryNavigation(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.updateSelectedCategory(if (it == "全部") null else it) },
                    onAddCategoryClicked = { navController.navigate("category_management") }
                )
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


@Composable
fun CategoryNavigation(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onAddCategoryClicked: () -> Unit
) {
    val sortedCategories = listOf("全部") + categories.filter { it != "全部" } // ✅ 确保 "全部" 始终在第一个

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow {
            items(sortedCategories) { category ->
                val isSelected = selectedCategory == category || (category == "全部" && selectedCategory == null)
                val backgroundColor = if (isSelected) Color.LightGray else Color.White
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(backgroundColor, MaterialTheme.shapes.medium)
                        .clickable { onCategorySelected(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(category, color = if (isSelected) Color.Black else Color.Gray)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        // 添加分类按钮
        IconButton(onClick = { onAddCategoryClicked() }) {
            Icon(Icons.Default.Add, contentDescription = "添加分类", tint = Color.Gray)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(viewModel: NoteViewModel, navController: NavController) {
    val categories by viewModel.categories.collectAsState()
    var newCategory by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("文件夹") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            LazyColumn {
                items(categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(category)
                        if (category != "全部") {
                            IconButton(onClick = { viewModel.removeCategory(category) }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newCategory,
                onValueChange = { newCategory = it },
                label = { Text("新建文件夹") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (newCategory.isNotBlank() && !categories.contains(newCategory)) {
                        viewModel.addCategory(newCategory)
                        newCategory = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("新建文件夹")
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