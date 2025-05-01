// QrCodeActivity.kt
package com.example.lifeservicesassistant.ui.theme.otro.qrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.lifeservicesassistant.R

class QrCodeActivity : ComponentActivity() {
    // 数据库实例（直接在Activity初始化）
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "qr-database"
        ).build()
    }

    // 权限请求回调
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // 处理权限拒绝逻辑
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 检查并请求存储权限
        checkStoragePermission()

        setContent {
            MaterialTheme {
                val apiKey = getString(R.string.qrcode_key)
                val apiService = remember { NetworkModule.provideQrCodeApiService() }
                val viewModel = remember {
                    QrCodeViewModel(
                        apiService = apiService,
                        apiKey = apiKey,
                        db = database // 注入数据库实例
                    )
                }

                QrCodeScreen(viewModel = viewModel)
            }
        }
    }

    private fun checkStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 已有权限
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                // 解释需要权限的原因
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}