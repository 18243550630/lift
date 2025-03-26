package com.example.lifeservicesassistant.ui.theme.healthy

import retrofit2.http.GET
import retrofit2.http.Query

interface HealthApiService {
    @GET("healthskill/index")
    suspend fun getHealthTip(
        @Query("key") apiKey: String,
        @Query("word") word: String
    ): HealthTipResponse
}
