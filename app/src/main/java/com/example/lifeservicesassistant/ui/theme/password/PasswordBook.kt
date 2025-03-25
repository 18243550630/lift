package com.example.lifeservicesassistant.ui.theme.password

// 密码本模型
data class PasswordBook(
    val id: Long,
    val title: String,
    val passwords: List<PasswordItem> = emptyList()
)

// 密码项模型
data class PasswordItem(
    val id: Long,
    val title: String,
    val password: String
)
