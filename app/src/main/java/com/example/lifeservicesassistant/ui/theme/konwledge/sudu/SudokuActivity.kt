package com.example.lifeservicesassistant.ui.theme.konwledge.sudu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeservicesassistant.R
import com.example.lifeservicesassistant.ui.theme.MyAppTheme

class SudokuActivity : ComponentActivity() {
     val viewModel: SudokuViewModel by viewModels {
        val apiKey = getString(R.string.juhe_sudu_key)
        SudokuViewModelFactory(
            SudokuRepository.create(apiKey)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                SudokuScreen(viewModel = viewModel)
            }
        }
    }
}

// ViewModel 工厂
class SudokuViewModelFactory(
    private val repository: SudokuRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SudokuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SudokuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}