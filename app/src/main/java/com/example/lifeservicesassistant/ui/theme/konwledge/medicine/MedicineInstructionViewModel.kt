package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicineInstructionViewModel : ViewModel() {
    private val _instructions = MutableStateFlow<List<MedicineInstruction>>(emptyList())
    val instructions: StateFlow<List<MedicineInstruction>> = _instructions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun searchMedicine(apiKey: String, keyword: String) {
        if (keyword.isBlank()) {
            _errorMessage.value = "请输入药品名称"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.medicineInstructionApi
                    .getMedicineInstructions(apiKey, keyword.trim())
                
                when {
                    response.code != 200 -> _errorMessage.value = response.msg
                    response.result?.list.isNullOrEmpty() -> _errorMessage.value = "未找到该药品说明书"
                    else -> _instructions.value = response.result!!.list!!
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络错误: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}