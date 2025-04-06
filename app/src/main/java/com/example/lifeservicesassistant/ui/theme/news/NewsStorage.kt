package com.example.lifeservicesassistant.ui.theme.news

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NewsStorage {
    private const val PREF_NAME = "news_prefs"
    private const val KEY_HISTORY = "history"
    private const val KEY_USER_PROFILE = "user_profile"
    private const val KEY_FAVORITE_FOLDERS = "favorite_folders"

    private val gson = Gson()

    fun getHistory(context: Context): MutableList<News> {
        return getList(context, KEY_HISTORY)
    }

    fun saveHistory(context: Context, list: List<News>) {
        saveList(context, KEY_HISTORY, list)
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

        interests.forEach { (key, value) ->
            interests[key] = value * 0.95f
        }

        interests[news.category] = (interests[news.category] ?: 0f) + 1.5f

        extractKeywords(news.title).forEach { word ->
            interests[word] = (interests[word] ?: 0f) + 0.8f
        }

        saveUserInterests(
            context,
            interests.toList()
                .sortedByDescending { (_, value) -> value }
                .take(100)
                .toMap()
        )
    }

    private fun extractKeywords(text: String): List<String> {
        val stopWords = setOf("the", "and", "a", "an", "in", "on", "at", "to", "for", "of", "with",
            "的", "是", "在", "了", "和", "有", "这", "为", "我", "我们")

        return text.split(" ")
            .map { it.trim() }
            .filter { it.length > 2 && it.length < 15 }
            .filterNot { stopWords.contains(it.lowercase()) }
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

    // ---------------- 收藏夹功能 ----------------

    fun getFavoriteFolders(context: Context): MutableMap<String, MutableList<News>> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITE_FOLDERS, null) ?: return mutableMapOf()
        val type = object : TypeToken<MutableMap<String, MutableList<News>>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    fun saveFavoriteFolders(context: Context, folders: Map<String, MutableList<News>>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FAVORITE_FOLDERS, gson.toJson(folders)).apply()
    }

    fun createFavoriteFolder(context: Context, folderName: String) {
        val folders = getFavoriteFolders(context)
        if (!folders.containsKey(folderName)) {
            folders[folderName] = mutableListOf()
            saveFavoriteFolders(context, folders)
        }
    }

    fun addFavoriteToFolder(context: Context, folderName: String, news: News) {
        val folders = getFavoriteFolders(context)
        val list = folders.getOrPut(folderName) { mutableListOf() }
        if (list.none { it.id == news.id }) {
            list.add(news)
            saveFavoriteFolders(context, folders)
        }
    }

    fun removeFavoriteFromFolder(context: Context, folderName: String, newsId: String) {
        val folders = getFavoriteFolders(context)
        folders[folderName]?.removeIf { it.id == newsId }
        saveFavoriteFolders(context, folders)
    }

    fun deleteFavoriteFolder(context: Context, folderName: String) {
        val folders = getFavoriteFolders(context)
        folders.remove(folderName)
        saveFavoriteFolders(context, folders)
    }

    fun getFavoritesInFolder(context: Context, folderName: String): List<News> {
        return getFavoriteFolders(context)[folderName] ?: emptyList()
    }
}
