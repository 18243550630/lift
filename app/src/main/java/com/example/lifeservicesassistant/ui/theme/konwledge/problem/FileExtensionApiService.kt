// FileExtensionApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface FileExtensionApiService {
    @GET("targa/index")
    suspend fun searchExtension(
        @Query("key") apiKey: String,
        @Query("word") extension: String
    ): FileExtensionResponse
}