package com.example.lifeservicesassistant.ui.theme.jizhangnew

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val amount: Double,
    val remark: String,
    val date: String // Store date as String (ISO format)
)
