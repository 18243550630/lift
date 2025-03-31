// HotNewsApiService.kt
package com.example.lifeservicesassistant.ui.theme.play

import retrofit2.http.GET
import retrofit2.http.Query

interface HotNewsApiService {
    @GET("networkhot/index")
    suspend fun getHotNews(
        @Query("key") apiKey: String
    ): HotNewsResponse
}