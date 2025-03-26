package com.example.lifeservicesassistant.ui.theme.healthy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.gson.Gson

class HealthViewModel : ViewModel() {

    private val _tips = MutableStateFlow<List<String>>(emptyList())
    val tips: StateFlow<List<String>> = _tips

    fun fetchTip(apiKey: String, keyword: String) {
        val cleanKeyword = keyword.trim()
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getHealthTip(apiKey, cleanKeyword)

                Log.d("Keyword_Debug", "查询关键词：'$cleanKeyword'")
                Log.d("API_Debug", "返回数据：${Gson().toJson(response)}")

                val resultList = response.result?.list

                if (response.code == 200 && !resultList.isNullOrEmpty()) {
                    _tips.value = resultList.map { it.content }
                } else {
                    _tips.value = listOf("没有找到相关提示：${response.msg} (code: ${response.code})")
                }

            } catch (e: Exception) {
                _tips.value = listOf("网络请求失败: ${e.localizedMessage}")
                Log.e("HealthViewModel", "请求异常", e)
            }
        }
    }
}
