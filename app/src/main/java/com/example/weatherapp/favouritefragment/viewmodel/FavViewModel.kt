package com.example.weatherapp.favouritefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavViewModel(val repo: RepositoryInterface) : ViewModel() {
    val favCity: MutableStateFlow<List<City>> = MutableStateFlow(emptyList())
    val weather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    init {
        getFavCity()
    }

    fun getFavCity() {
        viewModelScope.launch {
            repo.getStoredCity().collectLatest {
                favCity.value = it
            }
        }
    }

    fun deleteFavCity(city: City) {
        viewModelScope.launch {
            repo.deleteCity(city)
        }
    }

    fun getWeather(lat: Double, long: Double,language:String) {
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

    suspend fun write(key:String,value:String){
        viewModelScope.launch {
            repo.write(key, value)
        }
    }

    suspend fun read(key:String):String?{
        return repo.read(key)
    }

}