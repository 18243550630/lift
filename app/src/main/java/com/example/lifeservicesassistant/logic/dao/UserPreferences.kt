package com.example.lifeservicesassistant.logic.dao

import android.content.Context

object UserPreferences {
    private const val PREF_NAME = "user_preferences"
    private const val KEY_AGE = "age"
    private const val KEY_HEIGHT = "height"
    private const val KEY_WEIGHT = "weight"
    private const val KEY_BLOOD_PRESSURE = "blood_pressure"
    private const val KEY_BODY_FAT = "body_fat"


    // 保存数据到 SharedPreferences
    fun saveUserInfo(context: Context, age: Int, height: Float, weight: Float, bloodPressure: Int, bodyFat: Float) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_AGE, age)
        editor.putFloat(KEY_HEIGHT, height)
        editor.putFloat(KEY_WEIGHT, weight)
        editor.putInt(KEY_BLOOD_PRESSURE, bloodPressure)
        editor.putFloat(KEY_BODY_FAT, bodyFat)

        editor.apply()
    }

    // 从 SharedPreferences 读取数据
    fun getUserInfo(context: Context): UserInfo {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val age = sharedPreferences.getInt(KEY_AGE, 0)
        val height = sharedPreferences.getFloat(KEY_HEIGHT, 0f)
        val weight = sharedPreferences.getFloat(KEY_WEIGHT, 0f)
        val bloodPressure = sharedPreferences.getInt(KEY_BLOOD_PRESSURE, 0)
        val bodyFat = sharedPreferences.getFloat(KEY_BODY_FAT, 0f)

        return UserInfo(age, height, weight, bloodPressure, bodyFat)
    }
}

// 用于保存和传递用户信息的数据类
data class UserInfo(val age: Int, val height: Float, val weight: Float, val bloodPressure: Int, val bodyFat: Float)
