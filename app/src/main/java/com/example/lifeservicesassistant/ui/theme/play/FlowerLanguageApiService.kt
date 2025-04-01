// FlowerLanguageApiService.kt
package com.example.lifeservicesassistant.ui.theme.play

import retrofit2.http.GET
import retrofit2.http.Query

interface FlowerLanguageApiService {
    @GET("huayu/index")
    suspend fun searchFlowerLanguage(
        @Query("key") apiKey: String,
        @Query("word") flowerName: String
    ): FlowerLanguageResponse
}