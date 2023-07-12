package com.example.weatherapp.detailsfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.HourlyListItemBinding
import com.example.weatherapp.favouritefragment.viewmodel.FavViewModel
import com.example.weatherapp.homefragment.view.setIcon
import com.example.weatherapp.model.pojo.Hourly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(private val favViewModel: FavViewModel) :
    ListAdapter<Hourly, HourlyViewHolder>(WeatherDiffUtil()) {

    private val hours = mutableListOf<Date>()
    private val calendar = Calendar.getInstance()

    init {
        // Initialize the list of hours
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

        holder.bind(currentItem, date, favViewModel)
    }
}

class HourlyViewHolder(private val binding: HourlyListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(currentItem: Hourly, date: Date, favViewModel: FavViewModel) {
        binding.tvHourly.text = SimpleDateFormat("h a", Locale.getDefault()).format(date)
        binding.ivHourly.setAnimation(setIcon(currentItem.weather[0].icon))

        CoroutineScope(Dispatchers.Main).launch {
            val currentTemp = when (favViewModel.read("temp")) {
                "C" -> "${currentItem.temp.toInt()}°C"
                "F" -> "${(((currentItem.temp) * 9 / 5) + 32).toInt()}°F"
                "K" -> "${(currentItem.temp + 273.15).toInt()}°K"
                else -> ""
            }
            binding.tvHourlyTemp.text = currentTemp
        }
    }
}


class WeatherDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
        return oldItem.dt == newItem.dt
    }
}