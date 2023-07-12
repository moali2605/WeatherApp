package com.example.weatherapp.alarm_manger

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Build
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapp.R
import com.example.weatherapp.db.ConcreteLocalSource
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.network.NetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlarmReceiver : BroadcastReceiver() {
    private var msg: String = "No Alert Weather Is Fine"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarm = intent?.getSerializableExtra("alarm") as Alarm
        val kind = alarm.kind
        val id=alarm.id
        val networkClient = NetworkClient
        val concreteLocalSource: ConcreteLocalSource = ConcreteLocalSource.getInstance(context!!)

        CoroutineScope(Dispatchers.IO).launch {
            concreteLocalSource.deleteAlarm(alarm)
            if (isInternetConnected(context)) {
                networkClient.getWeatherFromApi(alarm.lat, alarm.lon, "metric", "en" )
                    .collectLatest {
                        if (it.isSuccessful) {
                            if (ApiState.Success(it.body()!!).weather.alerts?.get(0)?.description != null) {
                                msg =
                                    ApiState.Success(it.body()!!).weather.alerts?.get(0)?.description.toString()
                            }
                        }
                    }
            }else{
                msg="No Internet Connection !"
            }
            withContext(Dispatchers.Main) {
                if (kind == "Notification") {
                    val notification = NotificationCompat.Builder(context, "alarm")
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Weather App")
                        .setContentText(msg)
                        .build()
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return@withContext
                    }
                    NotificationManagerCompat.from(context).notify(id, notification)
                } else if (kind == "Dialog") {

                    val mediaPlayer = MediaPlayer.create(context, R.raw.alert)
                    mediaPlayer.start()

                    val alertDialog: AlertDialog.Builder =
                        AlertDialog.Builder(context.applicationContext)
                    alertDialog.setTitle("Weather app")
                    alertDialog.setMessage(msg)
                    alertDialog.setIcon(R.drawable.icon)
                    alertDialog.setPositiveButton("OK") { _, _ ->
                        mediaPlayer.stop()
                    }
                    val dialog: AlertDialog = alertDialog.create()
                    dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    dialog.show()
                    dialog.setOnDismissListener {
                        mediaPlayer.stop()
                    }
                }
            }
        }

    }
    private fun isInternetConnected(context: Context?): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}