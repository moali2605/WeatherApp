package com.example.weatherapp.mapfragment.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.mapfragment.viewmodel.MapFactory
import com.example.weatherapp.mapfragment.viewmodel.MapViewModel
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapFragment : Fragment() {

    lateinit var binding: FragmentMapBinding
    lateinit var mapFactory: MapFactory
    lateinit var mapViewModel: MapViewModel
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        mapFactory = MapFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(view.context),
                NetworkClient
            )
        )
        mapViewModel = ViewModelProvider(this, mapFactory)[MapViewModel::class.java]
        googleMapHandler()
    }

    private fun googleMapHandler() {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener {
                Log.d("hahaha", it.toString())
            }
            googleMap.setOnMapLongClickListener {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(it)
                )
                val city=City()
                city.lat=it.latitude
                city.lang=it.longitude
                val geocoder = view?.let { it1 -> Geocoder(it1.context , Locale.getDefault()) }
                val addresses = geocoder?.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val cityName = addresses[0].locality
                        city.name=cityName
                    }
                }
                binding.btnAddToFavFromMab.setOnClickListener {
                    mapViewModel.addCityToFavourite(city)
                    navController.navigate(R.id.action_mapFragment_to_favouriteFragment)
                }
            }
        }
    }
}
