package com.example.firstretrofit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var searchButton: Button
    private lateinit var cityInput: EditText
    private lateinit var weatherInfoTextView: TextView
    private lateinit var weatherService: WeatherService
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(this)


        cityInput = findViewById(R.id.cityInput)
        searchButton = findViewById(R.id.searchButton)
        weatherInfoTextView = findViewById(R.id.weatherInfo)


        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherService = retrofit.create(WeatherService::class.java)

        searchButton.setOnClickListener {
            val cityName = cityInput.text.toString().trim()
            if (cityName.isNotEmpty()) {
                fetchWeather(cityName)
            } else {
                Toast.makeText(this, "Please enter a city name.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(cityName: String) {
        weatherService.getWeatherByCity(cityName, "77108619684bf7062202b2c1d1f84899").enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        preferenceManager.searchCount(cityName)
                        val count = preferenceManager.getSearchCount(cityName)
                        val weatherDescription = responseBody.weather.firstOrNull()?.description ?: "No description available"
                        val temperature = responseBody.main.temp - 273.15
                        val weatherText = "Weather in $cityName: $weatherDescription, Temperature: $temperature"

                        weatherInfoTextView.text = weatherText
                        Toast.makeText(this@MainActivity, "$cityName has been searched $count times", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@MainActivity, "No response data available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Failed to get weather", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
