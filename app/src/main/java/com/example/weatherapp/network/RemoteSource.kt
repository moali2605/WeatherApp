package com.example.weatherapp.network

import com.example.weatherapp.model.dto.WeatherDto
import kotlinx.coroutines.flow.Flow

interface RemoteSource {
    suspend fun getWeatherFromApi(lat: Double, lon: Double): WeatherDto
}