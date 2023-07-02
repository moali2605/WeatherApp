package com.example.weatherapp.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class MainFactory(private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            MainViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}