package com.example.weatherapp.network

import com.example.weatherapp.model.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("forecast")
    suspend fun getWeatherData(@Query("lat") lat: Double,
                               @Query("lon") lon: Double,
                               @Query("appid") apiKey: String): WeatherDto

}