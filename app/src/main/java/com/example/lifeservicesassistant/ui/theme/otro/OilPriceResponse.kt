// OilPrice.kt
package com.example.lifeservicesassistant.ui.theme.otro

data class OilPriceResponse(
    val code: Int,
    val msg: String,
    val result: OilPriceResult?
)

data class OilPriceResult(
    val prov: String,  // 省份名称
    val p0: String,    // 0号柴油价格
    val p89: String,   // 89号汽油价格
    val p92: String,   // 92号汽油价格
    val p95: String,   // 95号汽油价格
    val p98: String,   // 98号汽油价格
    val time: String   // 更新时间
)