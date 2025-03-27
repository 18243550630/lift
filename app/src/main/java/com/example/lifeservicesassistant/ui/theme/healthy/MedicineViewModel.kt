package com.example.lifeservicesassistant.ui.theme.healthy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicineViewModel : ViewModel() {
    private val _medicineResult = MutableStateFlow<MedicineResponse?>(null)
    val medicineResult: StateFlow<MedicineResponse?> = _medicineResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun searchMedicine(apiKey: String, keyword: String) {
        if (keyword.isBlank()) {
            _errorMessage.value = "请输入药材名称"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _medicineResult.value = null

            try {
                val response = RetrofitClient.medicineApi.searchMedicine(apiKey, keyword)
                _medicineResult.value = response
            } catch (e: Exception) {
                _errorMessage.value = "请求失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}