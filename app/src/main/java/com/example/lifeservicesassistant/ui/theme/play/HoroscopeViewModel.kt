package com.example.lifeservicesassistant.ui.theme.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HoroscopeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HoroscopeState())
    val state: StateFlow<HoroscopeState> = _state

    fun fetchHoroscope(apiKey: String, sign: String, date: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val response = RetrofitClient.horoscopeApi.getHoroscope(
                    apiKey = apiKey,
                    sign = zodiacSigns[sign] ?: sign.lowercase(),
                    date = date
                )

                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result?.list.isNullOrEmpty() -> it.copy(
                            error = "今日运势数据为空",
                            isLoading = false
                        )
                        else -> it.copy(
                            horoscopeItems = response.result?.list ?: emptyList(),
                            isLoading = false,
                            selectedSign = sign
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "获取失败: ${e.message?.take(20)}...",
                        isLoading = false
                    )
                }
                e.printStackTrace()
            }
        }
    }
    data class HoroscopeState(
        val horoscopeItems: List<HoroscopeItem> = emptyList(),
        val selectedSign: String = "白羊座",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}