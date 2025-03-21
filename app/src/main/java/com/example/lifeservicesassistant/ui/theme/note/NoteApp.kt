package com.example.lifeservicesassistant.ui.theme.note

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NoteApp() {

    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(context))
    
    NavHost(navController = navController, startDestination = "list") {
        composable("list") { 
            NoteListScreen(viewModel, navController) 
        }
        composable("edit") { 
            EditNoteScreen(null, viewModel, navController) 
        }
        composable("edit/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull()
            EditNoteScreen(noteId, viewModel, navController)
        }
        composable("category_management") {
            CategoryManagementScreen(viewModel, navController)
        }
    }
}