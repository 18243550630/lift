// JudgmentQuestionViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JudgmentQuestionViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(JudgmentQuestionState())
    val state: StateFlow<JudgmentQuestionState> = _state
    
    private var selectedAnswer: Int? = null
    private var isAnswerRevealed = false

    fun fetchQuestion() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            selectedAnswer = null
            isAnswerRevealed = false
            
            try {
                val response = RetrofitClient.judgmentQuestionApi.getJudgmentQuestion(apiKey)
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result == null -> it.copy(
                            error = "获取题目失败",
                            isLoading = false
                        )
                        else -> it.copy(
                            question = response.result,
                            isLoading = false,
                            showAnalysis = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "获取失败: ${e.message?.take(20)}...",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun selectAnswer(answer: Int) {
        selectedAnswer = answer
        isAnswerRevealed = true
        _state.update { it.copy(showAnalysis = true) }
    }
}

data class JudgmentQuestionState(
    val question: JudgmentQuestionItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAnalysis: Boolean = false
)