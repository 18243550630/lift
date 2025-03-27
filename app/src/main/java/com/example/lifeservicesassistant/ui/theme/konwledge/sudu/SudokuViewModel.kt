package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SudokuViewModel(private val repository: SudokuRepository) : ViewModel() {
    var gameState by mutableStateOf<SudokuGame?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun generateNewGame(difficulty: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                gameState = repository.generateSudoku(difficulty)
            } catch (e: Exception) {
                errorMessage = "生成失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateCell(row: Int, col: Int, value: Int?) {
        gameState?.let { current ->
            // 创建新的 userInput 副本
            val newInput = current.userInput.map { it.toMutableList() }.toMutableList()
            newInput[row][col] = value
            // 创建新的游戏状态
            gameState = current.copy(
                userInput = newInput
            )
        }
    }
}