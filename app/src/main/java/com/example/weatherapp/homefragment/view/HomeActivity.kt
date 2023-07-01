package com.example.weatherapp.homefragment.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var bottomNavigationBar: BottomNavigationView
    lateinit var navController: NavController
    lateinit var locationDialog: Dialog
    lateinit var homeViewFactory: HomeViewFactory
    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationBar = binding.bottomNavigationBar
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigationBar, navController)

        locationDialog = Dialog(this)
        locationDialog.setContentView(R.layout.dialog_main)
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cvGetCurrentLocation: CardView = locationDialog.findViewById(R.id.cvGetCurrentLocation)
        val cvPickFromMap: CardView = locationDialog.findViewById(R.id.cvPickFromMap)

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
                                        if (homeViewModel.read("language") == "eng") {
                                            homeViewModel.getWeather(it.latitude, it.longitude,"eng")
                                        }else if(homeViewModel.read("language") == "ar"){
                                            homeViewModel.getWeather(it.latitude, it.longitude,"ar")
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
                    Log.e("latitude", it?.latitude.toString())
                    if (it != null) {
                        if (homeViewModel.read("language") == "eng") {
                            homeViewModel.getWeather(it.latitude, it.longitude,"eng")
                        }else if(homeViewModel.read("language") == "ar"){
                            homeViewModel.getWeather(it.latitude, it.longitude,"ar")
                        }
                    }
                }
            } else if (preferences == "map") {
                navController.navigate(R.id.homeMapFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (homeViewModel.read("location") == "gps") {
                homeViewModel.getLastLocation()
            }
        }
    }
}
