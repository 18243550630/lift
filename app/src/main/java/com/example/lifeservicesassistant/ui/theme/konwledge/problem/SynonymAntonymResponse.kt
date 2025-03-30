package com.example.lifeservicesassistant.ui.theme.konwledge.problem

data class SynonymAntonymResponse(
    val code: Int,
    val msg: String,
    val result: SynonymAntonymResult?
)

data class SynonymAntonymResult(
    val words: String,  // 查询词
    val jyc: String,    // 近义词（逗号分隔）
    val fyc: String     // 反义词（逗号分隔）
)