package com.example.lifeservicesassistant.ui.theme.view

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifeservicesassistant.MainActivity
import com.example.lifeservicesassistant.logic.model.User
import com.example.lifeservicesassistant.logic.model.UserDbHelper


@Composable
fun AuthScreen(navController: NavController, dbHelper: UserDbHelper) {
    val context = LocalContext.current
    val authPreferences = remember { AuthPreferences(context) }

    var isLogin by remember { mutableStateOf(true) }
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 自动填充保存的凭证
    LaunchedEffect(Unit) {
        authPreferences.getAccount()?.let { savedAccount ->
            account = savedAccount
            if (rememberMe) {
                authPreferences.getPassword()?.let { savedPassword ->
                    password = savedPassword
                }
            }
        }
    }

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
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (showPassword) "隐藏密码" else "显示密码"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it }
            )
            Text(
                text = "记住我",
                modifier = Modifier.clickable { rememberMe = !rememberMe }
            )
        }

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
                try {
                    if (account.isBlank() || password.isBlank()) {
                        errorMessage = "请输入账号和密码"
                        return@Button
                    }

                    if (isLogin) {
                        val user = dbHelper.getUserByAccount(account) ?: run {
                            errorMessage = "账号不存在"
                            return@Button
                        }

                        if (user.password != password) {
                            errorMessage = "密码错误"
                            return@Button
                        }

                        // 登录成功处理
                        if (rememberMe) {
                            authPreferences.saveCredentials(account, password)
                        } else {
                            authPreferences.clearCredentials()
                        }

                        // 直接跳转到MainActivity
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    } else {
                        // 注册逻辑
                        if (dbHelper.getUserByAccount(account) != null) {
                            errorMessage = "账号已存在"
                            return@Button
                        }

                        val newUser = User(
                            account = account,
                            password = password,
                            username = account, // 默认用户名设为账号
                            registerTime = System.currentTimeMillis()
                        )

                        val userId = dbHelper.addUser(newUser)
                        if (userId == -1L) {
                            errorMessage = "注册失败，请重试"
                            return@Button
                        }

                        if (rememberMe) {
                            authPreferences.saveCredentials(account, password)
                        }
                        errorMessage = "注册成功，请登录"
                        isLogin = true
                        account = ""
                        password = ""
                    }
                } catch (e: Exception) {
                    errorMessage = "操作失败: ${e.localizedMessage}"
                    Log.e("AuthScreen", "Authentication error", e)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "登录" else "注册")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                isLogin = !isLogin
                errorMessage = null
            }
        ) {
            Text(
                text = if (isLogin) "没有账号？立即注册" else "已有账号？立即登录",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}