package com.example.lifeservicesassistant.ui.theme.password

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PasswordViewModel : ViewModel() {

    // 假设这些数据是保存在本地的，模拟数据管理
    private val _passwordBooks = mutableStateOf<List<PasswordBook>>(emptyList())
    val passwordBooksState: State<List<PasswordBook>> = _passwordBooks

    val passwordBooks: List<PasswordBook> get() = _passwordBooks.value
    // 用于操作密码本和密码项
    fun addPasswordBook(title: String) {
        val newPasswordBook = PasswordBook(id = System.currentTimeMillis(), title = title)
        _passwordBooks.value = _passwordBooks.value + newPasswordBook
    }

    fun addPasswordItem(bookId: Long, title: String, password: String) {
        val updatedBooks = _passwordBooks.value.map { book ->
            if (book.id == bookId) {
                val newPasswordItem = PasswordItem(id = System.currentTimeMillis(), title = title, password = password)
                book.copy(passwords = book.passwords + newPasswordItem)
            } else {
                book
            }
        }
        _passwordBooks.value = updatedBooks
    }

    fun deletePasswordItem(bookId: Long, passwordId: Long) {
        val updatedBooks = _passwordBooks.value.map { book ->
            if (book.id == bookId) {
                val updatedPasswords = book.passwords.filter { it.id != passwordId }
                book.copy(passwords = updatedPasswords)
            } else {
                book
            }
        }
        _passwordBooks.value = updatedBooks
    }
}
