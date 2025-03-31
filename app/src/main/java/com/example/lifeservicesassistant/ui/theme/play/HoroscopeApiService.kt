package com.example.lifeservicesassistant.ui.theme.play

import retrofit2.http.GET
import retrofit2.http.Query

interface HoroscopeApiService {
    @GET("star/index")
    suspend fun getHoroscope(
        @Query("key") apiKey: String,
        @Query("astro") sign: String,
        @Query("date") date: String? = null
    ): HoroscopeResponse
}