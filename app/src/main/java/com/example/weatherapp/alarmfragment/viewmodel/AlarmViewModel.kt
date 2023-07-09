package com.example.weatherapp.alarmfragment.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmViewModel(val repo: RepositoryInterface) : ViewModel() {

    var alarm: MutableStateFlow<List<Alarm>> = MutableStateFlow(emptyList())
    val location: MutableStateFlow<Location?> = MutableStateFlow(null)

    init {
        getAlarm()
    }

    fun getAlarm() {
        viewModelScope.launch {
            repo.getAlarm().collectLatest {
                alarm.value = it
            }
        }
    }

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repo.insertAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repo.deleteAlarm(alarm)
        }
    }

    suspend fun write(key: String, value: String) {
        viewModelScope.launch {
            repo.write(key, value)
        }
    }

    suspend fun read(key: String): String? {
        return repo.read(key)
    }


}