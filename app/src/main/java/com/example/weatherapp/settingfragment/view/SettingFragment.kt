package com.example.weatherapp.settingfragment.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.db.ConcreteLocalSource
import com.example.weatherapp.homefragment.viewmodel.HomeViewFactory
import com.example.weatherapp.homefragment.viewmodel.HomeViewModel
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.example.weatherapp.settingfragment.viewmodel.SettingFactory
import com.example.weatherapp.settingfragment.viewmodel.SettingViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var settingFactory: SettingFactory
    private lateinit var homeViewFactory: HomeViewFactory
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var navController: NavController
    private lateinit var restartDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        settingFactory = SettingFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(requireActivity()),
                NetworkClient,
                LocationService.getInstance(
                    requireActivity(),
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                ),
                DataStoreClass.getInstance(requireContext())
            )
        )

        settingViewModel = ViewModelProvider(this, settingFactory)[SettingViewModel::class.java]

        homeViewFactory = HomeViewFactory(
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

        homeViewModel =
            ViewModelProvider(requireActivity(), homeViewFactory)[HomeViewModel::class.java]

        restartDialog = Dialog(requireActivity())
        restartDialog.setContentView(R.layout.dialog_restart)
        restartDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        restartDialog.setCancelable(false)
        val btnRestartDialogOK: CardView = restartDialog.findViewById(R.id.btnRestartDialogOK)
        val btnRestartDialogCancel: CardView =
            restartDialog.findViewById(R.id.btnRestartDialogCancel)

        lifecycleScope.launch {
            if (settingViewModel.read("location") == "gps") {
                withContext(Dispatchers.Main) {
                    binding.rbtnGPS.isChecked = true
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.rbtnMap.isChecked = true
                }
            }
            if (settingViewModel.read("temp") == "C") {
                withContext(Dispatchers.Main) {
                    binding.rbtnC.isChecked = true
                }
            } else if (settingViewModel.read("temp") == "F") {
                withContext(Dispatchers.Main) {
                    binding.rbtnF.isChecked = true
                }
            } else if (settingViewModel.read("temp") == "K") {
                withContext(Dispatchers.Main) {
                    binding.rbtnK.isChecked = true
                }
            }
            if (settingViewModel.read("wind") == "meter/s") {
                withContext(Dispatchers.Main) {
                    binding.rbtnMS.isChecked = true
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.rbtnMH.isChecked = true
                }
            }
            if (settingViewModel.read("language") == "eng") {
                binding.rbtnEng.isChecked = true
            } else if (settingViewModel.read("language") == "ar") {
                binding.rbtnArabic.isChecked = true
            }
        }

        binding.rgLocation.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbtnGPS.id) {
                if (isInternetConnected()) {
                    lifecycleScope.launch {
                        settingViewModel.write("location", "gps")
                        settingViewModel.getLastLocation()
                        homeViewModel.location.collect {
                            Log.e("latitude", it?.latitude.toString())
                            if (it != null) {
                                if (homeViewModel.read("language") == "eng") {
                                    homeViewModel.getWeather(
                                        it.latitude,
                                        it.longitude,
                                        "eng"
                                    )
                                } else if (homeViewModel.read("language") == "ar") {
                                    homeViewModel.getWeather(
                                        it.latitude,
                                        it.longitude,
                                        "ar"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
                    binding.rbtnMap.isChecked = true
                    binding.rbtnGPS.isChecked = false
                }
            } else {
                if (isInternetConnected()) {
                    lifecycleScope.launch {
                        settingViewModel.write("location", "map")
                        if (settingViewModel.read("location") == "gps") {
                            withContext(Dispatchers.Main) {
                                navController.navigate(R.id.action_settingFragment_to_homeMapFragment)
                            }
                        }
                    }
                }else{
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
                    binding.rbtnGPS.isChecked = true
                    binding.rbtnMap.isChecked = false
                }
            }
        }

        binding.rgWindSpeed.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbtnMH.id) {
                lifecycleScope.launch {
                    settingViewModel.write("wind", "mile/h")
                }
            } else if (checkedId == binding.rbtnMS.id) {
                lifecycleScope.launch {
                    settingViewModel.write("wind", "meter/s")
                }
            }
        }

        binding.rgTemp.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbtnF.id -> {
                    lifecycleScope.launch {
                        settingViewModel.write("temp", "F")
                    }
                }

                binding.rbtnK.id -> {
                    lifecycleScope.launch {
                        settingViewModel.write("temp", "K")
                    }
                }

                binding.rbtnC.id -> {
                    lifecycleScope.launch {
                        settingViewModel.write("temp", "C")
                    }
                }
            }
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbtnEng.id) {
                if (isInternetConnected()) {
                    restartDialog.show()
                    btnRestartDialogOK.setOnClickListener {
                        lifecycleScope.launch {
                            settingViewModel.write("language", "eng")
                            updateLocale("en")
                            restart()
                            restartDialog.dismiss()
                        }
                    }
                    btnRestartDialogCancel.setOnClickListener {
                        binding.rbtnArabic.isChecked = true
                        restartDialog.dismiss()
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
                    binding.rbtnEng.isChecked = true
                }
            } else if (checkedId == binding.rbtnArabic.id) {
                if (isInternetConnected()) {
                    restartDialog.show()
                    btnRestartDialogOK.setOnClickListener {
                        lifecycleScope.launch {
                            settingViewModel.write("language", "ar")
                            updateLocale("ar")
                            restart()
                            restartDialog.dismiss()
                        }
                    }
                    btnRestartDialogCancel.setOnClickListener {
                        binding.rbtnEng.isChecked = true
                        restartDialog.dismiss()
                    }
                } else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show()
                    binding.rbtnEng.isChecked = true
                }
            }
        }

    }

    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
    }

    private fun restart() {
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(
            requireActivity().packageName
        )
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().finish()
        if (intent != null) {
            startActivity(intent)
        }
    }
}