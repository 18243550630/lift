// PetApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface PetApiService {
    @GET("pet/index")
    suspend fun getPetInfo(
        @Query("key") apiKey: String,
        @Query("name") name: String? = null,
        @Query("type") type: Int? = null,
        @Query("page") page: Int = 1,
        @Query("num") count: Int = 10
    ): PetResponse
}