package com.example.weatherapp.alarmfragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.AlarmListItemBinding
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City

class AlarmAdapter(private val onClickDelete: (Alarm) -> Unit): ListAdapter<Alarm, ProductViewHolder>(ProductDiffUtil()) {
    lateinit var binding:AlarmListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlarmListItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)

        binding.tvALDate.text=currentItem.date
        binding.tvALTime.text=currentItem.time
        binding.btnDeleteCity.setOnClickListener {
            onClickDelete(currentItem)
        }
    }
}

class ProductViewHolder(binding: AlarmListItemBinding) : RecyclerView.ViewHolder(binding.root)


class ProductDiffUtil : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem==newItem
    }

}