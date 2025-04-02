package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context

class NewsRepository(
    private val context: Context
) {
    private val newsApi = RetrofitClient.newsApi

    suspend fun getTopHeadlines(category: String): ResultWrapper<List<News>> {
        return try {
            if (!context.isNetworkAvailable()) {
                return ResultWrapper.Failure("网络不可用")
            }

            val response = newsApi.getTopHeadlines(category = category)
            if (response.isSuccessful) {
                val newsList = response.body()?.result?.data?.map { it.toNews() } ?: emptyList()
                ResultWrapper.Success(newsList)
            } else {
                ResultWrapper.Failure("服务器错误: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultWrapper.Failure("请求失败: ${e.message}")
        }
    }

    suspend fun searchNews(query: String): ResultWrapper<List<News>> {
        return try {
            if (!context.isNetworkAvailable()) {
                return ResultWrapper.Failure("网络不可用")
            }

            val response = newsApi.searchNews(query)
            if (response.isSuccessful) {
                val newsList = response.body()?.result?.data?.map { it.toNews() } ?: emptyList()
                ResultWrapper.Success(newsList)
            } else {
                ResultWrapper.Failure("搜索失败: ${response.code()}")
            }
        } catch (e: Exception) {
            ResultWrapper.Failure("搜索请求失败: ${e.message}")
        }
    }
}
