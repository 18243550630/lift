package com.example.lifeservicesassistant.ui.theme.healthy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.lifeservicesassistant.BodyInfoActivity
import com.example.lifeservicesassistant.DietRecommendationActivity
import com.example.lifeservicesassistant.HealthyFeetActivity
import com.example.lifeservicesassistant.MainActivity
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.SunnyWeatherApplication.Companion.context
import com.example.lifeservicesassistant.getDietRecommendationBasedOnBMI
import com.example.lifeservicesassistant.logic.dao.UserPreferences
import com.example.lifeservicesassistant.util.CommonTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(
    stepCount: Int,  // 步数
    dailyGoal: Int,  // 目标步数
    onNavigateBack: () -> Unit  // 返回操作的回调
) {
    val userInfo = UserPreferences.getUserInfo(context)
    val context = LocalContext.current  // 获取当前的 Context
    val weight = userInfo.weight
    // 计算进度
    val progress = stepCount / dailyGoal.toFloat()
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "健康运动",  // 顶部标题
                onNavigateBack = onNavigateBack  // 将返回操作传递给 CommonTopBar
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 步数卡片
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    val intent = Intent(context, HealthyFeetActivity::class.java)
                    context.startActivity(intent)
                },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 步数图标
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sport),  // 使用 painterResource 加载图片
                        contentDescription = "Steps",
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "今日步数", style = TextStyle(fontWeight = FontWeight.Bold))
                        Text(
                            text = "$stepCount/$dailyGoal 步",  // 显示步数和目标
                            style = TextStyle(fontWeight = FontWeight.Light)
                        )
                    }
                    // 进度条
                    CircularProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),  // 进度条根据进度显示
                        modifier = Modifier.size(40.dp),
                        strokeWidth = 4.dp
                    )
                    Text("完成进度：${(progress * 100).toInt()}%", style = TextStyle(fontWeight = FontWeight.Bold))
                }
            }

            // 显示完成进度

            // 身体信息卡片
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    // 跳转到 BodyInfoActivity
                    val intent = Intent(context, BodyInfoActivity::class.java)
                    context.startActivity(intent)
                },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 使用 painterResource 加载图标
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sport),
                        contentDescription = "Sleep",
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "身体信息", style = TextStyle(fontWeight = FontWeight.Bold))
                        Text(text = "${weight}Kg", style = TextStyle(fontWeight = FontWeight.Light))
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    // 跳转到 DietRecommendationActivity
                    val intent = Intent(context, DietRecommendationActivity::class.java)
                    context.startActivity(intent)
                },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 使用 painterResource 加载图标
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sport),
                        contentDescription = "Diet Recommendation",
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "推荐饮食", style = TextStyle(fontWeight = FontWeight.Bold))
                    }
                }
            }



            // 站立卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 使用 painterResource 加载图标
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sport),
                        contentDescription = "Standing",
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "运动计划", style = TextStyle(fontWeight = FontWeight.Bold))
                        Text(text = "", style = TextStyle(fontWeight = FontWeight.Light))
                    }
                }
            }

            // 血压卡片（示例占位符）
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8BBD0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 使用 painterResource 加载图标
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sport),
                        contentDescription = "Blood Pressure",
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "血压", style = TextStyle(fontWeight = FontWeight.Bold))
                        Text(text = "数据缺失", style = TextStyle(fontWeight = FontWeight.Light))
                    }
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun HealthScreenPreview() {
    HealthScreen(onNavigateBack = { */
/* 空的回调，不做任何操作 *//*
 })
}*/
