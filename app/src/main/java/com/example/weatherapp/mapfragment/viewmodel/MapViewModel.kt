package com.example.weatherapp.mapfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.launch

class MapViewModel(var repo: RepositoryInterface) : ViewModel() {
    fun addCityToFavourite(city: City) {
        viewModelScope.launch {
            repo.insertCity(city)
        }
    }
}