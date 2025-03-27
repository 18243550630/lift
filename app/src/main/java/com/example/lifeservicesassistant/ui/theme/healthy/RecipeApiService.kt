package com.example.lifeservicesassistant.ui.theme.healthy

import retrofit2.http.GET
import retrofit2.http.Query

// RecipeApiService.kt
// RecipeApiService.kt
interface RecipeApiService {
    @GET("caipu/index")
    suspend fun searchRecipes(
        @Query("key") apiKey: String,
        @Query("word") keyword: String? = null,
        @Query("num") num: Int? = null,
        @Query("page") page: Int? = null
    ): RecipeResponse
}

// RecipeRepository.kt
class RecipeRepository(private val apiService: RecipeApiService, private val apiKey: String) {
    suspend fun searchRecipes(
        keyword: String? = null,
        num: Int = 10,
        page: Int = 1
    ): Result<RecipeResponse> {
        return try {
            val response = apiService.searchRecipes(
                apiKey = apiKey,
                keyword = keyword,
                num = num,
                page = page
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}