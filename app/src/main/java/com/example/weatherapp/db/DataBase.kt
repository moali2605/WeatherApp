package com.example.weatherapp.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.model.pojo.City

@Database(entities = [City::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun getCity(): CityDAO

    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null
        fun getInstance(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}