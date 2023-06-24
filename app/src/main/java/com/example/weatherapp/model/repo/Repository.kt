package com.example.weatherapp.model.repo

import com.example.weatherapp.dp.LocalSource
import com.example.weatherapp.model.dto.City
import com.example.weatherapp.model.dto.WeatherDto
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(val localSource: LocalSource, val remoteSource: RemoteSource) :
    RepositoryInterface {
    companion object {
        private var repository: Repository? = null
        fun getInstance(localSource: LocalSource, remoteSource: RemoteSource): Repository {
            return repository ?: synchronized(this) {
                repository ?: Repository(localSource, remoteSource).also {
                    repository = it
                }
            }
        }
    }

    override suspend fun getWeather(lat: Double, lang: Double): WeatherDto {
        return remoteSource.getWeatherFromApi(lat, lang)
    }

    override suspend fun insertCity(city: City) {
        localSource.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        localSource.deleteCity(city)
    }

    override fun getStoredCity(): Flow<List<City>> {
        return localSource.getStoredCity()
    }
}