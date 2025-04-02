package com.example.lifeservicesassistant.ui.theme.konwledge.problem

import com.example.lifeservicesassistant.ui.theme.konwledge.medicine.MedicineInstructionApiService
import com.example.lifeservicesassistant.ui.theme.otro.MoneyConversionApiService
import com.example.lifeservicesassistant.ui.theme.otro.OilPriceApiService
import com.example.lifeservicesassistant.ui.theme.play.DreamApiService
import com.example.lifeservicesassistant.ui.theme.play.FlowerLanguageApiService
import com.example.lifeservicesassistant.ui.theme.play.HoroscopeApiService
import com.example.lifeservicesassistant.ui.theme.play.HotNewsApiService
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
    val hotNewsApi: HotNewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HotNewsApiService::class.java)
    }

    val dreamApi: DreamApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DreamApiService::class.java)
    }

    val moneyConversionApi: MoneyConversionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoneyConversionApiService::class.java)
    }
    val oilPriceApi: OilPriceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OilPriceApiService::class.java)
    }
    val garbageApi: GarbageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GarbageApiService::class.java)
    }
    val computerTermApi: ComputerTermApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ComputerTermApiService::class.java)
    }
    val fileExtensionApi: FileExtensionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FileExtensionApiService::class.java)
    }
    val flowerLanguageApi: FlowerLanguageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlowerLanguageApiService::class.java)
    }
    val judgmentQuestionApi: JudgmentQuestionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JudgmentQuestionApiService::class.java)
    }
    val poetryQuestionApi: PoetryQuestionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PoetryQuestionApiService::class.java)
    }

}