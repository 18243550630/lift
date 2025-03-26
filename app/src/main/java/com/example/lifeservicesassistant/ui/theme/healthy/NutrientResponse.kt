package com.example.lifeservicesassistant.ui.theme.healthy

// NutrientResponse.kt
data class NutrientResponse(
    val code: Int,
    val msg: String,
    val result: NutrientResult?  // 改为嵌套对象
)

data class NutrientResult(
    val list: List<NutrientInfo>  // 接收数组数据
)

// NutrientInfo.kt
data class NutrientInfo(
    val name: String,            // 食物名称
    val rl: Double,              // 热量
    val dbz: Double,             // 蛋白质
    val zf: Double,              // 脂肪
    val shhf: Double,            // 碳水化合物
    val ys: Double,              // 膳食纤维
    val la: Double,              // 钠（原 las）
    val gai: Int,                // 钙
    val tei: Double,             // 铁
    // 其他字段按需添加...
)
