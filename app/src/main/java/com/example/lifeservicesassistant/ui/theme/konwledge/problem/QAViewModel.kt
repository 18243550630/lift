package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QAViewModel : ViewModel() {
    private val _currentQA = MutableStateFlow<QAResult?>(null)
    val currentQA: StateFlow<QAResult?> = _currentQA

    private val _selectedAnswer = MutableStateFlow<String?>(null)
    val selectedAnswer: StateFlow<String?> = _selectedAnswer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchQA(apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.qaApi.getQA(apiKey)
                if (response.code == 200) {
                    _currentQA.value = response.result
                    _selectedAnswer.value = null
                } else {
                    _errorMessage.value = response.msg
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络错误: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectAnswer(answer: String) {
        _selectedAnswer.value = answer
    }

    fun isAnswerCorrect(): Boolean {
        return selectedAnswer.value == currentQA.value?.answer
    }
}