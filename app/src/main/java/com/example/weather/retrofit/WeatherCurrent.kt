package com.example.weather.retrofit

import com.google.gson.annotations.SerializedName

data class WeatherCurrentResponse (
    @SerializedName("current")
    val current: WeatherCurrent
)

data class WeatherCurrent(
    @SerializedName("temperature_2m")
    val temperature: Double
)
