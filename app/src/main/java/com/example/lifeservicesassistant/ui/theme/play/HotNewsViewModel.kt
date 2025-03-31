// HotNewsViewModel.kt
package com.example.lifeservicesassistant.ui.theme.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HotNewsViewModel : ViewModel() {
    private val _state = MutableStateFlow(HotNewsState())
    val state: StateFlow<HotNewsState> = _state

    fun fetchHotNews(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.hotNewsApi.getHotNews(apiKey)
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result?.list.isNullOrEmpty() -> it.copy(
                            error = "暂无热搜数据",
                            isLoading = false
                        )
                        else -> it.copy(
                            hotNewsItems = response.result?.list ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "获取热搜失败: ${e.message?.take(20)}...",
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class HotNewsState(
    val hotNewsItems: List<HotNewsItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)