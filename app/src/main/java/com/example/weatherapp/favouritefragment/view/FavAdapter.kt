package com.example.weatherapp.favouritefragment.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FavListItemBinding
import com.example.weatherapp.model.pojo.City

class FavAdapter(
    private val onClick: (City) -> Unit,
    private val onClickDelete: (City) -> Unit,
    private val context: Context
) : ListAdapter<City, FavAdapter.FavViewHolder>(WeatherDiffUtil()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = FavListItemBinding.inflate(inflater, parent, false)
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, context, onClick, onClickDelete)
    }

    inner class FavViewHolder(private val binding: FavListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var deleteDialog: Dialog
        fun bind(
            currentItem: City,
            context: Context,
            onClick: (City) -> Unit,
            onClickDelete: (City) -> Unit
        ) {

            deleteDialog = Dialog(context)
            deleteDialog.setContentView(R.layout.dialog_delete)
            deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            deleteDialog.setCancelable(false)
            val btnOkToDelete: CardView = deleteDialog.findViewById(R.id.btnDeleteDialogOK)
            val btnCancelDelete: CardView = deleteDialog.findViewById(R.id.btnDeleteDialogCancel)

            binding.tvALTime.text = currentItem.name
            binding.cvFav.setOnClickListener {
                if (isInternetConnected()){
                onClick(currentItem)
                }else{
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_LONG).show()

                }

            }
            binding.btnDeleteCity.setOnClickListener {
                deleteDialog.show()
                btnOkToDelete.setOnClickListener {
                    onClickDelete(currentItem)
                    deleteDialog.dismiss()
                }
                btnCancelDelete.setOnClickListener {
                    deleteDialog.dismiss()
                    Toast.makeText(context, "Cancel Deleting !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun isInternetConnected(): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

    }

}


class WeatherDiffUtil : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}