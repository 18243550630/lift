package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.MedicineInstructionApiService
import com.example.lifeservicesassistant.ui.theme.play.HoroscopeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apis.tianapi.com/"

    val qaApi: QAApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QAApiService::class.java)
    }

    val synonymAntonymApi: SynonymAntonymApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SynonymAntonymApiService::class.java)
    }
    val medicineInstructionApi: MedicineInstructionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MedicineInstructionApiService::class.java)
    }
    val horoscopeApi: HoroscopeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HoroscopeApiService::class.java)
    }
}