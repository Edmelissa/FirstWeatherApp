package com.example.weather.ui

import android.util.Log
import java.time.DayOfWeek
import java.util.Collections

typealias DaysListener = (days: List<Day>, today: DayOfWeek) -> Unit

class Days {
    private val daysList = listOf(
        Day(DayOfWeek.MONDAY, 0.0, 0.0),
        Day(DayOfWeek.TUESDAY, 0.0, 0.0),
        Day(DayOfWeek.WEDNESDAY, 0.0, 0.0),
        Day(DayOfWeek.THURSDAY, 0.0, 0.0),
        Day(DayOfWeek.FRIDAY, 0.0, 0.0),
        Day(DayOfWeek.SATURDAY, 0.0, 0.0),
        Day(DayOfWeek.SUNDAY, 0.0, 0.0),
    )
    private var today = DayOfWeek.MONDAY

    private val listeners = mutableSetOf<DaysListener>()

    fun addListener(listener: DaysListener){
        listeners.add(listener)
        listener.invoke(daysList, today)
    }

    fun getDaysList() : List<Day> = daysList

    fun getToday() : DayOfWeek = today

    fun updateTemperature(day: DayOfWeek, newTemperatureMin: Double, newTemperatureMax: Double){
        daysList.filter { it.name == day }.forEach { it.temperatureMin = newTemperatureMin; it.temperatureMax = newTemperatureMax }

        notifyChanges()
    }

    fun updateDayToToday(day: DayOfWeek){
        val oldNumOfDay = today.value
        val newNumOfDay = day.value

        today = day
        Collections.rotate(daysList, oldNumOfDay-newNumOfDay)

        notifyChanges()
    }

    private fun notifyChanges(){
        listeners.forEach{ it.invoke(daysList, today)}
    }
}