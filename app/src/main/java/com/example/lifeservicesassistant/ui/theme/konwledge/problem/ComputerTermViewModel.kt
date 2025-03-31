// ComputerTermViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComputerTermViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(ComputerTermState())
    val state: StateFlow<ComputerTermState> = _state

    // ComputerTermViewModel.kt
    fun searchTerm(term: String) {
        if (term.isBlank()) {
            _state.update {
                it.copy(
                    term = null,
                    error = "请输入术语缩写"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val response = RetrofitClient.computerTermApi.searchTerm(
                    apiKey = apiKey,
                    term = term
                )

                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )

                        response.result == null -> it.copy(
                            error = "未找到相关术语",
                            isLoading = false
                        )

                        else -> it.copy(
                            term = response.result,  // 直接存储单个术语
                            isLoading = false,
                            searchedTerm = term
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "查询失败: ${e.message?.take(20)}...",
                        isLoading = false
                    )
                }
            }
        }
    }
    // ComputerTermViewModel.kt
    fun copyToClipboard(text: String, label: String) {
        val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
    // 更新状态类
    data class ComputerTermState(
        val term: ComputerTermItem? = null,  // 改为单个术语
        val searchedTerm: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}