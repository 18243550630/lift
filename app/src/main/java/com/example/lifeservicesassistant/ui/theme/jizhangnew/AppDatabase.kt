package com.example.lifeservicesassistant.ui.theme.jizhangnew

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false) // 禁用 schema 导出
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}