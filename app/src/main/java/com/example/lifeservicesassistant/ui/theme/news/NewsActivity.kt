package com.example.lifeservicesassistant.ui.theme.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            /*  NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AppNavigation()
                }
            }
        }
    }*/
            NewsScreen()
        }
    }
}

@Composable
fun NewsAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}