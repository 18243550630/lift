package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface QAApiService {
    @GET("baiketiku/index")
    suspend fun getQA(
        @Query("key") apiKey: String
    ): QAResponse
}