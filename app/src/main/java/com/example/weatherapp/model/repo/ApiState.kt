package com.example.weatherapp.model.repo

import com.example.weatherapp.model.dto.WeatherDto


sealed class ApiState {

    class Success(val weather: WeatherDto) : ApiState()
    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()

}
