package com.example.lifeservicesassistant.ui.theme.play

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 在 ErrorMessage.kt 中修改
@Composable
fun ErrorMessage(
    error: String,
    onRetry: (() -> Unit)? = null  // 可选的重试回调
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            modifier = Modifier.padding(16.dp)
        )
        
        onRetry?.let {
            Button(
                onClick = it,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("重试")
            }
        }
    }
}