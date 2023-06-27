package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ActivityHomeBinding
import com.example.weatherapp.location.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigationrail.NavigationRailMenuView

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var bottomNavigationBar: BottomNavigationView
    lateinit var navController: NavController
    lateinit var locationService: LocationService
    lateinit var locationDialog: Dialog
    lateinit var fusedClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar = binding.bottomNavigationBar
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavigationBar, navController)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        locationService = LocationService(this, fusedClient, locationCallBack)
        locationDialog = Dialog(this)
        locationDialog.setContentView(R.layout.dialog_main)
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cvGetCurrentLocation: CardView = locationDialog.findViewById(R.id.cvGetCurrentLocation)
        val cvPickFromMap: CardView = locationDialog.findViewById(R.id.cvPickFromMap)

    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation
            Log.w("here", "${lastLocation.latitude}   ${lastLocation.longitude}")
        }
    }

    override fun onResume() {
        super.onResume()
        locationService.getLastLocation()
    }

}