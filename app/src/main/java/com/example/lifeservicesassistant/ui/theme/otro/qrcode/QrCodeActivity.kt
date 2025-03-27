package com.example.lifeservicesassistant.ui.theme.otro.qrcode// MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import com.example.lifeservicesassistant.R

class QrCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 从strings.xml或BuildConfig中获取API KEY
        val apiKey = getString(R.string.qrcode_key) // 替换为你的实际API KEY
        
        setContent {
            MaterialTheme {
                val apiService = remember { NetworkModule.provideQrCodeApiService() }
                val viewModel = remember { QrCodeViewModel(apiService, apiKey) }
                
                QrCodeScreen(viewModel = viewModel)
            }
        }
    }
}