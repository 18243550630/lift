// OilPriceApiService.kt
package com.example.lifeservicesassistant.ui.theme.otro

import retrofit2.http.GET
import retrofit2.http.Query

interface OilPriceApiService {
    @GET("oilprice/index")
    suspend fun getOilPrice(
        @Query("key") apiKey: String,
        @Query("prov") province: String
    ): OilPriceResponse
}