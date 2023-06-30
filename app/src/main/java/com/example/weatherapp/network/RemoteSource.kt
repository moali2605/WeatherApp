package com.example.weatherapp.network

import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeatherFromApi(lat: Double, lon: Double,lang: String): Flow<Response<WeatherDto>>
}