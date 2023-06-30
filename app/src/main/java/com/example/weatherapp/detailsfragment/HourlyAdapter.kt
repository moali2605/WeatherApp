package com.example.weatherapp.detailsfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.HourlyListItemBinding
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.pojo.Hourly
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(val homeViewModel: HomeViewModel): ListAdapter<Hourly, HourlyViewHolder>(WeatherDiffUtil()){
    lateinit var binding: HourlyListItemBinding
    val hours = mutableListOf<Date>()
    val calendar = Calendar.getInstance()
    var currentTemp:Double? = null
    init {
        for (i in 1..48) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            hours.add(calendar.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyListItemBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentItem = getItem(position)
        val date = hours[position]
        runBlocking {
            if (homeViewModel.read("temp") == "C") {
                currentTemp = currentItem.temp
                binding.tvHourlyTemp.text = "${currentTemp!!.toInt()}°C"
            } else if (homeViewModel.read("temp") == "F") {
                currentTemp = ((currentItem.temp) * 9 / 5) + 32
                binding.tvHourlyTemp.text = "${currentTemp!!.toInt()}°F"
            } else if (homeViewModel.read("temp") == "K") {
                currentTemp = currentItem.temp + 273.15
                binding.tvHourlyTemp.text = "${currentTemp!!.toInt()}°K"
            }
        }
        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        val dateStr = dateFormat.format(date)
        binding.tvHourly.text = dateStr
        binding.ivHourly.setAnimation(setIcon(currentItem.weather[0].icon))
    }
    private fun setIcon(id: String): Int {
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
}

class HourlyViewHolder(binding: HourlyListItemBinding) : RecyclerView.ViewHolder(binding.root)


class WeatherDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt==newItem.dt
    }
}
