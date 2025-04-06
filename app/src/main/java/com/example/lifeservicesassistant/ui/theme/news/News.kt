package com.example.lifeservicesassistant.ui.theme.news

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class JuheApiResponse(
    val reason: String,
    val result: JuheResult?,
    val error_code: Int
)

data class JuheResult(
    val stat: String,
    val data: List<JuheNewsItem>
)

data class JuheNewsItem(
    val title: String,
    val date: String,
    val author_name: String,
    val thumbnail_pic_s: String?,
    val thumbnail_pic_s02: String?,
    val thumbnail_pic_s03: String?,
    val url: String,
    val category: String,
    val uniquekey: String
) {
    val imageUrl: String?
        get() = thumbnail_pic_s ?: thumbnail_pic_s02 ?: thumbnail_pic_s03
}

fun JuheNewsItem.toNews(): News {
    return News(
        id = uniquekey,
        title = title,
        content = "", // 聚合数据无正文字段
        author = author_name,
        publishedAt = date,
        urlToImage = imageUrl,
        source = author_name,
        url = url,
        category = category
    )
}

@Parcelize
data class News(
    val id: String,
    val title: String,
    val content: String,
    val author: String,
    val publishedAt: String,
    val urlToImage: String?,
    val source: String,
    val url: String,
    val category: String = "top",
    val keywords: List<String> = emptyList(),
    val embedding: List<Float> = emptyList() // 用于深度学习模型
) : Parcelable