package com.example.habittrainer.dao

import android.provider.BaseColumns

const val DATABASE_NAME = "habittrainer.db"
const val DATABASE_VERSION = 10

object HabitEntry : BaseColumns {
    const val TABLE_NAME = "habit"
    val ID_COLUMN = DbColumn("id", DbTypes.INTEGER, pk = true)
    val TITLE_COLUMN = DbColumn("title", DbTypes.VARCHAR, 255)
    val DESCR_COLUMN = DbColumn("description", DbTypes.VARCHAR, 255)
    val IMAGE_COLUMN = DbColumn("image", DbTypes.BLOB)

    fun getCreateQuery(): String =
        "CREATE TABLE $TABLE_NAME (" +
                "$ID_COLUMN, " +
                "$TITLE_COLUMN, " +
                "$DESCR_COLUMN, " +
                "$IMAGE_COLUMN" +
                ");"

    fun getDropQuery(): String =
        "DROP TABLE IF EXISTS $TABLE_NAME;"

    fun getAllColumnNames() : Array<String> {

        return HabitEntry::class.java.declaredFields
            .filter { field -> field::class == DbColumn::class }
            .map { field -> field.name }
            .toTypedArray()
    }

    fun getSortColumnQuery(column: DbColumn, sortType: String) : String =
        "${column.getName()} $sortType"
}

class DbColumn(
    private val name: String,
    private val type: DbTypes,
    private val length: Int = 0,
    private val pk: Boolean = false
) {
    override fun toString(): String =
        if (pk) "$name ${getType()} PRIMARY KEY" else "$name ${getType()}"

    fun getName() = name

    private fun getType(): String {
        return if (length == 0) type.name else "$type($length)"
    }
}

enum class DbTypes {
    INTEGER,
    VARCHAR,
    BLOB;
}