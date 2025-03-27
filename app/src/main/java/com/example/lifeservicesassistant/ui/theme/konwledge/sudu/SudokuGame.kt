package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

// 数独游戏状态
// 数独游戏状态
// API响应数据模型
data class SudokuApiResponse(
    val error_code: Int,
    val reason: String,
    val result: SudokuResult
)

data class SudokuResult(
    val puzzle: List<List<Int>>,    // 二维数组
    val solution: List<List<Int>>   // 二维数组
)

// 游戏状态模型
data class SudokuGame(
    val puzzle: List<List<Int>>,    // 保持二维结构
    val solution: List<List<Int>>,  // 保持二维结构
    val difficulty: String,
    val userInput: MutableList<MutableList<Int?>> = MutableList(9) { MutableList(9) { null } }
) {
    // 检查是否完成
    fun isComplete(): Boolean {
        return userInput.flatten() == solution.flatten()
    }

    // 获取单元格值（优先用户输入）
    fun getCellValue(row: Int, col: Int): Int {
        return userInput[row][col] ?: puzzle[row][col]
    }
}