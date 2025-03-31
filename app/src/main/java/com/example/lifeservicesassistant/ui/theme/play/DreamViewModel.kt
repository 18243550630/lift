// DreamViewModel.kt
package com.example.lifeservicesassistant.ui.theme.play


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.activity.compose.setContent


class DreamViewModel : ViewModel() {
    private val _state = MutableStateFlow(DreamState())
    val state: StateFlow<DreamState> = _state
    
    private var currentKeyword = ""
    private var currentPage = 1

    fun searchDream(keyword: String) {
        currentKeyword = keyword
        currentPage = 1
        fetchDreamInterpretation()
    }
    
    fun loadMore() {
        currentPage++
        fetchDreamInterpretation()
    }

    private fun fetchDreamInterpretation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.dreamApi.getDreamInterpretation(
                    apiKey = "ee5d3823a527577eee53438f2951d4d4",
                    keyword = currentKeyword,
                    count = 10,
                    page = currentPage
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result?.list.isNullOrEmpty() -> it.copy(
                            error = if (currentPage == 1) "未找到相关解梦" else "没有更多数据",
                            isLoading = false
                        )
                        else -> {
                            val newList = if (currentPage == 1) {
                                response.result?.list ?: emptyList()
                            } else {
                                it.dreamItems + (response.result?.list ?: emptyList())
                            }
                            it.copy(
                                dreamItems = newList,
                                isLoading = false,
                                canLoadMore = (response.result?.list?.size ?: 0) >= 10
                            )
                        }
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
}

data class DreamState(
    val dreamItems: List<DreamItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canLoadMore: Boolean = false
)