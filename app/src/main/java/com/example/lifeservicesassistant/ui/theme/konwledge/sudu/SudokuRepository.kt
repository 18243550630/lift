package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SudokuRepository(private val apiService: SudokuApiService, private val apiKey: String) {

        suspend fun generateSudoku(difficulty: String): SudokuGame {
            val response = apiService.generateSudoku(apiKey, difficulty)
            if (response.error_code != 0) {
                throw Exception("API Error: ${response.reason}")
            }
            return SudokuGame(
                puzzle = response.result.puzzle,
                solution = response.result.solution,
                difficulty = difficulty
            )
        }


    companion object {
        fun create(apiKey: String): SudokuRepository {
            val apiService = Retrofit.Builder()
                .baseUrl("https://apis.juhe.cn/fapig/")
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SudokuApiService::class.java)

            return SudokuRepository(apiService, apiKey)
        }
    }
}