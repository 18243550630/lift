package com.example.lifeservicesassistant.ui.theme.password

import LifeServicesAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifeservicesassistant.ui.theme.password.PasswordListScreen
import com.example.lifeservicesassistant.ui.theme.password.PasswordViewModel
import com.example.lifeservicesassistant.ui.theme.password.PasswordBookDetailScreen

class PasswordManagementActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeServicesAssistantTheme {
                // Surface 是一个常用的 Composable，用来作为内容的背景
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // 获取 NavController
                    val navController = rememberNavController()

                    // 设置 NavHost
                    PasswordManagementNavigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun PasswordManagementNavigation(navController: NavHostController) {
    val viewModel: PasswordViewModel = viewModel()

    NavHost(navController = navController, startDestination = "password_list") {
        composable("password_list") {
            PasswordListScreen(viewModel = viewModel, navController = navController)
        }
        composable("create_password_book") {
            CreatePasswordBookScreen(viewModel = viewModel, navController = navController)
        }
        composable("password_details/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull() ?: 0L
            PasswordBookDetailScreen(viewModel = viewModel, bookId = bookId, navController = navController)
        }
    }
}