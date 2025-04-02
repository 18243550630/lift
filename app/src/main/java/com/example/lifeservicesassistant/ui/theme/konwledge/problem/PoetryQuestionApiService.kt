// PoetryQuestionApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface PoetryQuestionApiService {
    @GET("scwd/index")
    suspend fun getPoetryQuestion(
        @Query("key") apiKey: String
    ): PoetryQuestionResponse
}