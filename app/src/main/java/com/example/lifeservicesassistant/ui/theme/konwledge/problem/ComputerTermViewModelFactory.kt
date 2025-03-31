// ComputerTermViewModelFactory.kt
package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ComputerTermViewModelFactory(
    private val apiKey: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComputerTermViewModel::class.java)) {
            return ComputerTermViewModel(apiKey, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}