// PetViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PetViewModel : ViewModel() {
    private val _state = MutableStateFlow(PetState())
    val state: StateFlow<PetState> = _state
    
    private var currentSearchName: String? = null
    private var currentPetType: Int? = null
    private var currentPage = 1

    fun searchPets(name: String? = null, type: Int? = null) {
        currentSearchName = name
        currentPetType = type
        currentPage = 1
        fetchPetInfo()
    }
    
    fun loadMore() {
        currentPage++
        fetchPetInfo()
    }

    private fun fetchPetInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.petApi.getPetInfo(
                    apiKey = "ee5d3823a527577eee53438f2951d4d4",
                    name = currentSearchName,
                    type = currentPetType,
                    page = currentPage,
                    count = 10
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result?.list.isNullOrEmpty() -> it.copy(
                            error = if (currentPage == 1) "未找到相关宠物信息" else "没有更多数据",
                            isLoading = false
                        )
                        else -> {
                            val newList = if (currentPage == 1) {
                                response.result?.list ?: emptyList()
                            } else {
                                it.petItems + (response.result?.list ?: emptyList())
                            }
                            it.copy(
                                petItems = newList,
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

data class PetState(
    val petItems: List<PetItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canLoadMore: Boolean = false
)