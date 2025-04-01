// FlowerLanguageViewModelFactory.kt
package com.example.lifeservicesassistant.ui.theme.play

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FlowerLanguageViewModelFactory(
    private val apiKey: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlowerLanguageViewModel::class.java)) {
            return FlowerLanguageViewModel(apiKey, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}