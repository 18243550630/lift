// MoneyConversion.kt
package com.example.lifeservicesassistant.ui.theme.otro

data class MoneyConversionResponse(
    val code: Int,
    val msg: String,
    val result: MoneyResult?
)

data class MoneyResult(
    val cnresult: String,  // 中文大写金额
    val fnresult: String,  // 格式化数字
    val enresult: String   // 英文大写金额
)