package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.SunnyWeatherApplication.Companion.context
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository(context)

    private val _newsState = mutableStateOf<NewsState>(NewsState.Loading)
    val newsState: State<NewsState> = _newsState

    private val _selectedNews = mutableStateOf<News?>(null)
    val selectedNews: State<News?> = _selectedNews

    private val supportedCategories = listOf("首页", "国内", "娱乐", "体育", "科技", "财经", "时尚")
    private val categoryMap = mapOf(
        "首页" to "top",
        "国内" to "guonei",
        "娱乐" to "yule",
        "体育" to "tiyu",
        "科技" to "keji",
        "财经" to "caijing",
        "时尚" to "shishang"
    )

    fun loadNews(category: String = "首页") {
        // 获取对应的API类别
        val apiCategory = categoryMap[category] ?: "top"
        viewModelScope.launch {
            _newsState.value = NewsState.Loading
            when (val result = repository.getTopHeadlines(apiCategory)) {
                is ResultWrapper.Success -> {
                    _newsState.value = NewsState.Success(result.data)
                }
                is ResultWrapper.Failure -> {
                    _newsState.value = NewsState.Error(result.message ?: "Unknown error")
                }
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading
            when (val result = repository.searchNews(query)) {
                is ResultWrapper.Success -> {
                    _newsState.value = NewsState.Success(result.data)
                }
                is ResultWrapper.Failure -> {
                    _newsState.value = NewsState.Error(result.message ?: "搜索失败")
                }
            }
        }
    }

    fun selectNews(news: News) {
        _selectedNews.value = news
    }

    sealed class NewsState {
        object Loading : NewsState()
        data class Success(val news: List<News>) : NewsState()
        data class Error(val message: String) : NewsState()
    }
}


// 自定义Result密封类
sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Failure(val message: String?, val exception: Exception? = null) : ResultWrapper<Nothing>()
}