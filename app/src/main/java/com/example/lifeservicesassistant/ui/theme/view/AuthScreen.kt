package com.example.lifeservicesassistant.ui.theme.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifeservicesassistant.logic.model.User
import com.example.lifeservicesassistant.logic.model.UserDbHelper

@Composable
fun AuthScreen(navController: NavController, dbHelper: UserDbHelper) {
    var isLogin by remember { mutableStateOf(true) }
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "登录" else "注册",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = account,
            onValueChange = { account = it },
            label = { Text("账号") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (account.isBlank() || password.isBlank()) {
                    errorMessage = "请输入账号和密码"
                    return@Button
                }

                if (isLogin) {
                    val user = dbHelper.getUserByAccount(account)
                    if (user != null && user.password == password) {
                        // 登录成功
                        navController.navigate("profile/${user.id}")

                    } else {
                        errorMessage = "账号或密码错误"
                    }
                } else {
                    if (dbHelper.getUserByAccount(account) != null) {
                        errorMessage = "账号已存在"
                    } else {
                        val newUser = User(account = account, password = password)
                        val result = dbHelper.addUser(newUser)
                        if (result != -1L) {
                            errorMessage = "注册成功，请登录"
                            isLogin = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "登录" else "注册")
        }

        TextButton(
            onClick = { isLogin = !isLogin }
        ) {
            Text(
                text = if (isLogin) "没有账号？立即注册" else "已有账号？立即登录",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}