package com.example.examenbcp



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.location.Geocoder
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var searchCity: EditText
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var weatherRepository: WeatherRepository

    private val apiKey = "4d8c3643cc592cf0573d2c3d62725706"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherRepository = WeatherRepository(this)
        mapView = findViewById(R.id.mapView)
        searchCity = findViewById(R.id.searchCity)
        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvDescription = findViewById(R.id.tvDescription)
        tvHumidity = findViewById(R.id.tvHumidity)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        val btnViewHistory: Button = findViewById(R.id.btnViewHistor)
        btnViewHistory.setOnClickListener {
            val weatherHistory = weatherRepository.getWeatherHistory()
            weatherHistory.forEach { record ->
                Log.d("WeatherHistory", "Ciudad: ${record.city}, Temp: ${record.temperature}, Desc: ${record.description}, Fecha: ${record.timestamp}")
            }
            val intent = Intent(this, WeatherHistoryActivity::class.java)
            startActivity(intent)
        }

        // Opción 1: Detectar cambios en el texto (TextWatcher)
        searchCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val cityName = s.toString().trim()
                if (cityName.isNotEmpty()) {
                    searchCity(cityName)
                }
            }
        })

        // Opción 2: Detectar Enter en el teclado
        searchCity.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val cityName = searchCity.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    searchCity(cityName)
                }
                true
            } else {
                false
            }
        }

    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map


        val bolivia = LatLng(-16.290154, -63.588653)
        googleMap.addMarker(MarkerOptions().position(bolivia).title("Bolivia"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bolivia, 5f))
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
            getWeatherByLocation(latLng.latitude, latLng.longitude)
        }
    }
    private fun searchCity(cityName: String) {
        val weatherService = WeatherApi.create()
        weatherService.getWeatherByCit(cityName, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weather ->
                        val latLng = LatLng(weather.coord.lat, weather.coord.lon)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                        googleMap.addMarker(MarkerOptions().position(latLng).title(cityName))
                        getWeatherByLocation(latLng.latitude, latLng.longitude)
                        Toast.makeText(this@MainActivity, "Búsqueda realizada con éxito: $cityName", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("SearchCity", "Error: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Error al buscar la ciudad: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("SearchCity", "Error de red: ${t.message}")
                Toast.makeText(this@MainActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*private fun searchCity(cityName: String) {
        try {
            val geocoder = Geocoder(this)
            val addresses = geocoder.getFromLocationName(cityName, 1)
            if (addresses.isNotEmpty()) {
                val location = addresses[0]
                val latLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                googleMap.addMarker(MarkerOptions().position(latLng).title(cityName))
                getWeatherByLocation(latLng.latitude, latLng.longitude)
            } else {
                Log.e("Geocoder", "No se encontraron resultados para la ciudad: $cityName")
            }
        } catch (e: Exception) {
            Log.e("Geocoder", "Error al buscar la ciudad: ${e.message}")
        }
    }*/

    private fun getWeatherByLocation(lat: Double, lon: Double) {

        val weatherService = WeatherApi.create()
        val repository = WeatherRepository(this) // Inicializar el repositori
        weatherService.getWeatherByCoordinates(lat, lon, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weather ->
                        tvCityName.text = "Ciudad: ${weather.name}"
                        tvTemperature.text = "Temperatura: ${weather.main.temp} °C"
                        tvDescription.text = "Descripción: ${weather.weather[0].description}"
                        tvHumidity.text="Humedad: ${weather.main.humidity}"

                        // Guardar en la base de datos
                        val timestamp = System.currentTimeMillis().toString()
                        repository.insertWeather(
                            city = weather.name,
                            temperature = weather.main.temp,
                            description = weather.weather[0].description,
                            humidity = weather.main.humidity,
                            timestamp = timestamp
                        )
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherError", "Error al obtener el clima: ${t.message}")
            }
        })
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
