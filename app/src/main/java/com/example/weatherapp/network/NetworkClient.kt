package com.example.weatherapp.network

import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient : RemoteSource {
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .build()

    private val apiService: ApiInterface = retrofit.create(ApiInterface::class.java)
    override suspend fun getWeatherFromApi(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherDto>> {
        return flowOf(
            apiService.getWeather(
                lat,
                lon,
                units,
                lang,

                "306181262de40d435bac69900d1ef801"
            )
        )
    }
}