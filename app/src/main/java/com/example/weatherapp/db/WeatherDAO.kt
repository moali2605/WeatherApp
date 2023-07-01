package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.Weather
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {

    @Query("SELECT * FROM weather")
    fun getStoredWeather(): Flow<WeatherDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weatherDto: WeatherDto)

    @Query("DELETE FROM weather")
    suspend fun deleteAll()
}