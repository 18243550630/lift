// GarbageViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GarbageViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(GarbageState())
    val state: StateFlow<GarbageState> = _state
    
    private var currentQuery = ""
    private var currentPage = 1

    fun searchGarbage(itemName: String) {
        currentQuery = itemName
        currentPage = 1
        fetchClassification()
    }
    
    fun loadMore() {
        currentPage++
        fetchClassification()
    }

    private fun fetchClassification() {
        if (currentQuery.isBlank()) {
            _state.update { it.copy(
                items = emptyList(),
                error = "请输入物品名称"
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.garbageApi.classifyGarbage(
                    apiKey = apiKey,
                    itemName = currentQuery,
                    page = currentPage
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result?.list.isNullOrEmpty() -> it.copy(
                            error = if (currentPage == 1) "未找到分类信息" else "没有更多数据",
                            isLoading = false
                        )
                        else -> {
                            val newList = if (currentPage == 1) {
                                response.result?.list ?: emptyList()
                            } else {
                                it.items + (response.result?.list ?: emptyList())
                            }
                            it.copy(
                                items = newList,
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

data class GarbageState(
    val items: List<GarbageItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canLoadMore: Boolean = false
)