package com.example.weatherapp.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.pojo.City
import kotlinx.coroutines.flow.Flow

@Dao
public interface CityDAO {
    @Query("SELECT * FROM city")
    fun getAllCity(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)

}