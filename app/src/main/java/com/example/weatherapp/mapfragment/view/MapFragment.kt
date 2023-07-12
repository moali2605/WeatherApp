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
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.db.ConcreteLocalSource
import com.example.weatherapp.homefragment.view.HomeActivity
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.mapfragment.viewmodel.MapFactory
import com.example.weatherapp.mapfragment.viewmodel.MapViewModel
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var mapFactory: MapFactory
    private lateinit var mapViewModel: MapViewModel
    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        mapFactory = MapFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(view.context),
                NetworkClient,
                LocationService.getInstance(
                    requireActivity(),
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                ),
                DataStoreClass.getInstance(requireActivity())
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
                Log.d("here", it.toString())
            }
            googleMap.setOnMapLongClickListener {
                googleMap.addMarker(
                    MarkerOptions()
                        .position(it)
                )
                val city = City()
                city.lat = it.latitude
                city.lang = it.longitude
                val geocoder = view?.let { it1 -> Geocoder(it1.context, Locale.getDefault()) }
                val addresses = geocoder?.getFromLocation(it.latitude, it.longitude, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        if (addresses[0].locality != null) {
                            val cityName = addresses[0].locality
                            city.name = cityName
                        } else {
                            city.name = "Not Valid City"
                        }
                    }
                }
                binding.btnAddToFavFromMab.setOnClickListener {
                    mapViewModel.addCityToFavourite(city)
                    navController.navigate(R.id.action_mapFragment_to_favouriteFragment)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeActivity).bottomNavigationBar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as HomeActivity).bottomNavigationBar.visibility = View.VISIBLE
    }
}
