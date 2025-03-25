package com.example.lifeservicesassistant.ui.theme.password

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// PasswordDao.kt
@Dao
interface PasswordDao {
    @Query("SELECT * FROM password_books ORDER BY createdAt DESC")
    fun getAllBooks(): Flow<List<PasswordBookEntity>>

    @Query("SELECT * FROM password_items WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun getItemsByBook(bookId: Long): Flow<List<PasswordItemEntity>>

    @Insert
    suspend fun insertBook(book: PasswordBookEntity): Long

    @Insert
    suspend fun insertItem(item: PasswordItemEntity)

    @Delete
    suspend fun deleteBook(book: PasswordBookEntity)

    @Query("DELETE FROM password_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)
}