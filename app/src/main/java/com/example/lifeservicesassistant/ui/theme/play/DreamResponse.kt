// DreamInterpretation.kt
package com.example.lifeservicesassistant.ui.theme.play

data class DreamResponse(
    val code: Int,
    val msg: String,
    val result: DreamResult?
)

data class DreamResult(
    val list: List<DreamItem>?
)

data class DreamItem(
    val id: Int,         // 数据ID
    val type: String,    // 类型(如"综合类")
    val title: String,   // 标题(如"扒火车")
    val result: String   // 解梦内容
)