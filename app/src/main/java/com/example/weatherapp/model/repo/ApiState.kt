package com.example.weatherapp.model.repo

import com.example.weatherapp.model.pojo.WeatherDto


sealed class ApiState {

    class Success(val weather: WeatherDto) : ApiState()
    class Failure(val msg: String) : ApiState()
    object Loading : ApiState()

}
