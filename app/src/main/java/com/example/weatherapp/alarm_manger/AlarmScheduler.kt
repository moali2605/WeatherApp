package com.example.weatherapp.alarm_manger

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.weatherapp.model.pojo.Alarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AlarmScheduler(var context: Context) : AlarmSchedulerInterface {

    private val alarmManger = context.getSystemService(AlarmManager::class.java)

    override fun scheduler(alarms: List<Alarm>) {
        for (alarm in alarms) {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarm",alarm)
            }
            val date = SimpleDateFormat(
                "dd,MMM,yyyy hh:mm a",
                Locale.getDefault()
            ).parse("${alarm.date} ${alarm.time}")
            val calendar = Calendar.getInstance(            ).apply {
                if (date != null) {
                    timeInMillis = date.time
                }
            }

            alarmManger.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    alarm.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancelAlarm(alarm: Alarm) {
        alarmManger.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}