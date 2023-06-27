package com.example.weatherapp.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(val repo: RepositoryInterface) : ViewModel() {

    val weather: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    init {
        getWeather()
    }

    fun getWeather() {
        viewModelScope.launch {
            repo.getWeather(31.0409, 31.3785, "metric", "eng").catch {
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
}