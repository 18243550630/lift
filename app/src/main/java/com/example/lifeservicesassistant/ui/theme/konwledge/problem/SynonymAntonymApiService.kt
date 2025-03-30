package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface SynonymAntonymApiService {
    @GET("jfwords/index")
    suspend fun getSynonymAntonym(
        @Query("key") apiKey: String,
        @Query("word") keyword: String
    ): SynonymAntonymResponse
}