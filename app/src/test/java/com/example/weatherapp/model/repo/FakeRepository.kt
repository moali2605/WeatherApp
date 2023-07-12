package com.example.weatherapp.model.repo

import android.location.Location
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.Current
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRepository : RepositoryInterface {


    val city1=City("egy",5.5,5.5)
    val city2=City("egy",5.5,5.5)
    val city3=City("egy",5.5,5.5)

    val alarm1 = Alarm(1, "25jun", "2am", "notification", 5.5, 5.5)
    val alarm2 = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)


    private val weatherDto: WeatherDto = WeatherDto(
        Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
        listOf(),
        listOf(),
        5.5,
        0.0,
        "",
        0.0,
        listOf()
    )

    private val cacheList: MutableList<WeatherDto> = mutableListOf()
    private val cityList: MutableList<City> = mutableListOf(city1,city2,city3)
    private val alarmList: MutableList<Alarm> = mutableListOf(alarm1,alarm2)
    private var valueReaded: String? = null
    private val listLocation: MutableList<Location> = mutableListOf()



    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherDto>> {
        return flowOf(Response.success(weatherDto))
    }

    override suspend fun insertCity(city: City) {
        cityList.add(city)
    }

    override suspend fun deleteCity(city: City) {
        cityList.remove(city)
    }

    override fun getStoredCity(): Flow<List<City>> {
        return flowOf(cityList)
    }

    override suspend fun write(key: String, value: String) {
        valueReaded = value
    }

    override suspend fun read(key: String): String? {
        return valueReaded
    }

    override fun getLastLocation() {
        TODO("Not yet implemented")
    }

    override fun getLocationUpdates(): Flow<Location> {
        return flowOf(listLocation[0])
    }

    override fun getStoredWeather(): Flow<WeatherDto> {
        return flowOf(cacheList[0])
    }

    override suspend fun insert(weatherDto: WeatherDto) {
        cacheList.add(weatherDto)
    }

    override suspend fun deleteAll() {
        cacheList.clear()
    }

    override fun getAlarm(): Flow<List<Alarm>> {
        return flowOf(alarmList)
    }

    override suspend fun insertAlarm(alarm: Alarm) {
        alarmList.add(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarmList.remove(alarm)
    }


}