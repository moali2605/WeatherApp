package com.example.weatherapp.model.repo

import com.example.weatherapp.model.dto.City
import com.example.weatherapp.model.dto.WeatherDto
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getWeather(lat:Double,lang:Double): WeatherDto
    suspend fun insertCity(city: City)
    suspend fun deleteCity(city:City)
    fun getStoredCity(): Flow<List<City>>
}