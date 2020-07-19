package com.example.habittrainer.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.habittrainer.Habit
import java.io.ByteArrayOutputStream

class HabitDbTable(context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun readAll(): List<Habit> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            HabitEntry.TABLE_NAME,
            HabitEntry.getAllColumnNames(),
            null,
            null,
            null,
            null,
            HabitEntry.getSortColumnQuery(HabitEntry.ID_COLUMN, "ASC")
        )

        val habits = getHabitsFrom(cursor)

        db.close()

        return habits
     }

    private fun getHabitsFrom(cursor: Cursor): MutableList<Habit> {

        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(HabitEntry.TITLE_COLUMN)
            val description = cursor.getString(HabitEntry.DESCR_COLUMN)
            val bitmap = cursor.getBitmap(HabitEntry.IMAGE_COLUMN)
            habits.add(Habit(title, description, bitmap))
        }
        cursor.close()
        return habits
    }

    private fun Cursor.getString(column: DbColumn) : String =
        this.getString(this.getColumnIndex(column.getName()))

    private fun Cursor.getBitmap(column: DbColumn) : Bitmap {
        val byteArray = this.getBlob(this.getColumnIndex(column.getName()))
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

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