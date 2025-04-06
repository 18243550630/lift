package com.example.lifeservicesassistant.ui.theme.news

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeservicesassistant.SunnyWeatherApplication.Companion.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository(context)
    private val recommender = NewsRecommender(context)

    private val _newsState = mutableStateOf<NewsState>(NewsState.Loading)
    val newsState: State<NewsState> = _newsState

    private val _selectedNews = mutableStateOf<News?>(null)
    val selectedNews: State<News?> = _selectedNews

    private val categoryMap = mapOf(
        "首页" to "top",
        "国内" to "guonei",
        "娱乐" to "yule",
        "体育" to "tiyu",
        "科技" to "keji",
        "财经" to "caijing",
        "时尚" to "shishang"
    )

    // 推荐缓存
    private var cachedRecommended: List<News> = emptyList()
    private var recommendedOffset = 0
    private val pageSize = 10

    suspend fun getPersonalizedRecommendations(forceRefresh: Boolean = false): List<News> {
        return withContext(Dispatchers.IO) {
            if (cachedRecommended.isEmpty() || forceRefresh) {
                val allNews = mutableListOf<News>()
                for (category in categoryMap.values) {
                    val result = repository.getTopHeadlines(category)
                    if (result is ResultWrapper.Success) {
                        allNews += result.data
                    }
                }

                val history = NewsStorage.getHistory(context)
                val userInterests = NewsStorage.getUserInterests(context)

                cachedRecommended = recommender.getPersonalizedRecommendations(
                    allNews = allNews,
                    history = history,
                    userInterests = userInterests,
                    topN = 100
                )

                recommendedOffset = 0
                Log.d("推荐调试", "新推荐生成：${cachedRecommended.size} 条")
            }

            val end = (recommendedOffset + pageSize).coerceAtMost(cachedRecommended.size)
            val nextPage = cachedRecommended.subList(recommendedOffset, end)
            recommendedOffset = end
            return@withContext nextPage
        }
    }

    fun loadNews(category: String = "首页") {
        viewModelScope.launch {
            _newsState.value = NewsState.Loading

            if (category == "推荐") {
                val initial = getPersonalizedRecommendations(forceRefresh = true)
                _newsState.value = NewsState.Success(
                    news = initial,
                    recommended = initial,
                    showRecommendedSection = false
                )
                return@launch
            }

            // 普通新闻分类
            val mappedCategory = categoryMap[category] ?: "top"
            val result = repository.getTopHeadlines(mappedCategory)

            if (result is ResultWrapper.Success) {
                _newsState.value = NewsState.Success(
                    news = result.data,
                    recommended = emptyList(),
                    showRecommendedSection = false
                )
            } else if (result is ResultWrapper.Failure) {
                _newsState.value = NewsState.Error(result.message ?: "加载失败")
            }
        }
    }

    fun loadMoreRecommendations() {
        viewModelScope.launch {
            val more = getPersonalizedRecommendations(forceRefresh = false)
            val current = (_newsState.value as? NewsState.Success)?.news ?: emptyList()
            _newsState.value = NewsState.Success(
                news = current + more,
                recommended = current + more,
                showRecommendedSection = false
            )
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
        data class Success(
            val news: List<News>,
            val recommended: List<News> = emptyList(),
            val showRecommendedSection: Boolean = false
        ) : NewsState()
        data class Error(val message: String) : NewsState()
    }
}

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T) : ResultWrapper<T>()
    data class Failure(val message: String?, val exception: Exception? = null) : ResultWrapper<Nothing>()
}
