package com.example.weatherapp.alarmfragment.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.AlarmListItemBinding

class AlarmAdapter: ListAdapter<String, ProductViewHolder>(ProductDiffUtil()) {
    lateinit var binding:AlarmListItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlarmListItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        val date = currentItem.substring(0, 11)
        val time = currentItem.substring(11)
        binding.tvALDate.text=date
        binding.tvALTime.text=time
    }
}

class ProductViewHolder(binding: AlarmListItemBinding) : RecyclerView.ViewHolder(binding.root)


class ProductDiffUtil : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem==newItem
    }

}