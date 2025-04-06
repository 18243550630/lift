package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NewsStorage {
    private const val PREF_NAME = "news_prefs"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_HISTORY = "history"
    private const val KEY_USER_PROFILE = "user_profile"
    private val gson = Gson()

    private val STOP_WORDS = setOf(
        "the", "and", "a", "an", "in", "on", "at", "to", "for", "of", "with",
        "的", "是", "在", "了", "和", "有", "这", "为", "我", "我们"
    )

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
            list.add(0, news)
            if (list.size > 50) list.removeLast()
            saveList(context, KEY_HISTORY, list)
            updateUserInterests(context, news)
        }
    }

    fun getUserInterests(context: Context): Map<String, Float> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USER_PROFILE, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, Float>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }

    fun updateUserInterests(context: Context, news: News) {
        val interests = getUserInterests(context).toMutableMap()

        // 1. 衰减旧兴趣
        interests.forEach { (key, value) ->
            interests[key] = value * 0.95f
        }

        // 2. 增加当前新闻类别权重
        interests[news.category] = (interests[news.category] ?: 0f) + 1.5f

        // 3. 从标题提取关键词
        extractKeywords(news.title).forEach { word ->
            interests[word] = (interests[word] ?: 0f) + 0.8f
        }

        // 4. 限制并保存
        saveUserInterests(
            context,
            interests.toList()
                .sortedByDescending { (_, value) -> value }
                .take(100)
                .toMap()
        )
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

    private fun saveUserInterests(context: Context, interests: Map<String, Float>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_PROFILE, gson.toJson(interests)).apply()
    }

    private fun extractKeywords(text: String): List<String> {
        return text.split(" ")
            .map { it.trim() }
            .filter { it.length > 2 && it.length < 15 }
            .filterNot { STOP_WORDS.contains(it.lowercase()) }
    }
}