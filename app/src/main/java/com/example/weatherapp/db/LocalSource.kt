package com.example.weatherapp.dp

import com.example.weatherapp.model.dto.City
import kotlinx.coroutines.flow.Flow

interface LocalSource {
   suspend fun insertCity(city: City)
   suspend fun deleteCity(city: City)
   fun getStoredCity(): Flow<List<City>>
}