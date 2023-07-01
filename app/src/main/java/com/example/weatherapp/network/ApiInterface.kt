package com.example.weatherapp.network

import com.example.weatherapp.model.pojo.WeatherDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("appid") appId: String): Response<WeatherDto>
}