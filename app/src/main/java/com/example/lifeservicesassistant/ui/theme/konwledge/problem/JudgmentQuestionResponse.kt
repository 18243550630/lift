// JudgmentQuestion.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class JudgmentQuestionResponse(
    val code: Int,
    val msg: String,
    val result: JudgmentQuestionItem?
)

data class JudgmentQuestionItem(
    val title: String,  // 题目
    val answer: Int,    // 答案 (0错误、1正确)
    val analyse: String // 分析
)