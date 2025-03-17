package com.example.lifeservicesassistant.ui.theme.otro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.lifeservicesassistant.logic.model.UserDbHelper
import com.example.lifeservicesassistant.ui.theme.view.AuthScreen

class LoginActivity : ComponentActivity() {
    private lateinit var dbHelper: UserDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = UserDbHelper(this)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var navController = rememberNavController()
                    AuthScreen(navController = navController, dbHelper = dbHelper)
                }
            }
        }
    }
}