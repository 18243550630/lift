// HotNewsActivity.kt
package com.example.lifeservicesassistant.ui.theme.play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class HotNewsActivity : ComponentActivity() {
    private val viewModel: HotNewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                HotNewsScreen(
                    viewModel = viewModel,
                    apiKey = getString(R.string.tianapi_key),
                    onBackClick = { finish() }
                )
            }
        }
        // 初始加载热搜数据
        viewModel.fetchHotNews(getString(R.string.tianapi_key))
    }
}