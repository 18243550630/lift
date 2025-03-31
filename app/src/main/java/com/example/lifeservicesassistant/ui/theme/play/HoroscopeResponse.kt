package com.example.lifeservicesassistant.ui.theme.play

data class HoroscopeResponse(
    val code: Int,
    val msg: String,
    val result: HoroscopeResult?  // 修改为对象类型
)

data class HoroscopeResult(
    val list: List<HoroscopeItem>  // 实际数据数组
)

data class HoroscopeItem(
    val type: String,     // 运势类型（如"综合指数"）
    val content: String   // 运势内容（如"60%"）
)
// 星座中英文映射
val zodiacSigns = mapOf(
    "白羊座" to "aries",
    "金牛座" to "taurus",
    "双子座" to "gemini",
    "巨蟹座" to "cancer",
    "狮子座" to "leo",
    "处女座" to "virgo",
    "天秤座" to "libra",
    "天蝎座" to "scorpio",
    "射手座" to "sagittarius",
    "摩羯座" to "capricorn",
    "水瓶座" to "aquarius",
    "双鱼座" to "pisces"
)