package com.example.lifeservicesassistant.ui.theme.note

import LifeServicesAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext

class NoteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeServicesAssistantTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current
                    val navController = rememberNavController()
                    val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(context))

                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") {
                            NoteListScreen(viewModel, navController)
                        }
                        composable("edit") {
                            EditNoteScreen(noteId = null, viewModel = viewModel, navController = navController)
                        }
                        composable("edit/{noteId}") { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull()
                            EditNoteScreen(noteId = noteId, viewModel = viewModel, navController = navController)
                        }
                        composable("category_management") {
                            CategoryManagementScreen(viewModel, navController)
                        }
                    }
                }
            }
        }
    }
}
