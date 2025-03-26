package com.example.lifeservicesassistant.ui.theme.healthy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*class NutrientViewModel : ViewModel() {
    private val _nutrientInfo = MutableStateFlow<NutrientInfo?>(null)
    val nutrientInfo: StateFlow<NutrientInfo?> = _nutrientInfo

    fun fetchNutrient(apiKey: String, keyword: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.nutrientApi.getNutrient(apiKey, keyword.trim(), 0)
                if (res.code == 200 && res.result?.list?.isNotEmpty() == true) {
                    _nutrientInfo.value = res.result.list[0]  // 取第一个品种
                } else {
                    _nutrientInfo.value = null
                }
            } catch (e: Exception) {
                Log.e("NutrientViewModel", "请求错误: ${e.message}")
                _nutrientInfo.value = null
            }
        }
    }
}*/

data class NutrientUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class NutrientViewModel : ViewModel() {
    private val _nutrientList = MutableStateFlow<List<NutrientInfo>>(emptyList())
    val nutrientList: StateFlow<List<NutrientInfo>> = _nutrientList

    private val _currentPage = MutableStateFlow(1)
    private val _totalItems = MutableStateFlow(0)
    val _isLoading = MutableStateFlow(false)



    fun toggleExpand(itemName: String) {
        _nutrientList.value = _nutrientList.value.map {
            if (it.name == itemName) it.copy(isExpanded = !it.isExpanded) else it
        }
    }
    fun fetchNutrient(apiKey: String, keyword: String, page: Int = 1, num: Int = 10) {
        if (_isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val res = RetrofitClient.nutrientApi.getNutrient(
                    key = apiKey,
                    word = keyword.trim(),
                    mode = 0,
                    num = num,
                    page = page
                )

                if (res.code == 200) {
                    _nutrientList.value = res.result?.list ?: emptyList()
                    _totalItems.value = res.result?.list?.size ?: 0
                    _currentPage.value = page
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage(apiKey: String, keyword: String, num: Int = 10) {
        fetchNutrient(apiKey, keyword, _currentPage.value + 1, num)
    }
}