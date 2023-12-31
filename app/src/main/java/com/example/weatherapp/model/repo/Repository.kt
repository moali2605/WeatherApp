package com.example.weatherapp.model.repo

import android.location.Location
import com.example.weatherapp.datastore.DataStoreInterface
import com.example.weatherapp.db.LocalSource
import com.example.weatherapp.location.LocationServiceInterface
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository private constructor(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource,
    private var locationServiceInterface: LocationServiceInterface,
    private var dateStoreInterface: DataStoreInterface
) :
    RepositoryInterface {
    companion object {
        private var repository: Repository? = null
        fun getInstance(
            localSource: LocalSource,
            remoteSource: RemoteSource,
            locationServiceInterface: LocationServiceInterface,
            dateStoreInterface: DataStoreInterface
        ): Repository {
            return repository ?: synchronized(this) {
                repository ?: Repository(
                    localSource,
                    remoteSource,
                    locationServiceInterface,
                    dateStoreInterface
                ).also {
                    repository = it
                }
            }
        }
    }


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherDto>> {
        return remoteSource.getWeatherFromApi(lat, lon, units, lang)
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

    override suspend fun write(key: String, value: String) {
        dateStoreInterface.write(key, value)
    }

    override suspend fun read(key: String): String? {
        return dateStoreInterface.read(key)
    }

    override fun getLastLocation() {
        locationServiceInterface.getLastLocation()
    }

    override fun getLocationUpdates(): Flow<Location> {
        return locationServiceInterface.getLocationUpdates()
    }

    override fun getStoredWeather(): Flow<WeatherDto> {
        return localSource.getStoredWeather()
    }

    override suspend fun insert(weatherDto: WeatherDto) {
        localSource.insert(weatherDto)
    }

    override suspend fun deleteAll() {
        localSource.deleteAll()
    }

    override fun getAlarm(): Flow<List<Alarm>> {
        return localSource.getAlarm()
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        localSource.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        localSource.deleteAlarm(alarm)
    }
}