package com.example.lifeservicesassistant.ui.theme.healthy

import MedicineScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class MedicineActivity : ComponentActivity() {
    private val viewModel: MedicineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MyAppTheme {
                MedicineScreen(
                    viewModel = viewModel,
                    apiKey = "ee5d3823a527577eee53438f2951d4d4",
                )
            }
        }
    }
}