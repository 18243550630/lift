package com.example.lifeservicesassistant.ui.theme.password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBookDetailScreen(viewModel: PasswordViewModel, bookId: Long, navController: NavController) {
    val book = viewModel.passwordBooks.find { it.id == bookId }
    var newPasswordTitle by remember { mutableStateOf("") }
    var newPasswordContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "密码本详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Text("保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // 添加密码项
            OutlinedTextField(
                value = newPasswordTitle,
                onValueChange = { newPasswordTitle = it },
                label = { Text("密码标题") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPasswordContent,
                onValueChange = { newPasswordContent = it },
                label = { Text("密码内容") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (newPasswordTitle.isNotBlank() && newPasswordContent.isNotBlank()) {
                        viewModel.addPasswordItem(bookId, newPasswordTitle, newPasswordContent)
                        newPasswordTitle = ""
                        newPasswordContent = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("添加密码")
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(book?.passwords ?: emptyList(), key = { it.id }) { passwordItem ->
                    PasswordItemRow(passwordItem, viewModel, bookId)
                }
            }
        }
    }
}
