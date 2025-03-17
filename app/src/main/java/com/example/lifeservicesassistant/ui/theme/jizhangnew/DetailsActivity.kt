package com.example.lifeservicesassistant.ui.theme.jizhangnew

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class DetailsActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用 setContent 来显示 Compose 界面
        setContent {
            MaterialTheme {
                // 创建 NavController 用于导航
                val navController = rememberNavController()

                // 使用 Scaffold 来承载整个界面
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("明细") },
                            actions = {
                                IconButton(onClick = {
                                    // 点击新建按钮，返回记录界面
                                    navController.navigate("record_screen")
                                }) {
                                    Text("新建")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    // 设置 NavHost 来处理页面跳转
                    NavHost(
                        navController = navController,
                        startDestination = "details_screen", // 设置开始界面
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("details_screen") { DetailsScreen(navController = navController) }
                    }
                }
            }
        }
    }
}
