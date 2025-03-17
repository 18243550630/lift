package com.example.lifeservicesassistant.ui.theme.jizhangnew

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "expense_database"
        ).build()
    }
}
