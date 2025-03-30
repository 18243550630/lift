package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.ui.theme.MyAppTheme
import com.example.lifeservicesassistant.R

class SynonymAntonymActivity : ComponentActivity() {
    private val viewModel: SynonymAntonymViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                SynonymAntonymScreen(
                    viewModel = viewModel,
                    apiKey ="ee5d3823a527577eee53438f2951d4d4",
                    onBackClick = { finish() }
                )
            }
        }
    }
}