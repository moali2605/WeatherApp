package com.example.weatherapp.homefragment.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.DaysListItemBinding
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.model.pojo.Daily
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyAdapter(private val homeViewModel: HomeViewModel): ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    lateinit var binding: DaysListItemBinding

   private var currentTemp:Double?=null




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DaysListItemBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentItem = getItem(position)
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
        binding.tvWeekDate.text=formatDayOfWeek(currentItem!!.dt)
        binding.ivWeek.setAnimation(setIcon(currentItem.weather[0].icon))
    }

    private fun formatDayOfWeek(timestamp: Double): String {
        val sdf = SimpleDateFormat("EEE,d MMM", Locale("en"))
        val calendar: Calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp.toLong() * 1000
        return when (calendar.get(Calendar.DAY_OF_YEAR)) {
            currentDay -> "Today"
            currentDay + 1 -> "Tomorrow"
            else -> sdf.format(calendar.time).uppercase(Locale.ROOT)
        }
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