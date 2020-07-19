package com.example.habittrainer.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.util.Log
import com.example.habittrainer.Habit
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun store(habit: Habit): Long {

        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(HabitEntry.TITLE_COLUMN.getName(), habit.title)
            put(HabitEntry.DESCR_COLUMN.getName(), habit.description)
            put(HabitEntry.IMAGE_COLUMN.getName(), toByteArray(habit.image))
        }

        val id = db.transaction {
            insert(HabitEntry.TABLE_NAME, null, values)
        }

        db.close()

        Log.d(TAG, "New habit was stored to db: $habit")

        return id
    }

    private fun toByteArray(image: Bitmap): ByteArray {

        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    private inline fun <T> SQLiteDatabase.transaction(function: SQLiteDatabase.() -> T): T {

        beginTransaction()
        val result = try {
            val returnValue = function()
            setTransactionSuccessful()
            returnValue
        } finally {
            endTransaction()
        }
        close()

        return result
    }
}