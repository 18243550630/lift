package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

data class MedicineInstructionResponse(
    val code: Int,
    val msg: String,
    val result: MedicineInstructionResult?
)

data class MedicineInstructionResult(
    val list: List<MedicineInstruction>?
)

data class MedicineInstruction(
    val title: String,
    val content: String,
    val specification: String? = HtmlUtils.extractSection(content, "【规格】"),
    val usage: String? = HtmlUtils.extractSection(content, "【用量用法】"),
    val sideEffects: String? = HtmlUtils.extractSection(content, "【注意事项】"),
    val aliases: String? = HtmlUtils.extractSection(content, "【别名】")
) {
    val parsedContent by lazy { HtmlUtils.parseMedicineContent(content) }
}