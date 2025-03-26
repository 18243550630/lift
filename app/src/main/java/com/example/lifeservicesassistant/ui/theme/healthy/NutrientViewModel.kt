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
    private val _nutrientInfo = MutableStateFlow<NutrientInfo?>(null)
    val nutrientInfo: StateFlow<NutrientInfo?> = _nutrientInfo

    private val _uiState = MutableStateFlow(NutrientUiState())
    val uiState: StateFlow<NutrientUiState> = _uiState

    fun fetchNutrient(apiKey: String, keyword: String) {
        _uiState.value = NutrientUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val res = RetrofitClient.nutrientApi.getNutrient(apiKey, keyword.trim(), 0)
                if (res.code == 200 && res.result?.list?.isNotEmpty() == true) {
                    _nutrientInfo.value = res.result.list[0] // 默认取第一个结果
                    _uiState.value = NutrientUiState()
                } else {
                    _uiState.value = NutrientUiState(errorMessage = res.msg ?: "未找到数据")
                }
            } catch (e: Exception) {
                _uiState.value = NutrientUiState(errorMessage = "请求失败: ${e.message}")
            }
        }
    }
}