package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class QAResponse(
    val code: Int,
    val msg: String,
    val result: QAResult?
)

data class QAResult(
    val title: String,
    val answerA: String,
    val answerB: String,
    val answerC: String,
    val answerD: String,
    val answer: String,  // 正确答案标识（如"A"、"B"等）
    val analytic: String?
)