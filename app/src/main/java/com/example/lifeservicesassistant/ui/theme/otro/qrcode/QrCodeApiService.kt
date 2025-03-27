package com.example.lifeservicesassistant.ui.theme.otro.qrcode// QrCodeApiService.kt
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface QrCodeApiService {
    @GET("qrcode/api")
    suspend fun generateQrCodeGet(
        @QueryMap params: Map<String, String>
    ): QrCodeResponse

    @FormUrlEncoded
    @POST("qrcode/api")
    suspend fun generateQrCodePost(
        @FieldMap params: Map<String, String>
    ): QrCodeResponse
}