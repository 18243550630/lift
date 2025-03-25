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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordBookDetailScreen(
    viewModel: PasswordViewModel,
    bookId: Long,
    navController: NavController
) {
    // 从ViewModel获取当前密码项列表
    val currentItems by viewModel.currentBookItemsState

    // 本地状态管理
    var newPasswordTitle by remember { mutableStateOf("") }
    var newPasswordContent by remember { mutableStateOf("") }

    // 当进入页面时加载对应密码本的数据
    LaunchedEffect(bookId) {
        viewModel.loadBookItems(bookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("密码本详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 添加新密码项的表单
            OutlinedTextField(
                value = newPasswordTitle,
                onValueChange = { newPasswordTitle = it },
                label = { Text("密码标题") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPasswordContent,
                onValueChange = { newPasswordContent = it },
                label = { Text("密码内容") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (newPasswordTitle.isNotBlank() && newPasswordContent.isNotBlank()) {
                        viewModel.addPasswordItem(
                            bookId = bookId,
                            title = newPasswordTitle,
                            password = newPasswordContent
                        )
                        newPasswordTitle = ""
                        newPasswordContent = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("添加密码")
            }

            // 密码项列表
            LazyColumn {
                items(currentItems, key = { it.id }) { passwordItem ->
                    PasswordItemRow(
                        passwordItem = passwordItem,
                        viewModel = viewModel,
                        bookId = bookId
                    )
                }
            }
        }
    }
}