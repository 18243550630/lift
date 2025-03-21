package com.example.lifeservicesassistant.ui.theme.note

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lifeservicesassistant.ui.theme.jizhangnew.AppDatabase

@Database(entities = [Note::class], version = 2) // 更新版本号
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var instance: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "notes.db"
                )
                    .addMigrations(MIGRATION_1_2) // 添加迁移
                    //.fallbackToDestructiveMigration() // 仅在开发时使用（删除旧数据）
                    .build().also { instance = it }
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE notes ADD COLUMN category TEXT DEFAULT NULL")
    }
}

