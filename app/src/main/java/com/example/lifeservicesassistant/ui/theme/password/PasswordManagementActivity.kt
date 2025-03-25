package com.example.lifeservicesassistant.ui.theme.password

import LifeServicesAssistantTheme
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifeservicesassistant.ui.theme.password.PasswordListScreen
import com.example.lifeservicesassistant.ui.theme.password.PasswordViewModel
import com.example.lifeservicesassistant.ui.theme.password.PasswordBookDetailScreen

// PasswordManagementActivity.kt
class PasswordManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeServicesAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: PasswordViewModel = viewModel(
                        factory = PasswordViewModelFactory(application)
                    )
                    PasswordManagementNavigation(navController, viewModel)
                }
            }
        }
    }
}

// PasswordViewModelFactory.kt
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

// PasswordManagementNavigation.kt
@Composable
fun PasswordManagementNavigation(
    navController: NavHostController,
    viewModel: PasswordViewModel
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