package com.example.examenbcp
import android.content.ContentValues
import android.content.Context
import com.example.examenbcp.WeatherRecord


class WeatherRepository(context: Context) {
    private val dbHelper = WeatherDatabaseHelper(context)

    fun insertWeather(city: String, temperature: Double, description: String, humidity: Int, timestamp: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(WeatherDatabaseHelper.COLUMN_CITY, city)
            put(WeatherDatabaseHelper.COLUMN_TEMPERATURE, temperature)
            put(WeatherDatabaseHelper.COLUMN_DESCRIPTION, description)
            put(WeatherDatabaseHelper.COLUMN_HUMIDITY, humidity)
            put(WeatherDatabaseHelper.COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(WeatherDatabaseHelper.TABLE_NAME, null, values)
        db.close()
    }

    fun getWeatherHistory(): List<WeatherRecord> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            WeatherDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${WeatherDatabaseHelper.COLUMN_TIMESTAMP} DESC"
        )

        val weatherHistory = mutableListOf<WeatherRecord>()
        while (cursor.moveToNext()) {
            val city = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.COLUMN_CITY))
            val temperature = cursor.getDouble(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.COLUMN_TEMPERATURE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.COLUMN_DESCRIPTION))
            val humidity = 1//cursor.getInt(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.COLUMN_HUMIDITY))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(WeatherDatabaseHelper.COLUMN_TIMESTAMP))
            weatherHistory.add(WeatherRecord(city, temperature, description, humidity, timestamp))
        }
        cursor.close()
        db.close()

        return weatherHistory
    }
}


