package com.example.lifeservicesassistant.ui.theme.otro.qrcode

// QrCodeRequest.kt
data class QrCodeRequest(
    val key: String,
    val text: String? = null,
    val el: String? = null, // h\q\m\l
    val bgcolor: String? = null, // ffffff
    val fgcolor: String? = null, // 000000
    val logo: String? = null,
    val w: Int? = null, // 300
    val m: Int? = null, // 10
    val lw: Int? = null, // 60
    val type: Int? = null // 1 or 2
)

// QrCodeResponse.kt
data class QrCodeResponse(
    val error_code: Int,
    val reason: String?,
    val result: QrCodeResult?
)

data class QrCodeResult(
    val base64_image: String?,
    val image_url: String?
)