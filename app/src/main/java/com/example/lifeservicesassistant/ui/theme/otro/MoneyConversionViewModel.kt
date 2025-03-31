// MoneyConversionViewModel.kt
package com.example.lifeservicesassistant.ui.theme.otro

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoneyConversionViewModel( private val application: Application) : ViewModel() {
    private val _state = MutableStateFlow(MoneyConversionState())
    val state: StateFlow<MoneyConversionState> = _state
    
    private var currentCurrencyType = "rmb" // 默认人民币

    fun copyToClipboard(text: String, label: String = "金额转换结果") {
        val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    fun convertAmount(amount: String, currencyType: String = currentCurrencyType) {
        currentCurrencyType = currencyType
        
        if (amount.isBlank()) {
            _state.update { it.copy(
                result = null,
                error = "请输入金额"
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.moneyConversionApi.convertMoney(
                    apiKey = "ee5d3823a527577eee53438f2951d4d4",
                    amount = amount,
                    currencyType = currencyType
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result == null -> it.copy(
                            error = "转换结果为空",
                            isLoading = false
                        )
                        else -> it.copy(
                            result = response.result,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "转换失败: ${e.message?.take(20)}...",
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class MoneyConversionState(
    val result: MoneyResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)