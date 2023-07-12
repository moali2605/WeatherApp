package com.example.weatherapp.model.repo

import com.example.weatherapp.model.pojo.WeatherDto
import com.example.weatherapp.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class FakeRemoteSource(private val weatherDto: WeatherDto) :RemoteSource {
    override suspend fun getWeatherFromApi(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherDto>> {
        return flowOf(Response.success(weatherDto))
    }
}