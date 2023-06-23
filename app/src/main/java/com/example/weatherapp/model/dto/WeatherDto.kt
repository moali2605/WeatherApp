package com.example.weatherapp.model.dto

data class WeatherDto(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<MainData>,
    val message: Int
)