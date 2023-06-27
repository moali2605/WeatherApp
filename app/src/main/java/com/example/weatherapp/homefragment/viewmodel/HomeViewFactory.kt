package com.example.weatherapp.homefragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class HomeViewFactory(private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}