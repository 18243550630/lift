package com.example.lifeservicesassistant.ui.theme.password

import LifeServicesAssistantTheme
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class PasswordManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeServicesAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PasswordManagementApp()
                }
            }
        }
    }
}

@Composable
fun PasswordManagementApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val viewModel: PasswordViewModel = viewModel(
        factory = PasswordViewModelFactory(context.applicationContext as Application)
    )
    val securityPrefs = remember { SecurityPrefs(context) }

    var appState by remember { mutableStateOf<AppState>(AppState.Loading) }

    LaunchedEffect(securityPrefs) {
        appState = if (securityPrefs.isFirstTime()) {
            AppState.InitialSetup
        } else {
            AppState.Login
        }
    }

    when (appState) {
        AppState.Loading -> {
            // 可以显示加载动画
        }
        AppState.InitialSetup -> {
            InitialSetupScreen(
                onSetupComplete = { appState = AppState.MainApp },
                securityPrefs = securityPrefs
            )
        }
        AppState.Login -> {
            LoginScreen(
                securityPrefs = securityPrefs,
                onLoginSuccess = { appState = AppState.MainApp },
                onResetToSetup = { appState = AppState.InitialSetup }
            )
        }
        AppState.MainApp -> {
            MainAppNavigation(
                navController = navController,
                viewModel = viewModel,
                securityPrefs = securityPrefs
            )
        }
    }
}

@Composable
fun MainAppNavigation(
    navController: NavHostController,
    viewModel: PasswordViewModel,
    securityPrefs: SecurityPrefs
) {
    NavHost(
        navController = navController,
        startDestination = "password_list"
    ) {
        composable("password_list") {
            PasswordListScreen(viewModel = viewModel, navController = navController)
        }
        composable("create_password_book") {
            CreatePasswordBookScreen(viewModel = viewModel, navController = navController)
        }
        composable("password_details/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull() ?: 0L
            viewModel.loadBookItems(bookId)
            PasswordBookDetailScreen(
                viewModel = viewModel,
                bookId = bookId,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialSetupScreen(
    onSetupComplete: () -> Unit,
    securityPrefs: SecurityPrefs
) {
    var phoneNumber by remember { mutableStateOf("") }
    var viewPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("首次使用设置", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("手机号码") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewPassword,
            onValueChange = { viewPassword = it },
            label = { Text("设置4位查看密码(数字+字母)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("确认查看密码") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (phoneNumber.isEmpty() || viewPassword.isEmpty() || confirmPassword.isEmpty()) {
                    errorMessage = "所有字段都必须填写"
                } else if (viewPassword != confirmPassword) {
                    errorMessage = "两次输入的密码不一致"
                } else if (viewPassword.length != 4 || !viewPassword.any { it.isDigit() } || !viewPassword.any { it.isLetter() }) {
                    errorMessage = "密码必须是4位数字和字母组合"
                } else {
                    securityPrefs.savePhoneNumber(phoneNumber)
                    securityPrefs.saveViewPassword(viewPassword)
                    onSetupComplete()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存设置")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    securityPrefs: SecurityPrefs,
    onLoginSuccess: () -> Unit,
    onResetToSetup: () -> Unit
) {
    var enteredPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showResetOption by remember { mutableStateOf(false) }
    var resetPhoneNumber by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("请输入查看密码", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = enteredPassword,
            onValueChange = { enteredPassword = it },
            label = { Text("4位查看密码") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (enteredPassword == securityPrefs.getViewPassword()) {
                    onLoginSuccess()
                } else {
                    errorMessage = "密码错误"
                    if (errorMessage.count { it == '误' } > 2) {
                        showResetOption = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("确认")
        }

        if (showResetOption) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("忘记密码？", style = MaterialTheme.typography.bodySmall)

            OutlinedTextField(
                value = resetPhoneNumber,
                onValueChange = { resetPhoneNumber = it },
                label = { Text("输入注册手机号") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (resetPhoneNumber == securityPrefs.getPhoneNumber()) {
                        securityPrefs.saveViewPassword("")
                        onResetToSetup()
                    } else {
                        errorMessage = "手机号不匹配"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重置密码")
            }
        }
    }
}

sealed class AppState {
    object Loading : AppState()
    object InitialSetup : AppState()
    object Login : AppState()
    object MainApp : AppState()
}

class PasswordViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PasswordViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}