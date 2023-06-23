package com.example.weatherapp.model.dto

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)