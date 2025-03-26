package com.example.lifeservicesassistant.ui.theme.news

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: NewsViewModel = viewModel()

    NavHost(navController = navController, startDestination = "newsList") {
        composable("newsList") {
            NewsListScreen(
                viewModel = viewModel,
                onNewsClick = { news ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("selectedNews", news)
                    navController.navigate("newsDetail")
                }
            )
        }
        composable("newsDetail") {
            NewsDetailScreen(navController = navController)
        }
    }
}
