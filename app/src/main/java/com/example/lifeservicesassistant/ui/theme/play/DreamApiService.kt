// DreamApiService.kt
package com.example.lifeservicesassistant.ui.theme.play

import retrofit2.http.GET
import retrofit2.http.Query

interface DreamApiService {
    @GET("dream/index")
    suspend fun getDreamInterpretation(
        @Query("key") apiKey: String,
        @Query("word") keyword: String,
        @Query("num") count: Int = 10,
        @Query("page") page: Int = 1
    ): DreamResponse
}