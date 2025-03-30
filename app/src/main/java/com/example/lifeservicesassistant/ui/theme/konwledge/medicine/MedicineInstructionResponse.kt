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
    val title: String,      // 药品名称
    val content: String,    // 说明书内容
    val specification: String? = null,  // 规格
    val usage: String? = null,          // 用法用量
    val sideEffects: String? = null     // 不良反应
) {
    // 安全访问属性
    val safeSpecification get() = specification ?: "暂无规格信息"
    val safeUsage get() = usage ?: "暂无用法用量信息"
    val safeSideEffects get() = sideEffects ?: "暂无不良反应信息"
}