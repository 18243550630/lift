// QrCodeViewModel.kt
package com.example.lifeservicesassistant.ui.theme.otro.qrcode

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.io.encoding.ExperimentalEncodingApi

class QrCodeViewModel(
    private val apiService: QrCodeApiService,
    private val apiKey: String,
    private val db: AppDatabase // 通过依赖注入传递
) : ViewModel() {

    var uiState by mutableStateOf(QrCodeUiState())

    // 历史记录列表
    var historyList by mutableStateOf<List<QrHistory>>(emptyList())
        private set

    init {
        // 初始化时加载历史记录
        viewModelScope.launch {
            db.qrHistoryDao().getAll().collect { list ->
                historyList = list
            }
        }
    }

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

                val response = apiService.generateQrCodeGet(params)
                if (response.error_code == 0) {
                    val base64 = response.result?.base64_image ?: ""
                    // 保存到历史记录
                    addToHistory(text, base64)

                    uiState = uiState.copy(
                        isLoading = false,
                        base64Image = base64
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

    // 保存二维码到相册
    fun saveQrCodeToGallery(context: Context, base64: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "QR_${System.currentTimeMillis()}.png")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                }

                val uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    }
                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(error = "已保存到相册")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(error = "保存失败: ${e.message}")
                }
            }
        }
    }

    // 添加到历史记录
    private fun addToHistory(content: String, base64: String) {
        viewModelScope.launch {
            val history = QrHistory(
                timestamp = System.currentTimeMillis(),
                content = content,
                base64Image = base64
            )
            db.qrHistoryDao().insert(history)
        }
    }

    // 其他原有代码保持不变
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

// 历史记录实体
@Entity(tableName = "qr_history")
data class QrHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val content: String,
    val base64Image: String
)

// DAO接口
@Dao
interface QrHistoryDao {
    @Query("SELECT * FROM qr_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<QrHistory>>

    @Insert
    suspend fun insert(history: QrHistory)
}

// 数据库定义
@Database(entities = [QrHistory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrHistoryDao(): QrHistoryDao
}

// 原有数据类保持不变
data class QrCodeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val qrCodeUrl: String? = null,
    val base64Image: String? = null,
    val el: String? = null,
    val bgColor: String? = null,
    val fgColor: String? = null,
    val logoUrl: String? = null,
    val size: Int? = null,
    val margin: Int? = null,
    val logoWidth: Int? = null
)