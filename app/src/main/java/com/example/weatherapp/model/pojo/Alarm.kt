package com.example.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class Alarm(
    var date: String,
    @PrimaryKey
    var time: String)
