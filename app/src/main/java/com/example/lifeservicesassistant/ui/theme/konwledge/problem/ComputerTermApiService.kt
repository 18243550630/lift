// ComputerTermApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

// ComputerTermApiService.kt
interface ComputerTermApiService {
    @GET("pcterm/index")
    suspend fun searchTerm(
        @Query("key") apiKey: String,
        @Query("word") term: String
    ): ComputerTermResponse  // 返回类型改为ComputerTermResponse
}