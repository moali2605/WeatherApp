package com.example.weatherapp.db

import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow

interface LocalSource {
   suspend fun insertCity(city: City)
   suspend fun deleteCity(city: City)
   fun getStoredCity(): Flow<List<City>>
   fun getStoredWeather(): Flow<WeatherDto>
   suspend fun insert(weatherDto: WeatherDto)
   suspend fun deleteAll()
   fun getAlarm(): Flow<List<Alarm>>
   suspend fun insertAlarm(alarm: Alarm)
   suspend fun deleteAlarm(alarm: Alarm)
}