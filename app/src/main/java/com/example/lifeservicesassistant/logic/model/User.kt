package com.example.lifeservicesassistant.logic.model

data class User(
    val id: Int = 0,
    val username: String = "",
    val account: String,
    val password: String,
    val avatar: String = "",
    val registerTime: Long = System.currentTimeMillis(),
    val status: Int = 1
)
