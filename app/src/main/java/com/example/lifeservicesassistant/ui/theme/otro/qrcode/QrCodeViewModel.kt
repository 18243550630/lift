package com.example.lifeservicesassistant.ui.theme.otro.qrcode// QrCodeViewModel.kt
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class QrCodeViewModel(private val apiService: QrCodeApiService, private val apiKey: String) : ViewModel() {
    var uiState by mutableStateOf(QrCodeUiState())

    @OptIn(ExperimentalEncodingApi::class)
    fun generateQrCode(text: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            try {
                val params = mapOf(
                    "key" to apiKey,
                    "text" to text,
                    "type" to "1",
                    "w" to "300"
                )

                // 在ViewModel中添加验证逻辑
              /*  val base64 = response.result?.base64_image
                if (base64 != null) {
                    try {
                        val bytes = Base64.decode(base64, CoroutineStart.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap == null) {
                            Log.e("QRCode", "Base64解码失败：生成的Bitmap为null")
                        } else {
                            Log.d("QRCode", "Base64解码成功，图片尺寸：${bitmap.width}x${bitmap.height}")
                        }
                    } catch (e: Exception) {
                        Log.e("QRCode", "Base64解码异常", e)
                    }
                }*/

                val response = apiService.generateQrCodeGet(params)
                if (response.error_code == 0) {
                    uiState = uiState.copy(
                        isLoading = false,
                        base64Image = response.result?.base64_image
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = response.reason ?: "生成失败"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "网络请求失败: ${e.message}"
                )
            }
        }
    }
    fun updateSettings(
        el: String? = null,
        bgColor: String? = null,
        fgColor: String? = null,
        logoUrl: String? = null,
        size: Int? = null,
        margin: Int? = null,
        logoWidth: Int? = null
    ) {
        uiState = uiState.copy(
            el = el,
            bgColor = bgColor,
            fgColor = fgColor,
            logoUrl = logoUrl,
            size = size,
            margin = margin,
            logoWidth = logoWidth
        )
    }
}

data class QrCodeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val qrCodeUrl: String? = null,
    val base64Image: String? = null,
    val el: String? = null, // h\q\m\l
    val bgColor: String? = null, // ffffff
    val fgColor: String? = null, // 000000
    val logoUrl: String? = null,
    val size: Int? = null, // 300
    val margin: Int? = null, // 10
    val logoWidth: Int? = null // 60
)