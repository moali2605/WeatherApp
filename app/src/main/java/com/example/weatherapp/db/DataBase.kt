package com.example.weatherapp.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.weatherapp.db.AlarmDAO
import com.example.weatherapp.db.WeatherDAO
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.Current
import com.example.weatherapp.model.pojo.Daily
import com.example.weatherapp.model.pojo.Hourly
import com.example.weatherapp.model.pojo.WeatherDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [City::class,WeatherDto::class,Alarm::class], version = 1)
@TypeConverters(Converters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun getCity(): CityDAO
    abstract fun getWeather() :WeatherDAO
    abstract fun getAlarm():AlarmDAO
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
class Converters {
    @TypeConverter
    fun fromCurrent(current: Current): String {
        return Gson().toJson(current)
    }

    @TypeConverter
    fun toCurrent(json: String): Current {
        return Gson().fromJson(json, Current::class.java)
    }
    @TypeConverter
    fun fromDailyList(dailyList: List<Daily>): String {
        return Gson().toJson(dailyList)
    }

    @TypeConverter
    fun toDailyList(json: String): List<Daily> {
        val type = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(json, type)
    }
    @TypeConverter
    fun fromHourlyList(hourlyList: List<Hourly>): String {
        return Gson().toJson(hourlyList)
    }

    @TypeConverter
    fun toHourlyList(json: String): List<Hourly> {
        val type = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(json, type)
    }
}