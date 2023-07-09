package com.example.weatherapp.homefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.HourlyListItemBinding
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.pojo.Hourly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(private val homeViewModel: HomeViewModel) :
    ListAdapter<Hourly, HourlyViewHolder>(WeatherDiffUtil()) {

    private val hours = mutableListOf<Date>()
    private val calendar = Calendar.getInstance()

    init {
        for (i in 1..48) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
            hours.add(calendar.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = HourlyListItemBinding.inflate(inflater, parent, false)

        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentItem = getItem(position)
        val date = hours[position]

        holder.bind(currentItem, date, homeViewModel)
    }
}

class HourlyViewHolder(private val binding: HourlyListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: Hourly, date: Date, homeViewModel: HomeViewModel) {
        binding.tvHourly.text = SimpleDateFormat("h a", Locale.getDefault()).format(date)
        binding.ivHourly.setAnimation(setIcon(currentItem.weather[0].icon))
        CoroutineScope(Dispatchers.Main).launch {
            val currentTemp = when (homeViewModel.read("temp")) {
                "C" -> "${currentItem.temp.toInt()}°C"
                "F" -> "${((currentItem.temp) * 9 / 5) + 32}°F"
                "K" -> "${(currentItem.temp + 273.15).toInt()}°K"
                else -> ""
            }
            binding.tvHourlyTemp.text = currentTemp
        }
    }
}

class WeatherDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem == newItem
    }
}