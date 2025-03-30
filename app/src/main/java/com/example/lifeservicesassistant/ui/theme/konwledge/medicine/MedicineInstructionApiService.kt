package com.example.lifeservicesassistant.ui.theme.konwledge.medicine

import retrofit2.http.GET
import retrofit2.http.Query

interface MedicineInstructionApiService {
    @GET("yaopin/index")
    suspend fun getMedicineInstructions(
        @Query("key") apiKey: String,
        @Query("word") keyword: String,
        @Query("num") count: Int = 1,
        @Query("page") page: Int = 1
    ): MedicineInstructionResponse
}