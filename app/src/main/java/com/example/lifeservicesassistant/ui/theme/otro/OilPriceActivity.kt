// OilPriceActivity.kt
package com.example.lifeservicesassistant.ui.theme.otro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class OilPriceActivity : ComponentActivity() {
    private val viewModel: OilPriceViewModel by viewModels {
        OilPriceViewModelFactory(
            getString(R.string.tianapi_key),
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                OilPriceScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() }
                )
            }
        }
        // 初始加载默认省份油价
        viewModel.fetchOilPrice("北京")
    }
}