package com.example.weatherapp.alarmfragment.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.AlarmListItemBinding
import com.example.weatherapp.model.pojo.Alarm

class AlarmAdapter(private val context: Context,private val onClickDelete: (Alarm) -> Unit) :
    ListAdapter<Alarm, ProductViewHolder>(ProductDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = AlarmListItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem,context,onClickDelete)

    }
}

class ProductViewHolder(private val binding: AlarmListItemBinding) : RecyclerView.ViewHolder(binding.root){
    private lateinit var deleteDialog: Dialog
    fun bind(currentItem:Alarm,context: Context,onClickDelete: (Alarm) -> Unit){
        deleteDialog= Dialog(context)
        deleteDialog.setContentView(R.layout.dialog_delete)
        deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.setCancelable(false)
        val btnOkToDelete: CardView =deleteDialog.findViewById(R.id.btnDeleteDialogOK)
        val btnCancelDelete: CardView =deleteDialog.findViewById(R.id.btnDeleteDialogCancel)

        binding.tvALDate.text = currentItem.date
        binding.tvALTime.text = currentItem.time
        binding.btnDeleteCity.setOnClickListener {
            deleteDialog.show()
            btnOkToDelete.setOnClickListener {
                onClickDelete(currentItem)
                deleteDialog.dismiss()
            }
            btnCancelDelete.setOnClickListener {
                deleteDialog.dismiss()
                Toast.makeText(context,"Cancel Deleting !", Toast.LENGTH_SHORT).show()
            }
        }
    }

}


class ProductDiffUtil : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }

}