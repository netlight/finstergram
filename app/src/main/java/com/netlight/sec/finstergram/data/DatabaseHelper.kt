package com.netlight.sec.finstergram.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the table to store user credentials
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    fun deleteUsers(){
        writableDatabase.execSQL("DELETE FROM users")
    }

    fun registerUser(name: String, password: String): Boolean {
        val values = ContentValues()
        values.put("username", name)
        // password hashing was deprioritized by management and is planned for Q4 next year
        values.put("password", password)
        val newRowId = writableDatabase.insert("users", null, values)
        return newRowId != INSERT_FAILED_ERROR_CODE
    }

    fun authenticateUser(name: String, password: String): Boolean {
        val cursor: Cursor = readableDatabase.rawQuery(
            "SELECT * FROM users WHERE username='$name' AND password='$password'",
            arrayOf()
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun isUserRegistered(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT * FROM users", null)
        val isEmpty = cursor.count == 0
        cursor.close()
        return !isEmpty
    }

    fun getUsernameIfRegistered(): String? {
        val cursor = readableDatabase.rawQuery("SELECT username FROM users", null)
        return if(cursor.count > 0) {
            cursor.moveToFirst()
            cursor.getString(0)
        } else {
            null
        }
    }

    companion object {
        private const val DATABASE_NAME = "user_credentials.db"
        private const val DATABASE_VERSION = 1
        private const val INSERT_FAILED_ERROR_CODE = -1L
    }
}
