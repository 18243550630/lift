package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NewsStorage {
    private const val PREF_NAME = "news_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_HISTORY = "history"

    private val gson = Gson()

    fun getFavorites(context: Context): MutableList<News> {
        return getList(context, KEY_FAVORITES)
    }

    fun getHistory(context: Context): MutableList<News> {
        return getList(context, KEY_HISTORY)
    }

    fun saveFavorites(context: Context, list: List<News>) {
        saveList(context, KEY_FAVORITES, list)
    }

    fun saveHistory(context: Context, list: List<News>) {
        saveList(context, KEY_HISTORY, list)
    }


    fun addFavorite(context: Context, news: News) {
        val list = getFavorites(context)
        if (list.none { it.id == news.id }) {
            list.add(news)
            saveList(context, KEY_FAVORITES, list)
        }
    }

    fun addHistory(context: Context, news: News) {
        val list = getHistory(context)
        if (list.none { it.id == news.id }) {
            list.add(0, news) // 最近的排最前
            if (list.size > 50) list.removeLast()
            saveList(context, KEY_HISTORY, list)
        }
    }

    private fun getList(context: Context, key: String): MutableList<News> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<News>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun saveList(context: Context, key: String, list: List<News>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }
}
