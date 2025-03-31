// OilPriceViewModelFactory.kt
package com.example.lifeservicesassistant.ui.theme.otro

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OilPriceViewModelFactory(
    private val apiKey: String,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OilPriceViewModel::class.java)) {
            return OilPriceViewModel(apiKey, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}