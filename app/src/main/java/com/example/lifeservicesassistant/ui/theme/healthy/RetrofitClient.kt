package com.example.lifeservicesassistant.ui.theme.healthy

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apis.tianapi.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 👈 打印请求/响应全部内容
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: HealthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 👈 加入 OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HealthApiService::class.java)
    }

    val nutrientApi: NutrientApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://apis.tianapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutrientApiService::class.java)
    }

    val medicineApi: MedicineApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedicineApiService::class.java)
    }

}

