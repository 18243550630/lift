package com.example.lifeservicesassistant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.lifeservicesassistant.logic.dao.UserPreferences
import com.example.lifeservicesassistant.ui.theme.healthy.BodyInfoScreen
import com.example.lifeservicesassistant.util.CommonTopBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DietRecommendationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

        // 从 SharedPreferences 获取用户信息
        val userInfo = UserPreferences.getUserInfo(this)

        // 获取身高和体重
        val height = userInfo.height
        val weight = userInfo.weight

        // 计算 BMI
        val bmi = if (height > 0 && weight > 0) {
            weight / (height * height) // BMI = 体重(kg) / 身高(m)^2
        } else {
            0f // 如果身高或体重无效，BMI 为 0
        }
        setContent {
            DietRecommendationScreen(bmi, onNavigateBack = {finish()})

        }
    }

}


@Composable
fun DietRecommendationScreen(bmi: Float, onNavigateBack: () -> Unit) {
    // 饮食推荐列表
    val dietRecommendationsLow = listOf(
        "早餐: 高蛋白饮食, 午餐: 增加碳水化合物, 晚餐: 清淡饮食",
        "早餐: 燕麦粥, 午餐: 红烧鸡胸肉, 晚餐: 烤蔬菜",
        "早餐: 全麦面包, 午餐: 三明治, 晚餐: 清炒西兰花"
    )

    val dietRecommendationsNormal = listOf(
        "早餐: 高纤维食物, 午餐: 高蛋白低脂肪, 晚餐: 低盐饮食",
        "早餐: 牛奶麦片, 午餐: 沙拉, 晚餐: 烤鱼",
        "早餐: 鸡蛋, 午餐: 牛肉意面, 晚餐: 清炒蔬菜"
    )

    val dietRecommendationsOverweight = listOf(
        "早餐: 低脂肪食物, 午餐: 多蔬菜少肉, 晚餐: 清淡少油",
        "早餐: 酸奶水果, 午餐: 鸡胸肉沙拉, 晚餐: 清蒸鱼",
        "早餐: 燕麦, 午餐: 烤鸡肉, 晚餐: 牛油果沙拉"
    )

    val dietRecommendationsObese = listOf(
        "早餐: 高蛋白低脂肪, 午餐: 多吃蔬菜, 晚餐: 高纤维饮食",
        "早餐: 烤蛋白, 午餐: 沙拉, 晚餐: 牛油果吐司",
        "早餐: 蛋白质饮料, 午餐: 绿叶沙拉, 晚餐: 烤鸡胸肉"
    )

    // 根据 BMI 选择对应的饮食推荐
    var recommendations by remember { mutableStateOf<String>("") }

    // 设置初始推荐
    LaunchedEffect(bmi) {
        recommendations = when {
            bmi < 18.5 -> {
                val message = "您的 BMI 指数较低，建议增加营养摄入。"
                val recommendation = dietRecommendationsLow.random()
                "$message\n推荐饮食: $recommendation"
            }
            bmi in 18.5..24.9 -> {
                val message = "您的 BMI 指数正常，建议均衡饮食。"
                val recommendation = dietRecommendationsNormal.random()
                "$message\n推荐饮食: $recommendation"
            }
            bmi in 25.0..29.9 -> {
                val message = "您的 BMI 指数偏高，建议减少脂肪摄入。"
                val recommendation = dietRecommendationsOverweight.random()
                "$message\n推荐饮食: $recommendation"
            }
            else -> {
                val message = "您的 BMI 指数较高，建议控制饮食和增加运动。"
                val recommendation = dietRecommendationsObese.random()
                "$message\n推荐饮食: $recommendation"
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "饮食推荐",
                onNavigateBack = onNavigateBack  // 这里可以添加返回按钮逻辑
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
            Text(
                text = "BMI: %.2f".format(bmi),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = recommendations,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // 更换推荐
                val newRecommendation = when {
                    bmi < 18.5 -> dietRecommendationsLow.random()
                    bmi in 18.5..24.9 -> dietRecommendationsNormal.random()
                    bmi in 25.0..29.9 -> dietRecommendationsOverweight.random()
                    else -> dietRecommendationsObese.random()
                }
                val message = when {
                    bmi < 18.5 -> "您的 BMI 指数较低，建议增加营养摄入。"
                    bmi in 18.5..24.9 -> "您的 BMI 指数正常，建议均衡饮食。"
                    bmi in 25.0..29.9 -> "您的 BMI 指数偏高，建议减少脂肪摄入。"
                    else -> "您的 BMI 指数较高，建议控制饮食和增加运动。"
                }
                recommendations = "$message\n推荐饮食: $newRecommendation"
            }) {
                Text("更换推荐")
            }
        }
    }
}





fun getDietRecommendationBasedOnBMI(bmi: Float): String {
    return when {
        bmi < 18.5 -> "您的 BMI 指数较低，建议增加营养摄入。\n早餐: 蛋白质丰富的食物\n午餐: 增加碳水化合物\n晚餐: 以清淡为主"
        bmi in 18.5..24.9 -> "您的 BMI 指数正常，建议均衡饮食。\n早餐: 高纤维食品\n午餐: 高蛋白低脂肪\n晚餐: 低盐饮食"
        bmi in 25.0..29.9 -> "您的 BMI 指数偏高，建议减少脂肪摄入。\n早餐: 低脂肪食物\n午餐: 多蔬菜少肉\n晚餐: 清淡少油"
        else -> "您的 BMI 指数较高，建议控制饮食和增加运动。\n早餐: 高蛋白低脂肪\n午餐: 多吃蔬菜\n晚餐: 清淡高纤维"
    }
}
