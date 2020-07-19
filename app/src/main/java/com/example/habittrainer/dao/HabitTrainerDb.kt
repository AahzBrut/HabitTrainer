package com.example.habittrainer.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class HabitTrainerDb (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = HabitTrainerDb::class.java.simpleName

    override fun onCreate(db: SQLiteDatabase) {

        Log.d(TAG, "Query: ${HabitEntry.getCreateQuery()}")
        db.execSQL(HabitEntry.getCreateQuery())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        Log.d(TAG, "Query: ${HabitEntry.getDropQuery()}")
        db.execSQL(HabitEntry.getDropQuery())
        onCreate(db)
    }
}