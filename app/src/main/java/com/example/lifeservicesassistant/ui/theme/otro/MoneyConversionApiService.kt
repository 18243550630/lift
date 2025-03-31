// MoneyConversionApiService.kt
package com.example.lifeservicesassistant.ui.theme.otro

import retrofit2.http.GET
import retrofit2.http.Query

interface MoneyConversionApiService {
    @GET("cnmoney/index")
    suspend fun convertMoney(
        @Query("key") apiKey: String,
        @Query("money") amount: String,
        @Query("type") currencyType: String = "rmb" // 默认人民币
    ): MoneyConversionResponse
}