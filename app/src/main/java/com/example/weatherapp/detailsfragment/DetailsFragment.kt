package com.example.weatherapp.detailsfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.homefragment.view.HomeActivity
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentDetailsBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.favouritefragment.viewmodel.FavFactory
import com.example.weatherapp.favouritefragment.viewmodel.FavViewModel
import com.example.weatherapp.homefragment.view.SlideInItemAnimator
import com.example.weatherapp.homefragment.view.setIcon
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DetailsFragment : Fragment() {
    lateinit var binding: FragmentDetailsBinding
    lateinit var favFactory: FavFactory
    lateinit var favViewModel: FavViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as HomeActivity).bottomNavigationBar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as HomeActivity).bottomNavigationBar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentTemp: Double?
        var currentFeelsLike: Double?
        var tomorrowTemp: Double?

        binding.tvTodayTemp.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.ivTody.setAnimation(R.raw.snow)
        binding.ivTomorrow.setAnimation(R.raw.snow)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.translate_card)
        binding.cvHourly.animation = animation
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd,MMM,yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        binding.tvTodayDate.text = currentDate

        favFactory = FavFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(view.context),
                NetworkClient, LocationService.getInstance(requireActivity(), LocationServices.getFusedLocationProviderClient(requireActivity())),
                DataStoreClass.getInstance(requireActivity())
            )
        )
        favViewModel = ViewModelProvider(requireActivity(), favFactory)[FavViewModel::class.java]

        hourlyAdapter = HourlyAdapter(favViewModel)
        binding.rvHourly.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        dailyAdapter = DailyAdapter(favViewModel)
        binding.rvWeek.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        lifecycleScope.launch {
            favViewModel.weather.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        binding.ivTody.setAnimation(R.raw.loading)
                    }

                    is ApiState.Success -> {
                        binding.ivTody.setAnimation(setIcon(it.weather.current.weather[0].icon))
                        binding.ivTomorrow.setAnimation(setIcon(it.weather.daily[1].weather[0].icon))
                        binding.tvTodyCity.text = it.weather.timezone
                        if (favViewModel.read("temp") == "C") {
                            currentTemp = it.weather.current.temp
                            currentFeelsLike = it.weather.current.feels_like
                            tomorrowTemp = it.weather.daily[0].temp.day
                            binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°C"
                            binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°C"
                            binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°C"
                        } else if (favViewModel.read("temp") == "F") {
                            currentTemp = ((it.weather.current.temp) * 9 / 5) + 32
                            currentFeelsLike = ((it.weather.current.feels_like) * 9 / 5) + 32
                            tomorrowTemp = ((it.weather.daily[0].temp.day) * 9 / 5) + 32
                            binding.tvTodayTemp.text = "${currentTemp!!.toInt()}°F"
                            binding.tvTodyFealLike.text = "${currentFeelsLike!!.toInt()}°F"
                            binding.tvTomorrowTemp.text = "${tomorrowTemp!!.toInt()}°F"
                        } else if (favViewModel.read("temp") == "K") {
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
                        if (favViewModel.read("wind") == "meter/s") {
                            binding.tvTodayWind.text = "${it.weather.current.wind_speed.toInt()}m/s"
                            binding.tvTomorrowWind.text = "${it.weather.daily[1].wind_speed.toInt()}Km/h"
                        } else if (favViewModel.read("wind") == "mile/h") {
                            binding.tvTodayWind.text = "${(it.weather.current.wind_speed)*2.23694.toInt()}M/h"
                            binding.tvTomorrowWind.text = "${(it.weather.daily[1].wind_speed)*2.23694.toInt()}Km/h"
                        }
                        binding.tvTomorrowState.text = "${it.weather.daily[1].weather[0].description}"
                        binding.tvTomorrowHumidity.text = "${it.weather.daily[1].humidity}%"
                        binding.tvTomorrowUV.text = "${it.weather.daily[1].uvi}"
                        binding.tvTomorrowPressuree.text = "${it.weather.daily[1].pressure}"
                        hourlyAdapter.submitList(it.weather.hourly)
                        dailyAdapter.submitList(it.weather.daily)
                    }

                    else -> {
                        Toast.makeText(view.context, "Oooops", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        binding.rvHourly.itemAnimator = SlideInItemAnimator()
    }
}