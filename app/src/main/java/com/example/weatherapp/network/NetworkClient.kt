package com.example.weatherapp.network

import com.example.weatherapp.model.dto.WeatherDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient :RemoteSource{
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build()

    override suspend fun getWeatherFromApi(lat: Double, lon: Double): WeatherDto {
        return  retrofit.create(ApiInterface::class.java).getWeatherData(lat,lon,"71e77c6129acd4f8203f949fee5acc34")
    }
}