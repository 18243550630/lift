// JudgmentQuestionActivity.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class JudgmentQuestionActivity : ComponentActivity() {
    private val viewModel: JudgmentQuestionViewModel by viewModels {
        JudgmentQuestionViewModelFactory(
            getString(R.string.tianapi_key),
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                JudgmentQuestionScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
    }
}