package com.example.weather.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast?daily=temperature_2m_max,temperature_2m_min")
    suspend fun getWeatherWeek(@Query("latitude") lat: Double,
                               @Query("longitude") long: Double,
                               @Query("forecast_days") days: Int):WeatherWeekResponse

    @GET("forecast?current=temperature_2m")
    suspend fun getWeatherCurrent(@Query("latitude") lat: Double,
                                  @Query("longitude") long: Double):WeatherCurrentResponse


}