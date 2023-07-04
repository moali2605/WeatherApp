package com.example.weatherapp.homefragment.view

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var homeViewFactory: HomeViewFactory
    lateinit var homeViewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTemp: Double?
        var currentFeelsLike: Double?
        var tomorrowTemp: Double?

        navController = Navigation.findNavController(view)
        binding.ivTody.setAnimation(R.raw.snow)
        binding.ivTomorrow.setAnimation(R.raw.snow)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translate_card)
        binding.cvHourly.animation = animation
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd,MMM,yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        binding.tvTodayDate.text = currentDate

        homeViewFactory = HomeViewFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(requireActivity()),
                NetworkClient,
                LocationService.getInstance(
                    requireActivity(),
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                ),
                DataStoreClass.getInstance(requireActivity())
            )
        )

        homeViewModel =
            ViewModelProvider(requireActivity(), homeViewFactory)[HomeViewModel::class.java]
        hourlyAdapter = HourlyAdapter(homeViewModel)
        binding.rvHourly.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        dailyAdapter = DailyAdapter(homeViewModel)
        binding.rvWeek.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }



        lifecycleScope.launch {
            val preferences = homeViewModel.read("location")
            if (preferences == "gps") {
                withContext(Dispatchers.Main) {
                    binding.btnSetLocation.visibility = View.GONE
                }
            } else if (preferences == "map") {
                withContext(Dispatchers.Main) {
                    binding.btnSetLocation.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            navController.navigate(R.id.action_homeFragment_to_homeMapFragment)
                        }
                    }
                }
            }
        }


        lifecycleScope.launch {
            if (isInternetConnected()) {
                homeViewModel.weather.collectLatest {
                    when (it) {
                        is ApiState.Loading -> {
                            binding.ivTody.setAnimation(R.raw.loading)
                        }

                        is ApiState.Success -> {
                            homeViewModel.deleteAllWeather()
                            homeViewModel.insertWeather(it.weather)
                            binding.ivTody.setAnimation(setIcon(it.weather.current.weather[0].icon))
                            binding.ivTomorrow.setAnimation(setIcon(it.weather.daily[1].weather[0].icon))
                            binding.tvTodyCity.text = it.weather.timezone
                            if (homeViewModel.read("temp") == "C") {
                                currentTemp = it.weather.current.temp
                                currentFeelsLike = it.weather.current.feels_like
                                tomorrowTemp = it.weather.daily[0].temp.day
                                binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°C"
                                binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°C"
                                binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°C"
                            } else if (homeViewModel.read("temp") == "F") {
                                currentTemp = ((it.weather.current.temp) * 9 / 5) + 32
                                currentFeelsLike = ((it.weather.current.feels_like) * 9 / 5) + 32
                                tomorrowTemp = ((it.weather.daily[0].temp.day) * 9 / 5) + 32
                                binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°F"
                                binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°F"
                                binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°F"
                            } else if (homeViewModel.read("temp") == "K") {
                                currentTemp = it.weather.current.temp + 273.15
                                currentFeelsLike = it.weather.current.feels_like + 273.15
                                tomorrowTemp = it.weather.daily[0].temp.day + 273.15
                                binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°K"
                                binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°K"
                                binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°K"
                            }
                            binding.tvTodayStatus.text = it.weather.current.weather[0].description
                            binding.tvTodayHumidity.text = "${it.weather.current.humidity}%"
                            binding.tvTodayUV.text = it.weather.current.uvi.toString()
                            binding.tvTodayPressure.text = it.weather.current.pressure.toString()
                            if (homeViewModel.read("wind") == "meter/s") {
                                binding.tvTodayWind.text =
                                    "${it.weather.current.wind_speed.toInt()}m/s"
                                binding.tvTomorrowWind.text =
                                    "${it.weather.daily[1].wind_speed.toInt()}Km/h"
                            } else if (homeViewModel.read("wind") == "mile/h") {
                                binding.tvTodayWind.text =
                                    "${(it.weather.current.wind_speed) * 2.23694.toInt()}M/h"
                                binding.tvTomorrowWind.text =
                                    "${(it.weather.daily[1].wind_speed) * 2.23694.toInt()}Km/h"
                            }
                            binding.tvTomorrowState.text =
                                "${it.weather.daily[1].weather[0].description}"
                            binding.tvTomorrowHumidity.text = "${it.weather.daily[1].humidity}%"
                            binding.tvTomorrowUV.text = "${it.weather.daily[1].uvi}"
                            binding.tvTomorrowPressuree.text = "${it.weather.daily[1].pressure}"
                            Log.i("here", "adapter")
                            hourlyAdapter.submitList(it.weather.hourly)
                            dailyAdapter.submitList(it.weather.daily)
                        }

                        else -> {
                            Toast.makeText(view.context, "Oooops", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                homeViewModel.locationStored.collectLatest {

                    if (it != null) {
                        binding.ivTody.setAnimation(setIcon(it.current.weather[0].icon))
                        binding.ivTomorrow.setAnimation(setIcon(it.daily[1].weather[0].icon))
                        binding.tvTodyCity.text = it.timezone
                        if (homeViewModel.read("temp") == "C") {
                            currentTemp = it.current.temp
                            currentFeelsLike = it.current.feels_like
                            tomorrowTemp = it.daily[0].temp.day
                            binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°C"
                            binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°C"
                            binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°C"
                        } else if (homeViewModel.read("temp") == "F") {
                            currentTemp = ((it.current.temp) * 9 / 5) + 32
                            currentFeelsLike = ((it.current.feels_like) * 9 / 5) + 32
                            tomorrowTemp = ((it.daily[0].temp.day) * 9 / 5) + 32
                            binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°F"
                            binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°F"
                            binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°F"
                        } else if (homeViewModel.read("temp") == "K") {
                            currentTemp = it.current.temp + 273.15
                            currentFeelsLike = it.current.feels_like + 273.15
                            tomorrowTemp = it.daily[0].temp.day + 273.15
                            binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°K"
                            binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°K"
                            binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°K"
                        }
                        binding.tvTodayStatus.text = it.current.weather[0].description
                        binding.tvTodayHumidity.text = "${it.current.humidity}%"
                        binding.tvTodayUV.text = it.current.uvi.toString()
                        binding.tvTodayPressure.text = it.current.pressure.toString()
                        if (homeViewModel.read("wind") == "meter/s") {
                            binding.tvTodayWind.text =
                                "${it.current.wind_speed.toInt()}m/s"
                            binding.tvTomorrowWind.text =
                                "${it.daily[1].wind_speed.toInt()}Km/h"
                        } else if (homeViewModel.read("wind") == "mile/h") {
                            binding.tvTodayWind.text =
                                "${(it.current.wind_speed) * 2.23694.toInt()}M/h"
                            binding.tvTomorrowWind.text =
                                "${(it.daily[1].wind_speed) * 2.23694.toInt()}Km/h"
                        }
                        binding.tvTomorrowState.text =
                            it.daily[1].weather[0].description
                        binding.tvTomorrowHumidity.text = "${it.daily[1].humidity}%"
                        binding.tvTomorrowUV.text = "${it.daily[1].uvi}"
                        binding.tvTomorrowPressuree.text = "${it.daily[1].pressure}"
                        hourlyAdapter.submitList(it.hourly)
                        dailyAdapter.submitList(it.daily)
                    }
                }
            }
        }
    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


}


fun setIcon(id: String): Int {
    return when (id) {
        "01d" -> R.raw.sunny
        "02d" -> R.raw.fewclouds
        "03d" -> R.raw.scatteredclouds
        "04d" -> R.raw.brokenclouds
        "09d" -> R.raw.showerrain
        "10d" -> R.raw.rain
        "11d" -> R.raw.thunderstorm
        "13d" -> R.raw.snow
        "50d" -> R.raw.mist
        "01n" -> R.raw.nclearsky
        "02n" -> R.raw.nfewclouds
        "03n" -> R.raw.nscatteredclouds
        "04n" -> R.raw.nbrokenclouds
        "09n" -> R.raw.nshowerrain
        "10n" -> R.raw.rain
        "11n" -> R.raw.nthunderstorm
        "13n" -> R.raw.nsnow
        "50n" -> R.raw.nmist
        else -> R.raw.loading
    }
}
