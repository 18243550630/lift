package com.example.lifeservicesassistant.ui.theme.healthy

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NutrientApiService {
    @GET("nutrient/index")
    suspend fun getNutrient(
        @Query("key") key: String,
        @Query("word") word: String,
        @Query("mode") mode: Int = 0,
        @Query("num") num: Int = 10,  // 新增返回数量参数
        @Query("page") page: Int = 1   // 新增分页参数
    ): NutrientResponse
}