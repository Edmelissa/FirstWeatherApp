package com.example.weather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.DayWeatherItemBinding
import java.time.DayOfWeek

class DaysAdapter(private val actionListener: DaysActionListener): RecyclerView.Adapter<DaysAdapter.DaysViewHolder>(){
    var daysList = listOf<Day>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var today = DayOfWeek.MONDAY
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DayWeatherItemBinding.inflate(inflater, parent, false)

        return DaysViewHolder(binding)
    }

    override fun getItemCount() = daysList.size

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) = holder.onBind(daysList[position], today)

    class DaysViewHolder(binding: DayWeatherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val name: TextView = binding.textViewDayName
        private val temperatureMin: TextView = binding.textViewDayTemperatureMin
        private val temperatureMax: TextView = binding.textViewDayTemperatureMax

        fun onBind(day: Day, today: DayOfWeek) {
            name.text = if(day.name != today) day.name.toString() else "TODAY"
            temperatureMin.text = day.temperatureMin.toString() + "°"
            temperatureMax.text = day.temperatureMax.toString() + "°"
        }
    }
}

interface DaysActionListener{
    fun onUpdateDayTemperature(day: DayOfWeek, newTemperatureMin: Double, newTemperatureMax: Double)

    fun onUpdateDayToToday(day: DayOfWeek)
}