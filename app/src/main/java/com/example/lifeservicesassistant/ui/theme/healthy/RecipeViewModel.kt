package com.example.lifeservicesassistant.ui.theme.healthy

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


// RecipeViewModel.kt
class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _recipes = mutableStateListOf<Recipe>()
    val recipes: List<Recipe> = _recipes

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var currentPage = 1
    private var canLoadMore = true

    fun searchRecipes(keyword: String? = null, isNewSearch: Boolean = true) {
        if (isLoading.value) return

        if (isNewSearch) {
            currentPage = 1
            canLoadMore = true
        } else if (!canLoadMore) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.searchRecipes(
                keyword = keyword,
                page = currentPage
            )

            _isLoading.value = false

            result.onSuccess { response ->
                if (isNewSearch) {
                    _recipes.clear()
                }

                if (response.result.list.isNotEmpty()) {
                    _recipes.addAll(response.result.list)
                    currentPage++
                } else {
                    canLoadMore = false
                }
            }.onFailure { e ->
                _error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}