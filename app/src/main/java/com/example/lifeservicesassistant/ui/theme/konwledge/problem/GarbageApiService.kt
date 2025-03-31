// GarbageApiService.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import retrofit2.http.GET
import retrofit2.http.Query

interface GarbageApiService {
    @GET("lajifenlei/index")
    suspend fun classifyGarbage(
        @Query("key") apiKey: String,
        @Query("word") itemName: String,
        @Query("mode") mode: Int = 0,  // 0模糊查询
        @Query("num") count: Int = 10,  // 返回数量
        @Query("page") page: Int = 1    // 翻页
    ): GarbageResponse
}