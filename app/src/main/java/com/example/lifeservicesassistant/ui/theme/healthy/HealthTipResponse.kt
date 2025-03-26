package com.example.lifeservicesassistant.ui.theme.healthy

data class HealthTipResponse(
    val code: Int,
    val msg: String,
    val result: HealthResult?
)

data class HealthResult(
    val list: List<HealthTip>?
)

data class HealthTip(
    val title: String,
    val content: String
)

