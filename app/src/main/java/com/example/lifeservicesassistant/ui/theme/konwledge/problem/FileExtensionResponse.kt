// FileExtension.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class FileExtensionResponse(
    val code: Int,
    val msg: String,
    val result: FileExtensionItem?
)

data class FileExtensionItem(
    val targa: String,  // 文件扩展名
    val notes: String   // 含义说明
)