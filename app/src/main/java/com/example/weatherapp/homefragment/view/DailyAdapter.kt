package com.example.weatherapp.homefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DaysListItemBinding
import com.example.weatherapp.databinding.HourlyListItemBinding
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.pojo.Daily
import com.example.weatherapp.model.pojo.Hourly
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyAdapter(val homeViewModel: HomeViewModel): ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    lateinit var binding: DaysListItemBinding
    private val dates = mutableListOf<Date>()
    var currentTemp:Double?=null
    init {
        val calendar = Calendar.getInstance()
        for (i in 1..8) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            dates.add(calendar.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DaysListItemBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentItem = getItem(position)
        val date = dates[position]
        runBlocking {
            if (homeViewModel.read("temp") == "C") {
                currentTemp = currentItem.temp.day
                binding.tvWeekTemp.text = "${currentTemp!!.toInt()}°C"
            } else if (homeViewModel.read("temp") == "F") {
                currentTemp = ((currentItem.temp.day) * 9 / 5) + 32
                binding.tvWeekTemp.text = "${currentTemp!!.toInt()}°F"
            } else if (homeViewModel.read("temp") == "K") {
                currentTemp = currentItem.temp.day + 273.15
                binding.tvWeekTemp.text = "${currentTemp!!.toInt()}°K"
            }
        }
        binding.tvWeekState.text = currentItem.weather[0].description

        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        val dateStr = dateFormat.format(date)
        binding.tvWeekDate.text = dateStr
        setPhotoIcon(currentItem.weather[0].icon,binding.ivWeek)
       // binding.ivWeek.setAnimation(setIcon(currentItem.weather[0].icon))
    }

    class DailyViewHolder(binding: DaysListItemBinding) : RecyclerView.ViewHolder(binding.root)

    class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem.dt == newItem.dt
        }
    }
}