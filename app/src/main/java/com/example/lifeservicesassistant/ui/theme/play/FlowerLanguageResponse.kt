// FlowerLanguage.kt
package com.example.lifeservicesassistant.ui.theme.play

data class FlowerLanguageResponse(
    val code: Int,
    val msg: String,
    val result: FlowerLanguageItem?
)

data class FlowerLanguageItem(
    val cnflower: String,    // 中文花名
    val enflower: String,    // 英文花名
    val flowerlang: String,  // 花语
    val flowerprov: String   // 花语箴言
)