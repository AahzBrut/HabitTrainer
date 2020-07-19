package com.example.habittrainer.dao

import android.provider.BaseColumns

const val DATABASE_NAME = "habittrainer.db"
const val DATABASE_VERSION = 10

object HabitEntry : BaseColumns {
    val TABLE_NAME = "habit"
    val _ID = "id"
    val ID_COLUMN = DbColumn("id", DbTypes.INTEGER, true)
    val TITLE_COLUMN = DbColumn("title", DbTypes.VARCHAR(256))
    val DESCR_COLUMN = DbColumn("description", DbTypes.VARCHAR(128))
    val IMAGE_COLUMN = DbColumn("image", DbTypes.BLOB)

    fun getCreateQuery() : String =
        "CREATE TABLE $TABLE_NAME (" +
            "$ID_COLUMN, " +
            "$TITLE_COLUMN, " +
            "$DESCR_COLUMN, " +
            "$IMAGE_COLUMN " +
            ");"

    fun getDropQuery() : String =
        "DROP TABLE IF EXISTS $TABLE_NAME;"
}

class DbColumn (
    private val name : String,
    private val type : DbTypes,
    private val pk: Boolean = false
) {
    override fun toString(): String =
        if (pk) "$name $type PRIMARY KEY" else "$name $type"

    fun getName() = name
}

enum class DbTypes(length: Int? = null) {

    INTEGER(null),
    VARCHAR(null),
    BLOB(null);

    private var length: Int? = null

    init {
        this.length = length
    }

    override fun toString(): String = if (length == null) name else "$name($length)"
    operator fun invoke(length: Int): DbTypes {
        this.length = length
        return this
    }
}