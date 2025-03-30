package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class QAActivity : ComponentActivity() {
    private val viewModel: QAViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                QAScreen(
                    viewModel = viewModel,
                    apiKey = getString(R.string.tianapi_key),
                    onBackClick = { finish() }
                )
            }
        }
        // 初始加载第一题
        viewModel.fetchQA(getString(R.string.tianapi_key))
    }
}