// HotNews.kt
package com.example.lifeservicesassistant.ui.theme.play

data class HotNewsResponse(
    val code: Int,
    val msg: String,
    val result: HotNewsResult?
)

data class HotNewsResult(
    val list: List<HotNewsItem>?
)

data class HotNewsItem(
    val hotnum: Int,    // 热搜指数
    val title: String,  // 热搜标题
    val digest: String? // 热搜简介(可能为空)
)