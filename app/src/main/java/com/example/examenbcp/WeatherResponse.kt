package com.example.examenbcp;


data class WeatherResponse(


    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Main(
    val temp: Double, // Temperatura
    val humidity: Int ,// Humedad
    val temp_min: Double,
    val temp_max: Double
)

data class Weather(
    val description: String, // Descripción del clima
    val icon: String
)