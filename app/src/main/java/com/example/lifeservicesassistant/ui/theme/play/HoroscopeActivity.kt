package com.example.lifeservicesassistant.ui.theme.play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class HoroscopeActivity : ComponentActivity() {
    private val viewModel: HoroscopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                HoroscopeScreen(
                    viewModel = viewModel,
                    apiKey =  getString(R.string.tianapi_key),
                    onBackClick = { finish() }
                )
            }
        }
        // 初始加载今日运势
        viewModel.fetchHoroscope( getString(R.string.tianapi_key), "白羊座")
    }
}