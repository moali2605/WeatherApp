package com.example.weatherapp.alarmfragment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmViewModel(val repo: RepositoryInterface) : ViewModel() {

    var alarm:MutableStateFlow<List<Alarm>> = MutableStateFlow(emptyList())

    init {
        getAlarm()
    }

    fun getAlarm(){
        viewModelScope.launch {
            repo.getAlarm().collectLatest {
                alarm.value=it
            }
        }
    }

    fun insertAlarm(alarm: Alarm){
        viewModelScope.launch {
            repo.insertAlarm(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm){
        viewModelScope.launch {
            repo.deleteAlarm(alarm)
        }
    }

}