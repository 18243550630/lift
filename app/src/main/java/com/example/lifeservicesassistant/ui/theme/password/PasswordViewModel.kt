package com.example.lifeservicesassistant.ui.theme.password

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// PasswordViewModel.kt
class PasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).passwordDao()

    private val _passwordBooks = mutableStateOf<List<PasswordBook>>(emptyList())
    val passwordBooksState: State<List<PasswordBook>> = _passwordBooks

    private val _currentBookItems = mutableStateOf<List<PasswordItem>>(emptyList())
    val currentBookItemsState: State<List<PasswordItem>> = _currentBookItems

    init {
        loadAllBooks()
    }

    private fun loadAllBooks() {
        viewModelScope.launch {
            dao.getAllBooks().collect { bookEntities ->
                _passwordBooks.value = bookEntities.map { entity ->
                    PasswordBook(
                        id = entity.id,
                        title = entity.title,
                        passwords = emptyList() // 延迟加载
                    )
                }
            }
        }
    }

    fun loadBookItems(bookId: Long) {
        viewModelScope.launch {
            dao.getItemsByBook(bookId).collect { itemEntities ->
                _currentBookItems.value = itemEntities.map { entity ->
                    PasswordItem(
                        id = entity.id,
                        title = entity.title,
                        password = entity.password
                    )
                }
            }
        }
    }

    fun addPasswordBook(title: String) {
        viewModelScope.launch {
            val id = dao.insertBook(PasswordBookEntity(title = title))
            // Flow会自动更新列表
        }
    }

    fun addPasswordItem(bookId: Long, title: String, password: String) {
        viewModelScope.launch {
            dao.insertItem(
                PasswordItemEntity(
                    bookId = bookId,
                    title = title,
                    password = password
                )
            )
            // Flow会自动更新列表
        }
    }

    fun deletePasswordItem(bookId: Long, itemId: Long) {
        viewModelScope.launch {
            dao.deleteItem(itemId)
            // Flow会自动更新列表
        }
    }

    fun isPhoneMatch(inputPhone: String): Boolean {
        val prefs = SecurityPrefs(getApplication())
        return prefs.getPhoneNumber() == inputPhone
    }

    fun resetViewPassword(newPassword: String) {
        val prefs = SecurityPrefs(getApplication())
        prefs.saveViewPassword(newPassword)
    }


}