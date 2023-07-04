package com.example.weatherapp.alarm_manger

import com.example.weatherapp.model.pojo.Alarm

interface AlarmSchedulerInterface {
    fun scheduler(alarms: List<Alarm>)
    fun cancelAlarm(alarm: Alarm)
}