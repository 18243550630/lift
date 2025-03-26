package com.example.lifeservicesassistant.ui.theme.healthy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class HealthTipActivity : ComponentActivity() {

    private val viewModel: HealthViewModel by viewModels()
    private val apiKey = "ee5d3823a527577eee53438f2951d4d4" // 👈 请替换为你自己的天行API KEY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    HealthTipScreen(viewModel = viewModel, apiKey = apiKey)
                }
            }
        }
    }
}
