// GarbageClassification.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class GarbageResponse(
    val code: Int,
    val msg: String,
    val result: GarbageResult?
)

data class GarbageResult(
    val list: List<GarbageItem>?
)

data class GarbageItem(
    val name: String,    // 废弃物名称
    val type: Int,       // 0可回收、1有害、2厨余(湿)、3其他(干)
    val aipre: Int,      // 0正常结果、1预判结果
    val explain: String, // 分类解释
    val contain: String, // 包含类型
    val tip: String      // 投放提示
)