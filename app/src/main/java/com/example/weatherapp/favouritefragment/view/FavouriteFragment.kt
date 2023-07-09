package com.example.weatherapp.favouritefragment.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavouriteBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.db.ConcreteLocalSource
import com.example.weatherapp.favouritefragment.viewmodel.FavFactory
import com.example.weatherapp.favouritefragment.viewmodel.FavViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var navController: NavController
    private lateinit var favFactory: FavFactory
    private lateinit var favViewModel: FavViewModel
    private lateinit var favAdapter: FavAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.action_favouriteFragment_to_mapFragment)
        }

        favFactory = FavFactory(
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
        favViewModel = ViewModelProvider(requireActivity(), favFactory)[FavViewModel::class.java]

        favAdapter = FavAdapter({
            lifecycleScope.launch {
                if (favViewModel.read("language") == "eng") {
                    favViewModel.getWeather(it.lat, it.lang, "eng")
                } else if (favViewModel.read("language") == "ar") {
                    favViewModel.getWeather(it.lat, it.lang, "ar")
                }
            }
            navController.navigate(R.id.action_favouriteFragment_to_detailsFragment)
        }, {
            favViewModel.deleteFavCity(it)
        },view.context)


        binding.rvFav.apply {
            adapter = favAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
        lifecycleScope.launch {
            favViewModel.favCity.collectLatest {
                favAdapter.submitList(it)
            }
        }
    }
}