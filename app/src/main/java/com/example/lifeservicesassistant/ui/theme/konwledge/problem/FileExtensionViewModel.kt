// FileExtensionViewModel.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

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

class FileExtensionViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(FileExtensionState())
    val state: StateFlow<FileExtensionState> = _state

    fun searchExtension(extension: String) {
        if (extension.isBlank()) {
            _state.update { it.copy(
                extensionInfo = null,
                error = "请输入文件扩展名"
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.fileExtensionApi.searchExtension(
                    apiKey = apiKey,
                    extension = extension
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result == null -> it.copy(
                            error = "未找到该扩展名信息",
                            isLoading = false
                        )
                        else -> it.copy(
                            extensionInfo = response.result,
                            isLoading = false,
                            searchedExtension = extension
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

data class FileExtensionState(
    val extensionInfo: FileExtensionItem? = null,
    val searchedExtension: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)