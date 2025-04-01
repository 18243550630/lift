// FlowerLanguageViewModel.kt
package com.example.lifeservicesassistant.ui.theme.play

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

class FlowerLanguageViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(FlowerLanguageState())
    val state: StateFlow<FlowerLanguageState> = _state

    fun searchFlower(flowerName: String) {
        if (flowerName.isBlank()) {
            _state.update { it.copy(
                flowerInfo = null,
                error = "请输入花名"
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.flowerLanguageApi.searchFlowerLanguage(
                    apiKey = apiKey,
                    flowerName = flowerName
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result == null -> it.copy(
                            error = "未找到该花的花语信息",
                            isLoading = false
                        )
                        else -> it.copy(
                            flowerInfo = response.result,
                            isLoading = false,
                            searchedFlower = flowerName
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
    
    fun copyToClipboard(text: String, label: String) {
        val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}

data class FlowerLanguageState(
    val flowerInfo: FlowerLanguageItem? = null,
    val searchedFlower: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)