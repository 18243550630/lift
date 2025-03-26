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
    // 基础信息
    val name: String,        // 食品名称
    val type: String,        // 食品种类
    val isExpanded: Boolean = false,

    // 宏量营养素
    val rl: Float,           // 热量(大卡)
    val dbz: Float,          // 蛋白质(g)
    val zf: Float,           // 脂肪(g)
    val shhf: Float,         // 碳水化合物(g)
    val ssxw: Float,         // 膳食纤维(g)
    val dgc: Float,          // 胆固醇(mg)

    // 维生素
    val las: Float,          // 硫胺素(mg)
    val su: Float,           // 核黄素(mg)
    val ys: Float,           // 烟酸(mg)
    val wsfc: Float,         // 维生素C(mg)
    val wsse: Float,         // 维生素E(mg)
    val wssa: Float,         // 维生素A(μg)
    val lb: Float,           // 胡萝卜素(μg)

    // 矿物质
    val gai: Float,          // 钙(mg)
    val tei: Float,          // 铁(mg)
    val xin: Float,          // 锌(mg)
    val tong: Float,         // 铜(mg)
    val meng: Float,         // 锰(mg)
    val jia: Float,          // 钾(mg)
    val la: Float,           // 钠(mg)
    val ling: Float,         // 磷(mg)
    val xi: Float,           // 硒(μg)
    val mei: Float,          // 镁(mg)

    // 其他
    val shc: Float           // 视黄醇当量(μg)
)
