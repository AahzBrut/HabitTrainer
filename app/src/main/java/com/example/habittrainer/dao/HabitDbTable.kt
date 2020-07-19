package com.example.habittrainer.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.habittrainer.Habit
import com.example.habittrainer.dao.HabitEntry.DESCR_COLUMN
import com.example.habittrainer.dao.HabitEntry.ID_COLUMN
import com.example.habittrainer.dao.HabitEntry.IMAGE_COLUMN
import com.example.habittrainer.dao.HabitEntry.TABLE_NAME
import com.example.habittrainer.dao.HabitEntry.TITLE_COLUMN
import com.example.habittrainer.getInitialHabits
import java.io.ByteArrayOutputStream

class HabitDbTable(private val context: Context) {

    private val TAG = HabitDbTable::class.java.simpleName

    private val dbHelper = HabitTrainerDb(context)

    fun readAll(): List<Habit> {

        val db = dbHelper.readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            HabitEntry.getAllColumnNames(),
            null,
            null,
            null,
            null,
            HabitEntry.getSortColumnQuery(ID_COLUMN, "ASC")
        )

        val habits = getHabitsFrom(cursor)

        db.close()

        return habits
    }

    fun store(habit: Habit): Long {

        val db = dbHelper.writableDatabase

        val values = ContentValues()
        with(values) {
            put(TITLE_COLUMN.getName(), habit.title)
            put(DESCR_COLUMN.getName(), habit.description)
            put(IMAGE_COLUMN.getName(), toByteArray(habit.image))
        }

        val id = db.transaction {
            insert(TABLE_NAME, null, values)
        }

        db.close()

        Log.d(TAG, "New habit was stored to db: $habit")

        return id
    }

    fun init() {

        if (isEmpty()) {
            getInitialHabits(context).forEach { store(it) }
        }
    }

    private fun isEmpty(): Boolean {

        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(HabitEntry.getIsEmptyQuery(), null)
        val result = cursor.count == 0

        cursor.close()
        db.close()

        return result
    }

    private fun getHabitsFrom(cursor: Cursor): MutableList<Habit> {

        val habits = mutableListOf<Habit>()
        while (cursor.moveToNext()) {
            val title = cursor.getString(TITLE_COLUMN)
            val description = cursor.getString(DESCR_COLUMN)
            val bitmap = cursor.getBitmap(IMAGE_COLUMN)
            habits.add(Habit(title, description, bitmap))
        }
        cursor.close()
        return habits
    }

    private fun Cursor.getString(column: DbColumn): String =
        this.getString(this.getColumnIndex(column.getName()))

    private fun Cursor.getBitmap(column: DbColumn): Bitmap {
        val byteArray = this.getBlob(this.getColumnIndex(column.getName()))
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
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