package com.example.weatherapp.alarmfragment.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentAlarmBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmBinding
    var datePicked: String = ""
    var timePicked: String = ""


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
        var timeList: ArrayList<String> = ArrayList()
        val dialog = Dialog(view.context)
        dialog.setContentView(R.layout.dialog_alarm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnAlarm.setOnClickListener {
            dialog.show()
        }
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Select Appointment time")
                .build()
        val tvDTime: TextView = dialog.findViewById(R.id.tvDTime)
        val tvDDate: TextView = dialog.findViewById(R.id.tvDDate)
        val rbGroup: RadioGroup = dialog.findViewById(R.id.rbGrouqDialog)
        val btnDSave: Button = dialog.findViewById(R.id.btnDSave)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd,MMM,yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        val currentTime = timeFormat.format(calendar.time)

        tvDTime.text=currentTime
        tvDDate.text=currentDate

        tvDTime.setOnClickListener {
            datePicker.show(parentFragmentManager, "date_picker")
        }

        datePicker.addOnPositiveButtonClickListener {
            datePicked = SimpleDateFormat("dd,MMM,yyyy", Locale.getDefault())
                .format(Date(it))
            timePicker.show(parentFragmentManager, "time_picker")
            Toast.makeText(view.context, datePicked, Toast.LENGTH_LONG).show()
            tvDDate.text = datePicked
        }
        timePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            calendar.set(Calendar.MINUTE, timePicker.minute)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            timePicked = timeFormat.format(calendar.time)
            Toast.makeText(view.context, timePicked, Toast.LENGTH_LONG).show()
            tvDTime.text = timePicked
        }

        var alarmAdapter = AlarmAdapter()

        btnDSave.setOnClickListener {
            if (datePicked.isNotEmpty() && timePicked.isNotEmpty()) {
                timeList.add(datePicked + timePicked)
                alarmAdapter.submitList(timeList)
                Log.e("here", timeList.size.toString() )
            }
            dialog.dismiss()
        }


        binding.rvAlarm.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }
}