package com.example.lifeservicesassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lifeservicesassistant.ui.theme.MyAppTheme
import com.example.lifeservicesassistant.ui.theme.mianview.AppScreen

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 使用 Material3 主题包裹你的界面
            MyAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // 调用你创建的 AppScreen 作为主页面
                    AppScreen()
                }
            }
        }
    }
}
