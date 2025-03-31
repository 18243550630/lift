package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicineInstructionState(
    val instructions: List<MedicineInstruction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MedicineInstructionViewModel : ViewModel() {
    private val _state = MutableStateFlow(MedicineInstructionState())
    val uiState: StateFlow<MedicineInstructionState> = _state

    fun searchMedicine(apiKey: String, keyword: String) {
        if (keyword.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val response = RetrofitClient.medicineInstructionApi
                    .getMedicineInstructions(apiKey, keyword)

                if (response.code == 200) {
                    val instructions = response.result?.list?.map { item ->
                        MedicineInstruction(
                            title = item.title,
                            content = item.content
                        )
                    } ?: emptyList()

                    _state.update {
                        it.copy(
                            instructions = instructions,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            error = response.msg ?: "未知错误",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "网络错误: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}