package com.example.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherDto(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    @PrimaryKey
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Double,
    val alerts: List<Alert>?
)