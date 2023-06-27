package com.example.weatherapp.model.repo

import com.example.weatherapp.dp.LocalSource
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

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


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units:String,
        lang: String
    ): Flow<Response<WeatherDto>> {
        return remoteSource.getWeatherFromApi(lat, lon,units,lang)
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