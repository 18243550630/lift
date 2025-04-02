package com.example.lifeservicesassistant.ui.theme.news

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("index")
    suspend fun getTopHeadlines(
        @Query("type") category: String = "top", // top, guonei, yule, etc.
        @Query("key") apiKey: String = "be22a8974c6b62231d874cc19d81e1d6"
    ): Response<JuheApiResponse>

    @GET("index")
    suspend fun searchNews(
        @Query("q") query: String,  // 添加搜索关键词参数
        @Query("key") apiKey: String = "be22a8974c6b62231d874cc19d81e1d6"
    ): Response<JuheApiResponse>
}

