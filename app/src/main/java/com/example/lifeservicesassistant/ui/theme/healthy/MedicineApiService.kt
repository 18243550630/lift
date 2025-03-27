package com.example.lifeservicesassistant.ui.theme.healthy

import retrofit2.http.GET
import retrofit2.http.Query

interface MedicineApiService {
    @GET("zhongyao/index")
    suspend fun searchMedicine(
        @Query("key") apiKey: String,
        @Query("word") keyword: String,
        @Query("num") count: Int = 10,
        @Query("page") page: Int = 1
    ): MedicineResponse
}