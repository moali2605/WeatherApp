package com.example.weatherapp.alarmfragment.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.alarm_manger.AlarmScheduler
import com.example.weatherapp.alarmfragment.viewmodel.AlarmFactory
import com.example.weatherapp.alarmfragment.viewmodel.AlarmViewModel
import com.example.weatherapp.databinding.FragmentAlarmBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AlarmFragment : Fragment() {


    lateinit var binding: FragmentAlarmBinding
    private lateinit var alarmFactory: AlarmFactory
    lateinit var alarmViewModel: AlarmViewModel
    var datePicked: String = ""
    var timePicked: String = ""
    lateinit var kindOfNotification:String
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheduler = AlarmScheduler(requireActivity())

        alarmFactory = AlarmFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(requireActivity()),
                NetworkClient,
                LocationService.getInstance(
                    requireActivity(),
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                ),
                DataStoreClass.getInstance(requireActivity())
            )
        )

        alarmViewModel = ViewModelProvider(this, alarmFactory)[AlarmViewModel::class.java]


        dialog = Dialog(view.context)
        dialog.setContentView(R.layout.dialog_alarm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val hasNotificationPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        val hasExactAlarmPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED

        binding.btnAlarm.setOnClickListener {
            if (!hasNotificationPermission || !hasExactAlarmPermission) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.FOREGROUND_SERVICE
                    ),
                    100
                )
                if (true && !Settings.canDrawOverlays(requireActivity())) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.parse("package:" + requireActivity().packageName)
                    this.startActivity(intent)
                }
                Toast.makeText(view.context,"We Must Have Permission To Show Notification And Alert",Toast.LENGTH_LONG).show()

            } else {
                dialog.show()
            }
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
        val rbNotification:RadioButton=dialog.findViewById(R.id.rbDNotification)
        val rbDialog:RadioButton=dialog.findViewById(R.id.rbDAlert)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd,MMM,yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(calendar.time)
        val currentTime = timeFormat.format(calendar.time)

        tvDTime.text = currentTime
        tvDDate.text = currentDate

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

        val alarmAdapter = AlarmAdapter() {
            alarmViewModel.deleteAlarm(it)
            scheduler.cancelAlarm(it)

        }
        lifecycleScope.launch {
            alarmViewModel.alarm.collectLatest {
                alarmAdapter.submitList(it)
            }
        }

       rbGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==rbNotification.id) {
                kindOfNotification="Notification"
            } else if (checkedId == rbDialog.id) {
                kindOfNotification="Dialog"
            }
        }

        btnDSave.setOnClickListener {
            if (::kindOfNotification.isInitialized){
            if (datePicked.isNotEmpty() && timePicked.isNotEmpty()) {
                val alarm = Alarm(datePicked, timePicked, kindOfNotification, "alarm")
                alarmViewModel.insertAlarm(alarm)
            }
            dialog.dismiss()}else{
                Toast.makeText(context,"chose alarm kind!",Toast.LENGTH_LONG).show()
            }
        }


        binding.rvAlarm.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(view.context).apply {
                orientation = RecyclerView.VERTICAL
            }
        }
    }
}