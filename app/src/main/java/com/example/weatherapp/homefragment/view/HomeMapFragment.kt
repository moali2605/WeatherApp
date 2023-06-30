package com.example.weatherapp.homefragment.view


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
import com.example.weatherapp.databinding.FragmentHomeMapBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions


class HomeMapFragment : Fragment() {

    lateinit var binding: FragmentHomeMapBinding
    lateinit var homeViewFactory: HomeViewFactory
    lateinit var homeViewModel: HomeViewModel
    lateinit var navController: NavController



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentHomeMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        homeViewFactory = HomeViewFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(view.context),
                NetworkClient, LocationService.getInstance(requireActivity(), LocationServices.getFusedLocationProviderClient(requireActivity())),
                DataStoreClass.getInstance(requireActivity())
            )
        )
        homeViewModel = ViewModelProvider(requireActivity(), homeViewFactory)[HomeViewModel::class.java]
        googleMapHandler()
    }
    private fun googleMapHandler() {
        val supportMapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapHome) as SupportMapFragment
        supportMapFragment.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener {
                Log.d("here", it.toString())
            }
            googleMap.setOnMapLongClickListener {location->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(location)
                )
                binding.btnAddLocation.setOnClickListener {
                    homeViewModel.getWeather(location.latitude,location.longitude)
                    navController.navigate(R.id.action_homeMapFragment_to_homeFragment)
                }
            }
        }
    }

}

