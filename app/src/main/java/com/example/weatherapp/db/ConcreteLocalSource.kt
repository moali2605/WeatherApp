package com.example.weatherapp.dp

import android.content.Context
import com.example.weatherapp.db.WeatherDAO
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow


public class ConcreteLocalSource private constructor(context: Context) : LocalSource {

    private val database: DataBase by lazy { DataBase.getInstance(context) }
    private val cityDAO: CityDAO by lazy { database.getCity() }
    private val weatherDAO: WeatherDAO by lazy { database.getWeather() }

    companion object {
        private var concreteLocalSource: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource {
            return concreteLocalSource ?: synchronized(this) {
                concreteLocalSource ?: ConcreteLocalSource(context).also {
                    concreteLocalSource = it
                }
            }
        }

    }

    override suspend fun insertCity(city: City) {
        cityDAO.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        cityDAO.deleteCity(city)
    }

    override fun getStoredCity(): Flow<List<City>> {
        return cityDAO.getAllCity()
    }

    override fun getStoredWeather(): Flow<WeatherDto> {
        return weatherDAO.getStoredWeather()
    }

    override suspend fun insert(weatherDto: WeatherDto) {
         weatherDAO.insert(weatherDto)
    }

    override suspend fun deleteAll() {
        weatherDAO.deleteAll()
    }
}