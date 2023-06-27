package com.example.weatherapp.mapfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class MapFactory(private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}