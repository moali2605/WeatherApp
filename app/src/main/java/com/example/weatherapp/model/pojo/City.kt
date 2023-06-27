package com.example.weatherapp.model.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey
    var name: String = "",
    var lat: Double = 0.0,
    var lang: Double = 0.0
) {
    constructor() : this("", 0.0, 0.0)
}
