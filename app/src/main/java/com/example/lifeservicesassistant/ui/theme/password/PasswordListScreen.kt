package com.example.lifeservicesassistant.ui.theme.password

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.lazy.items // 关键导入
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PasswordListScreen(viewModel: PasswordViewModel, navController: NavController) {
    val passwordBooks by viewModel.passwordBooksState

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("密码管理") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("create_password_book")
            }) {
                Icon(Icons.Default.Add, contentDescription = "创建密码本")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            if (passwordBooks.isEmpty()) {
                item {
                    Text(
                        text = "暂无密码本，请点击右下角按钮添加",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(passwordBooks, key = { it.id }) { book ->
                    ListItem(
                        text = { Text(book.title) },
                        modifier = Modifier
                            .clickable {
                                navController.navigate("password_details/${book.id}")
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}