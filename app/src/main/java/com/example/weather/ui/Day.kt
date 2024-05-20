package com.example.weather.ui

import java.time.DayOfWeek

data class Day(
    var name: DayOfWeek,
    var temperatureMin: Double,
    var temperatureMax: Double
)
