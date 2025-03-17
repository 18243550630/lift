package com.example.lifeservicesassistant.logic.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        
        // 列名
        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_ACCOUNT = "account"
        const val COL_PASSWORD = "password"
        const val COL_AVATAR = "avatar"
        const val COL_REGISTER_TIME = "register_time"
        const val COL_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT,
                $COL_ACCOUNT TEXT UNIQUE,
                $COL_PASSWORD TEXT,
                $COL_AVATAR TEXT,
                $COL_REGISTER_TIME INTEGER,
                $COL_STATUS INTEGER DEFAULT 1
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    // 添加用户
    fun addUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ACCOUNT, user.account)
            put(COL_PASSWORD, user.password)
            put(COL_REGISTER_TIME, System.currentTimeMillis())
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // 根据账号查询用户
    fun getUserByAccount(account: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_ACCOUNT = ?",
            arrayOf(account),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                account = cursor.getString(cursor.getColumnIndexOrThrow(COL_ACCOUNT)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                avatar = cursor.getString(cursor.getColumnIndexOrThrow(COL_AVATAR)),
                registerTime = cursor.getLong(cursor.getColumnIndexOrThrow(COL_REGISTER_TIME)),
                status = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STATUS))
            )
        } else {
            null
        }.also { cursor.close() }
    }

    // 更新其他用户信息
    fun updateUserInfo(user: User): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username)
            put(COL_AVATAR, user.avatar)
        }
        return db.update(
            TABLE_NAME,
            values,
            "$COL_ID = ?",
            arrayOf(user.id.toString())
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}