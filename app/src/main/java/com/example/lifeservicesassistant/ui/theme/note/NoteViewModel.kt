package com.example.lifeservicesassistant.ui.theme.note

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class NoteViewModel(context: Context, private val repository: NoteRepository) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("note_categories", Context.MODE_PRIVATE)

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _categories = MutableStateFlow<List<String>>(loadCategories()) // ✅ 从 SharedPreferences 读取
    val categories: StateFlow<List<String>> = _categories

    val notes: StateFlow<List<Note>> = combine(searchText, selectedCategory) { query, category ->
        repository.getAllNotes().firstOrNull()?.filter { note ->
            (category == null || note.category == category) &&
                    (query.isBlank() || note.title.contains(query, true) || note.content.contains(query, true))
        } ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun updateSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    // ✅ 添加分类 & 存储到 SharedPreferences
    fun addCategory(category: String) {
        if (!_categories.value.contains(category)) {
            _categories.value = _categories.value + category
            saveCategories() // 存储
        }
    }

    // ✅ 删除分类 & 存储到 SharedPreferences
    fun removeCategory(category: String) {
        if (category != "全部") {
            _categories.value = _categories.value - category
            saveCategories() // 存储
        }
    }

    fun addNote(title: String, content: String, category: String? = null) = viewModelScope.launch {
        val note = Note(title = title, content = content, category = category)
        repository.addNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        repository.updateNote(note.copy(updatedTime = System.currentTimeMillis()))
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    // ✅ 从 SharedPreferences 读取分类
    private fun loadCategories(): List<String> {
        val storedCategories = sharedPreferences.getStringSet("categories", setOf()) ?: setOf()
        return listOf("全部") + storedCategories.sorted() // ✅ 确保 "全部" 始终排在第一
    }

    private fun saveCategories() {
        sharedPreferences.edit()
            .putStringSet("categories", _categories.value.filter { it != "全部" }.toSet()) // ✅ 存储时排除 "全部"
            .apply()
    }

}