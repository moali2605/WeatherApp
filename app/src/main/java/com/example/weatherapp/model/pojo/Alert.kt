package com.example.weatherapp.model.pojo

data class Alert(
    val senderName: String,
    val event: String,
    val startTime: Long,
    val endTime: Long,
    val description: String,
    val tags: List<String>
)
