package com.example.weatherapp.alarmfragment.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAlarmBinding
import com.google.android.material.datepicker.MaterialDatePicker

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmBinding

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlarmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.dialog_alarm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnAlarm.setOnClickListener {
            dialog.show()
        }
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        val tvDTime:TextView=dialog.findViewById(R.id.tvDTime)
        val tvDDate:TextView=dialog.findViewById(R.id.tvDDate)
        val rbGroup:RadioGroup=dialog.findViewById(R.id.rbGrouqDialog)
        val btnDSave:Button=dialog.findViewById(R.id.btnDSave)

        tvDTime.setOnClickListener {
            datePicker.show(parentFragmentManager, "date_picker_tag")
        }


    }
}