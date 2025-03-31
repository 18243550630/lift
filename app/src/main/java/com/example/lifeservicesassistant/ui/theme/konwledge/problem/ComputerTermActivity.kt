// ComputerTermActivity.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class ComputerTermActivity : ComponentActivity() {
    private val viewModel: ComputerTermViewModel by viewModels {
        ComputerTermViewModelFactory(
            getString(R.string.tianapi_key),
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                ComputerTermScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}