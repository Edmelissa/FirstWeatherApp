package com.example.weather.retrofit

import com.google.gson.annotations.SerializedName

data class WeatherWeekResponse (
    @SerializedName("daily")
    val daily: WeatherWeek
)

data class WeatherWeek (
    @SerializedName("time")
    val date : List<String>,

    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,

    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>
)