package com.example.lifeservicesassistant.ui.theme.healthy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme



class RecipeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitClient.createRecipeApiService()
        val apiKey = getString(R.string.tianapi_key)
        val repository = RecipeRepository(apiService, apiKey)
        val viewModel = RecipeViewModel(repository)
        setContent {
            MyAppTheme {
                RecipeScreen(viewModel = viewModel)
            }
        }
    }
}