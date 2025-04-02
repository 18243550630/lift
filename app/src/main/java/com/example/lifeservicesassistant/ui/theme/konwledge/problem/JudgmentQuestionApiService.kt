// JudgmentQuestionApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface JudgmentQuestionApiService {
    @GET("decide/index")
    suspend fun getJudgmentQuestion(
        @Query("key") apiKey: String
    ): JudgmentQuestionResponse
}