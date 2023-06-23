package com.example.weatherapp.homefragment.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.location.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {

    private lateinit var locationService: LocationService
    lateinit var binding: FragmentHomeBinding
    lateinit var locationDialog: Dialog
    lateinit var fusedClient: FusedLocationProviderClient
    private var My_LOCATION_PERMISSION_ID = 5005


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        locationService.getLastLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivTody.setAnimation(R.raw.snow)
        binding.ivTomorrow.setAnimation(R.raw.snow)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translate_card)
        binding.cvHourly.animation = animation
        fusedClient = LocationServices.getFusedLocationProviderClient(view.context)
        locationService=LocationService(requireActivity(),fusedClient,locationCallBack)
        locationDialog = Dialog(view.context)
        locationDialog.setContentView(R.layout.dialog_main)
        locationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cvGetCurrentLocation: CardView = locationDialog.findViewById(R.id.cvGetCurrentLocation)
        val cvPickFromMap: CardView = locationDialog.findViewById(R.id.cvPickFromMap)

    }

    private val locationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val lastLocation = locationResult.lastLocation
            binding.tvTodyCity.text = lastLocation.latitude.toString()
        }
    }

    override fun onStop() {
        super.onStop()
        locationService.getLastLocation()
    }

}