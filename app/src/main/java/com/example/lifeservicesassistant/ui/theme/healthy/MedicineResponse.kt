package com.example.lifeservicesassistant.ui.theme.healthy

data class MedicineResponse(
    val code: Int,
    val msg: String?,
    val result: MedicineResult?
)

data class MedicineResult(
    val list: List<MedicineItem>?  // 注意现在是数组结构
)

data class MedicineItem(
    val title: String,
    val content: String
)
// 解析后的结构化数据（可选）
data class MedicineInfo(
    val name: String,      // 中文名
    val category: String,  // 类别
    val englishName: String, // 英文名
    val alias: String,     // 别名
    val source: String,    // 来源
    val properties: String, // 性味
    val morphology: String, // 植物形态
    val habitat: String,   // 生长地
    val chemistry: String, // 化学成分
    val effects: String    // 功能主治
)