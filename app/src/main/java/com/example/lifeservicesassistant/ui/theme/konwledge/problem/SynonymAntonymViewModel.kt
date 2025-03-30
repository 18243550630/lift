package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SynonymAntonymViewModel : ViewModel() {
    private val _result = MutableStateFlow<SynonymAntonymResult?>(null)
    val result: StateFlow<SynonymAntonymResult?> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun search(apiKey: String, keyword: String) {
        if (keyword.isBlank()) {
            _errorMessage.value = "请输入查询词语"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.synonymAntonymApi.getSynonymAntonym(apiKey, keyword)
                when {
                    response.code != 200 -> _errorMessage.value = response.msg
                    response.result == null -> _errorMessage.value = "未找到相关结果"
                    else -> _result.value = response.result
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络错误: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}