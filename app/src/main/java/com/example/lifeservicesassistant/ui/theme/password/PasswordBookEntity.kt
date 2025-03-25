package com.example.lifeservicesassistant.ui.theme.password

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// PasswordBookEntity.kt
@Entity(tableName = "password_books")
data class PasswordBookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

// PasswordItemEntity.kt
@Entity(
    tableName = "password_items",
    foreignKeys = [
        ForeignKey(
            entity = PasswordBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bookId"])]
)
data class PasswordItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val title: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)