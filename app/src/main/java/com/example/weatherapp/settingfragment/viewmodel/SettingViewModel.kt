package com.example.weatherapp.settingfragment.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(val repo: RepositoryInterface) :ViewModel(){

    val location: MutableStateFlow<Location?> = MutableStateFlow(null)

    suspend fun write(key:String,value:String){
        viewModelScope.launch {
            repo.write(key, value)
        }
    }

    suspend fun read(key:String):String?{
        return repo.read(key)
    }
    fun getLastLocation(){
        repo.getLastLocation()
    }

}