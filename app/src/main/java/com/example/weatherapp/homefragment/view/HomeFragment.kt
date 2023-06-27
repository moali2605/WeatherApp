package com.example.weatherapp.homefragment.view


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
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale




class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var homeViewFactory: HomeViewFactory
    lateinit var homeViewModel: HomeViewModel
    lateinit var hourlyAdapter: HourlyAdapter
    lateinit var dailyAdapter: DailyAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        hourlyAdapter = HourlyAdapter()
        binding.rvHourly.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.HORIZONTAL
            }
        }
        dailyAdapter = DailyAdapter()
        binding.rvWeek.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }

        homeViewFactory = HomeViewFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(view.context),
                NetworkClient
            )
        )

        homeViewModel = ViewModelProvider(this, homeViewFactory)[HomeViewModel::class.java]
        lifecycleScope.launch {
            homeViewModel.weather.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        binding.ivTody.setAnimation(R.raw.loading)
                    }
                    is ApiState.Success -> {
                        binding.ivTody.setAnimation(setIcon(it.weather.current.weather[0].icon))
                        binding.ivTomorrow.setAnimation(setIcon(it.weather.daily[1].weather[0].icon))
                        binding.tvTodyCity.text = it.weather.timezone
                        val currentTemp = it.weather.current.temp
                        binding.tvTodayTemp.text = "${currentTemp.toInt()}°C"
                        val currentFeelsLike = it.weather.current.feels_like
                        binding.tvTodyFealLike.text = "${currentFeelsLike.toInt()}°C"
                        binding.tvTodayStatus.text = it.weather.current.weather[0].description
                        binding.tvTodayHumidity.text = "${it.weather.current.humidity}%"
                        binding.tvTodayUV.text = it.weather.current.uvi.toString()
                        binding.tvTodayPressure.text = it.weather.current.pressure.toString()
                        binding.tvTodayWind.text = "${it.weather.current.wind_speed}km/h"
                        val tomorrowTemp = it.weather.daily[1].temp.day
                        binding.tvTomorrowTemp.text = "${tomorrowTemp.toInt()}°C"
                        binding.tvTomorrowState.text = "${it.weather.daily[1].weather[0].description}"
                        binding.tvTomorrowHumidity.text = "${it.weather.daily[1].humidity}%"
                        binding.tvTomorrowUV.text = "${it.weather.daily[1].uvi}"
                        binding.tvTomorrowPressuree.text = "${it.weather.daily[1].pressure}"
                        binding.tvTomorrowWind.text = "${it.weather.daily[1].wind_speed}Km/h"
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
