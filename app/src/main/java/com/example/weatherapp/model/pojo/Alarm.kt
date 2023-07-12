package com.example.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var date: String,
    var time: String,
    var kind: String,
    var lat: Double,
    var lon: Double
) : Serializable