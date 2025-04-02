// SecurityPrefs.kt
package com.example.lifeservicesassistant.ui.theme.password

import android.content.Context
import android.content.SharedPreferences

class SecurityPrefs(context: Context) {
    private val sharedPref: SharedPreferences = 
        context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    fun savePhoneNumber(phone: String) {
        sharedPref.edit().putString("user_phone", phone).apply()
    }

    fun getPhoneNumber(): String? = sharedPref.getString("user_phone", null)

    fun saveViewPassword(password: String) {
        sharedPref.edit().putString("view_password", password).apply()
    }

    fun getViewPassword(): String? = sharedPref.getString("view_password", null)

    fun isFirstTime(): Boolean = 
        sharedPref.getString("view_password", null) == null
}