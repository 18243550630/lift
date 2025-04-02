package com.example.lifeservicesassistant.ui.theme.view

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {
    private val sharedPref: SharedPreferences = 
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveCredentials(account: String, password: String) {
        sharedPref.edit().apply {
            putString("account", account)
            putString("password", password)
            apply()
        }
    }

    fun getAccount(): String? = sharedPref.getString("account", null)
    
    fun getPassword(): String? = sharedPref.getString("password", null)
    
    fun clearCredentials() {
        sharedPref.edit().clear().apply()
    }
}