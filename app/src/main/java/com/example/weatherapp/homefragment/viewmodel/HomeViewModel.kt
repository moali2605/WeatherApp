package com.example.weatherapp.homefragment.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.pojo.Current
import com.example.weatherapp.model.pojo.WeatherDto
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(val repo: RepositoryInterface) : ViewModel() {

    val weather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val location: MutableStateFlow<Location?> = MutableStateFlow(null)
    val locationStored: MutableStateFlow<WeatherDto?> = MutableStateFlow(null)

    init {
        getLocationUpdate()
        getStoredWeather()
    }

    fun getWeather(lat: Double, long: Double, language: String) {
        viewModelScope.launch {
            repo.getWeather(lat, long, "metric", language).catch {
                weather.value = ApiState.Failure(it.message!!)
            }.collectLatest {
                if (it.isSuccessful) {
                    weather.value = ApiState.Success(it.body()!!)
                } else {
                    weather.value = ApiState.Failure(it.message())
                }
            }
        }
    }

    fun getLastLocation() {
        repo.getLastLocation()
    }

    private fun getLocationUpdate() {
        viewModelScope.launch {
            repo.getLocationUpdates().collect {
                location.value = it
            }
        }
    }

    suspend fun write(key: String, value: String) {
        viewModelScope.launch {
            repo.write(key, value)
        }
    }

    suspend fun read(key: String): String? {
        return repo.read(key)
    }

    private fun getStoredWeather() {
        viewModelScope.launch {
            repo.getStoredWeather().collectLatest {
                locationStored.value = it
            }
        }
    }

    fun insertWeather(weatherDto: WeatherDto) {
        viewModelScope.launch {
            repo.insert(weatherDto)
        }
    }

    fun deleteAllWeather() {
        viewModelScope.launch {
            repo.deleteAll()
        }
    }


}