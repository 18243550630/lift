package com.example.lifeservicesassistant.ui.theme.healthy

import LifeServicesAssistantTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room

class NutrientActivity : ComponentActivity() {

    private lateinit var viewModel: NutrientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ViewModel（如果你用默认构造函数）
        viewModel = ViewModelProvider(this)[NutrientViewModel::class.java]

        // 如果你使用了 ViewModelFactory 注入数据库，请在此替换为对应逻辑

        setContent {
            LifeServicesAssistantTheme {
                NutrientScreen(viewModel = viewModel, apiKey = "ee5d3823a527577eee53438f2951d4d4",onBackClick = { finish() }) // 👈 替换为你的真实 APIKEY
            }
        }
    }
}
