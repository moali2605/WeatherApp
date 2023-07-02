package com.example.weatherapp.alarmfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.launch

class AlarmViewModel(val repo: RepositoryInterface) : ViewModel() {

    suspend fun write(key: String, value: String) {
        viewModelScope.launch {
            repo.write(key, value)
        }
    }

    suspend fun read(key: String): String? {
        return repo.read(key)
    }

    fun getLastLocation() {
        repo.getLastLocation()
    }
}