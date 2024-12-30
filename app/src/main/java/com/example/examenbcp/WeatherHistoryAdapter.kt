package com.example.examenbcp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import android.widget.ArrayAdapter

class WeatherHistoryAdapter(context: Context, private val history: List<WeatherRecord>) :
    ArrayAdapter<WeatherRecord>(context, 0, history) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.weather_history_item, parent, false)
        val record = history[position]

        val tvCity = view.findViewById<TextView>(R.id.tvCitys)
        val tvTemperature = view.findViewById<TextView>(R.id.tvTemperature)
        val tvTimestamp = view.findViewById<TextView>(R.id.tvTimestamps)

        tvCity.text = "Ciudad: ${record.city}"
        tvTemperature.text = "Temperatura: ${record.temperature}°C"
        tvTimestamp.text = "Fecha: ${record.timestamp}"

        return view
    }
}
