package com.example.weatherapp.settingfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.repo.RepositoryInterface
import java.lang.IllegalArgumentException

class SettingFactory (private val repository: RepositoryInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingViewModel::class.java)){
            SettingViewModel(repository) as T
        }else{
            throw IllegalArgumentException("Not Found")
        }
    }
}