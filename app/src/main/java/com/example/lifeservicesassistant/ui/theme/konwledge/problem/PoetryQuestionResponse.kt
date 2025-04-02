// PoetryQuestion.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class PoetryQuestionResponse(
    val code: Int,
    val msg: String,
    val result: PoetryQuestionItem?
)

data class PoetryQuestionItem(
    val question: String,   // 问题
    val answer_a: String,   // 选项A
    val answer_b: String,   // 选项B
    val answer_c: String,   // 选项C
    val answer: String,     // 正确答案 (A/B/C)
    val analytic: String    // 答案解析
)