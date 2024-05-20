package com.example.weather

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.retrofit.WeatherApi
import com.example.weather.ui.Days
import com.example.weather.ui.DaysActionListener
import com.example.weather.ui.DaysAdapter
import com.example.weather.ui.DaysListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var daysDataSource: Days
    private lateinit var adapter: DaysAdapter

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var location: Location
    private lateinit var city: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        daysDataSource = Days()

        adapter = DaysAdapter(
            object : DaysActionListener {
                override fun onUpdateDayTemperature(day: DayOfWeek, newTemperatureMin: Double, newTemperatureMax: Double) {
                    daysDataSource.updateTemperature(day, newTemperatureMin, newTemperatureMax)
                }

                override fun onUpdateDayToToday(day: DayOfWeek) {
                    daysDataSource.updateDayToToday(day)
                }
            }
        )

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerViewDays.adapter = adapter
        binding.recyclerViewDays.layoutManager = layoutManager

        daysDataSource.addListener(daysListener)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLocation()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val weatherApi = retrofit.create(WeatherApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            val weatherCurrentResponse = weatherApi.getWeatherCurrent(location.latitude, location.longitude)
            val weatherWeekResponse = weatherApi.getWeatherWeek(location.latitude, location.longitude, 7)

            runOnUiThread {
                binding.textViewLocation.text = city
                binding.textViewTemperature.text = weatherCurrentResponse.current.temperature.toString() + "°"

                for(i in 0..6){
                    val day = weatherWeekResponse.daily.date[i]
                    val temperatureMax = weatherWeekResponse.daily.temperatureMax[i]
                    val temperatureMin = weatherWeekResponse.daily.temperatureMin[i]

                    val formatter = SimpleDateFormat("yyyy-MM-dd")
                    val calendar: Calendar = Calendar.getInstance()
                    calendar.setTime(formatter.parse(day))

                    val numDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
                    val dayOfWeek = DayOfWeek.of(if(numDayOfWeek == 0) 7 else numDayOfWeek)

                    if(i == 0){
                        daysDataSource.updateDayToToday(dayOfWeek)
                    }

                    daysDataSource.updateTemperature(dayOfWeek, temperatureMin, temperatureMax)
                }
            }
        }
    }

    private val daysListener: DaysListener = { days, today ->
        adapter.daysList = days
        adapter.today = today
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

     private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    location = task.result
                    val geoCoder = Geocoder(this, Locale.getDefault())
                    val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)
                    city = address?.get(0)?.adminArea ?: "None"
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}