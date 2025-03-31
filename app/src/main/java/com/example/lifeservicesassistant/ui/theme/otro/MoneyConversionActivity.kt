// MoneyConversionActivity.kt
package com.example.lifeservicesassistant.ui.theme.otro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class MoneyConversionActivity : ComponentActivity() {
    private val viewModel: MoneyConversionViewModel by viewModels {
        MoneyConversionViewModelFactory(
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MoneyConversionScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}