package com.example.weatherapp.favouritefragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.FavListItemBinding
import com.example.weatherapp.databinding.HourlyListItemBinding
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.Hourly
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FavAdapter(private val onClick: (City) -> Unit,private val onClickDelete: (City) -> Unit) : ListAdapter<City, FavViewHolder>(WeatherDiffUtil()){
    lateinit var binding: FavListItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavListItemBinding.inflate(inflater, parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val currentItem = getItem(position)
        binding.tvALTime.text=currentItem.name
        binding.cvFav.setOnClickListener {
            onClick(currentItem)
        }
        binding.btnDeleteCity.setOnClickListener {
            onClickDelete(currentItem)
        }
    }
}

class FavViewHolder(binding: FavListItemBinding) : RecyclerView.ViewHolder(binding.root)


class WeatherDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem==newItem
    }
}