// ComputerTerm.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

// ComputerTerm.kt
data class ComputerTermResponse(
    val code: Int,
    val msg: String,
    val result: ComputerTermItem?  // 修改为单个对象而非列表
)

// 删除ComputerTermResult类，直接使用ComputerTermItem
data class ComputerTermItem(
    val abbr: String,
    val type: String,
    val notes: String
)