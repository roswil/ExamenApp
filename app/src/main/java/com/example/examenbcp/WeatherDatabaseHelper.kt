package com.example.examenbcp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WeatherDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "weather.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "weather_history"
        const val COLUMN_ID = "id"
        const val COLUMN_CITY = "city"
        const val COLUMN_TEMPERATURE = "temperature"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_HUMIDITY = "humidity"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CITY TEXT,
                $COLUMN_TEMPERATURE REAL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_HUMIDITY INTEGER,
                $COLUMN_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
