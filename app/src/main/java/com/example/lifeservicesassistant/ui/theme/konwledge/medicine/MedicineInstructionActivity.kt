package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class MedicineInstructionActivity : ComponentActivity() {
    private val viewModel: MedicineInstructionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MedicineInstructionScreen(
                    viewModel = viewModel,
                    apiKey =  getString(R.string.tianapi_key),
                    onBackClick = { finish() }
                )
            }
        }
    }
}