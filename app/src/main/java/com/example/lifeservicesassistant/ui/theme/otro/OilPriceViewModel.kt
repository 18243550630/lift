// OilPriceViewModel.kt
package com.example.lifeservicesassistant.ui.theme.otro

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.ui.theme.konwledge.problem.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OilPriceViewModel(
    private val apiKey: String,
    private val application: Application
) : ViewModel() {
    private val _state = MutableStateFlow(OilPriceState())
    val state: StateFlow<OilPriceState> = _state
    
    private val provinces = listOf(
        "北京", "上海", "天津", "重庆", "河北", "山西", "辽宁", "吉林", "黑龙江",
        "江苏", "浙江", "安徽", "福建", "江西", "山东", "河南", "湖北", "湖南",
        "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
        "内蒙古", "广西", "西藏", "宁夏", "新疆"
    )

    fun fetchOilPrice(province: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = RetrofitClient.oilPriceApi.getOilPrice(
                    apiKey = apiKey,
                    province = province
                )
                
                _state.update {
                    when {
                        response.code != 200 -> it.copy(
                            error = response.msg,
                            isLoading = false
                        )
                        response.result == null -> it.copy(
                            error = "未找到油价数据",
                            isLoading = false
                        )
                        else -> it.copy(
                            oilPrice = response.result,
                            isLoading = false,
                            selectedProvince = province
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
    
    fun getProvinces() = provinces
}

data class OilPriceState(
    val oilPrice: OilPriceResult? = null,
    val selectedProvince: String = "北京",
    val isLoading: Boolean = false,
    val error: String? = null
)