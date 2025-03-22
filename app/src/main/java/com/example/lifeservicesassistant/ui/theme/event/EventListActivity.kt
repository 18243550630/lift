package com.example.lifeservicesassistant.ui.theme.event

import LifeServicesAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class EventListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeServicesAssistantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val viewModel: EventViewModel = viewModel(factory = EventViewModelFactory(context))

                    NavHost(navController = navController, startDestination = "event_list") {
                        composable("event_list") {
                            EventListScreen(viewModel, navController)
                        }
                        composable(
                            route = "event_detail/{listId}",
                            arguments = listOf(navArgument("listId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val listId = backStackEntry.arguments?.getLong("listId") ?: return@composable
                            EventDetailScreen(viewModel, listId, navController) // ✅ 传入 navController
                        }
                    }
                }
            }
        }
    }
}