// PoetryQuestionViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PoetryQuestionViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(PoetryQuestionState())
    val state: StateFlow<PoetryQuestionState> = _state
    
    private var selectedAnswer: String? = null
    private var isAnswerRevealed = false
    private var autoNextEnabled = false
    private var countdownJob: Job? = null

    fun fetchQuestion() {
        countdownJob?.cancel()
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true, 
                error = null,
                countdown = null
            ) }
            selectedAnswer = null
            isAnswerRevealed = false
            
            try {
                val response = RetrofitClient.poetryQuestionApi.getPoetryQuestion(apiKey)
                
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
    
    fun selectAnswer(answer: String) {
        selectedAnswer = answer
        isAnswerRevealed = true
        _state.update { it.copy(showAnalysis = true) }
        
        if (autoNextEnabled) {
            startCountdown()
        }
    }
    
    fun setAutoNextEnabled(enabled: Boolean) {
        autoNextEnabled = enabled
        if (!enabled) {
            countdownJob?.cancel()
            _state.update { it.copy(countdown = null) }
        }
    }
    
    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                _state.update { it.copy(countdown = i) }
                delay(1000)
            }
            fetchQuestion()
        }
    }
    
    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }
}

data class PoetryQuestionState(
    val question: PoetryQuestionItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAnalysis: Boolean = false,
    val countdown: Int? = null
)