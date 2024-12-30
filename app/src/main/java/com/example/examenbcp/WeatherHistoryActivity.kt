package com.example.examenbcp

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class WeatherHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_history)

        val listView: ListView = findViewById(R.id.lvWeatherHistory)
        val repository = WeatherRepository(this)

        // Obtener el historial del clima
        val weatherHistory = repository.getWeatherHistory()

        // Crear un adaptador para mostrar los datos
        val adapter = WeatherHistoryAdapter(this, weatherHistory)
        listView.adapter = adapter
    }
}
