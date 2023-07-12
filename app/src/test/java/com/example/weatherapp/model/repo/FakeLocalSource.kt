package com.example.weatherapp.model.repo

import com.example.weatherapp.db.LocalSource
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    private val cacheList: MutableList<WeatherDto> = mutableListOf(),
    private val cityList: MutableList<City> = mutableListOf(),
    private val alarmList: MutableList<Alarm> = mutableListOf()
) : LocalSource {

    override suspend fun insertCity(city: City) {
        cityList.add(city)
    }

    override suspend fun deleteCity(city: City) {
        cityList.remove(city)
    }

    override fun getStoredCity(): Flow<List<City>> {
        return flowOf(cityList)
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