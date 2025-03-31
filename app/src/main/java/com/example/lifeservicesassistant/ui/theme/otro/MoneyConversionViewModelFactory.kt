package com.example.lifeservicesassistant.ui.theme.otro

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoneyConversionViewModelFactory(

    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoneyConversionViewModel::class.java)) {
            return MoneyConversionViewModel( application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}