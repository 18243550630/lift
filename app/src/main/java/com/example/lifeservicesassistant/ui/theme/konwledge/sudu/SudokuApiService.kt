package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import retrofit2.http.GET
import retrofit2.http.Query

interface SudokuApiService {
    @GET("sudoku/generate")
    suspend fun generateSudoku(
        @Query("key") key: String,
        @Query("difficulty") difficulty: String = "easy"
    ): SudokuApiResponse
}