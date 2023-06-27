package com.example.weatherapp.model.repo


import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepositoryInterface {
    suspend fun getWeather(lat: Double, lon: Double,units:String,lang: String): Flow<Response<WeatherDto>>
    suspend fun insertCity(city: City)
    suspend fun deleteCity(city:City)
    fun getStoredCity(): Flow<List<City>>
}