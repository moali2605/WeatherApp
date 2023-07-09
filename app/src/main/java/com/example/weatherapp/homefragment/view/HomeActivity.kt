package com.example.weatherapp.homefragment.view

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.weatherapp.R
import com.example.weatherapp.alarm_manger.AlarmScheduler
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.db.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import android.Manifest.permission.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var bottomNavigationBar: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var locationDialog: Dialog
    private lateinit var homeViewFactory: HomeViewFactory
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scheduler = AlarmScheduler(this)

        homeViewFactory = HomeViewFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(this),
                NetworkClient,
                LocationService.getInstance(
                    this,
                    LocationServices.getFusedLocationProviderClient(this)
                ),
                DataStoreClass.getInstance(this)
            )
        )

        homeViewModel = ViewModelProvider(this, homeViewFactory)[HomeViewModel::class.java]




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm channel"
            val descriptionText = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }


        val concreteLocalSource: ConcreteLocalSource = ConcreteLocalSource.getInstance(this)
        lifecycleScope.launch {
            concreteLocalSource.getAlarm().collectLatest {
                scheduler.scheduler(it)
            }
        }



        lifecycleScope.launch {
            if (homeViewModel.read("language") == "eng") {
                updateLocale("en")
            } else if (homeViewModel.read("language") == "ar") {
                updateLocale("ar")
            }
        }


        bottomNavigationBar = binding.bottomNavigationBar
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigationBar, navController)

        locationDialog = Dialog(this)
        locationDialog.setContentView(R.layout.dialog_main)
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cvGetCurrentLocation: CardView = locationDialog.findViewById(R.id.cvGetCurrentLocation)
        val cvPickFromMap: CardView = locationDialog.findViewById(R.id.cvPickFromMap)

        lifecycleScope.launch {
            if (homeViewModel.read("location") == "gps") {
                homeViewModel.getLastLocation()
            }
        }

        lifecycleScope.launch {
            if (homeViewModel.read("temp") == null || homeViewModel.read("language") == null || homeViewModel.read(
                    "wind"
                ) == null
            ) {
                homeViewModel.apply {
                    write("language", "eng")
                    write("wind", "meter/s")
                    write("temp", "C")
                }
            }
        }
        if (isInternetConnected()) {
            lifecycleScope.launch {
                val preferences = homeViewModel.read("location")
                if (preferences == null) {
                    withContext(Dispatchers.Main) {
                        locationDialog.show()
                        cvGetCurrentLocation.setOnClickListener {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    homeViewModel.getLastLocation()
                                    homeViewModel.write("location", "gps")
                                    homeViewModel.location.collect {
                                        Log.e("latitude", it?.latitude.toString())
                                        if (it != null) {
                                            homeViewModel.write("gpsLocationLat",it.latitude.toString())
                                            homeViewModel.write("gpsLocationLon",it.longitude.toString())

                                            if (homeViewModel.read("language") == "eng") {
                                                homeViewModel.getWeather(
                                                    it.latitude,
                                                    it.longitude,
                                                    "eng"
                                                )

                                            } else if (homeViewModel.read("language") == "ar") {
                                                homeViewModel.getWeather(
                                                    it.latitude,
                                                    it.longitude,
                                                    "ar"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            locationDialog.dismiss()

                        }
                        cvPickFromMap.setOnClickListener {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    homeViewModel.write("location", "map")
                                }
                            }
                            navController.navigate(R.id.homeMapFragment)
                            locationDialog.dismiss()
                        }
                    }
                } else if (preferences == "gps") {
                    homeViewModel.getLastLocation()
                    homeViewModel.location.collectLatest {
                        if (it != null) {
                            homeViewModel.write("gpsLocationLat",it.latitude.toString())
                            homeViewModel.write("gpsLocationLon",it.longitude.toString())
                            if (homeViewModel.read("language") == "eng") {
                                homeViewModel.getWeather(it.latitude, it.longitude, "eng")
                            } else if (homeViewModel.read("language") == "ar") {
                                homeViewModel.getWeather(it.latitude, it.longitude, "ar")
                            }
                        }
                    }
                } else if (preferences == "map") {
                    if (homeViewModel.read("lat") == null || homeViewModel.read("long") == null) {
                        navController.navigate(R.id.homeMapFragment)
                    } else {
                        if (homeViewModel.read("language") == "eng") {
                            homeViewModel.getWeather(
                                homeViewModel.read("lat")!!.toDouble(),
                                homeViewModel.read("long")!!.toDouble(), "eng"
                            )
                        } else if (homeViewModel.read("language") == "ar") {
                            homeViewModel.getWeather(
                                homeViewModel.read("lat")!!.toDouble(),
                                homeViewModel.read("long")!!.toDouble(), "ar"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (homeViewModel.read("location") == "gps") {
                homeViewModel.getLastLocation()
            }
        }
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
    }

}
