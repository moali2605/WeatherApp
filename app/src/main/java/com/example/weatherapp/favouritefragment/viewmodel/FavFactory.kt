package com.example.weatherapp.favouritefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class FavFactory(private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavViewModel::class.java)){
            FavViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}