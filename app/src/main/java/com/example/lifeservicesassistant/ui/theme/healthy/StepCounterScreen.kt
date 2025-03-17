package com.example.lifeservicesassistant.ui.theme.healthy

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lifeservicesassistant.util.CommonTopBar
import com.example.lifeservicesassistant.util.StepPreferences

@Composable
fun StepCounterScreen(
    stepCount: Int,
    onNavigateBack: () -> Unit,
    context: Context
) {
    // 使用记住目标步数，初始值从 SharedPreferences 获取
    var goalText by remember { mutableStateOf(TextFieldValue(StepPreferences.getGoalStep(context).toString())) }

    val dailyGoal = goalText.text.toIntOrNull() ?: 10000  // 如果没有获取到目标步数，则使用默认值 10000
    val caloriesBurned = stepCount * 0.04  // 计算消耗的卡路里
    val progress = stepCount / dailyGoal.toFloat()  // 计算进度

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "每日步数统计",
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 当前步数
            Text("当前步数：$stepCount", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // 消耗热量
            Text("消耗热量：${"%.2f".format(caloriesBurned)} 卡路里", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // 目标步数输入框
            Text("目标步数：", style = MaterialTheme.typography.titleMedium)
            TextField(
                value = goalText,
                onValueChange = {
                    goalText = it
                    it.text.toIntOrNull()?.let { newGoal ->
                        StepPreferences.saveGoalStep(context, newGoal) // 保存新的目标步数
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .width(200.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 进度条
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 完成进度
            Text("完成进度：${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        }
    }
}
