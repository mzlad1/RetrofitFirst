package com.example.firstretrofit

import android.content.Context

class PreferenceManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("WeatherPref", Context.MODE_PRIVATE)

    fun searchCount(city: String) {
        val count = sharedPreferences.getInt(city, 0)
        sharedPreferences.edit().putInt(city, count + 1).apply()
    }

    fun getSearchCount(city: String): Int = sharedPreferences.getInt(city, 0)
}
