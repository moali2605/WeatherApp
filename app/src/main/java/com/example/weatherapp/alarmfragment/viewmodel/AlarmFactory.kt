package com.example.weatherapp.alarmfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class AlarmFactory(private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlarmViewModel::class.java)){
            AlarmViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}