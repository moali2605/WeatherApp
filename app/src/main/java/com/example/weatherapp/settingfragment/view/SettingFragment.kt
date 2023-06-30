package com.example.weatherapp.settingfragment.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.FragmentSettingBinding
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
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

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    lateinit var settingViewModel: SettingViewModel
    lateinit var settingFactory: SettingFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            if (settingViewModel.read("language") == "english") {
                withContext(Dispatchers.Main) {
                    binding.rbtnMS.isChecked = true
                }
            } else if (settingViewModel.read("language") == "arabic") {
                withContext(Dispatchers.Main) {
                    binding.rbtnMH.isChecked = true
                }
            }
        }
        lifecycleScope.launch {
            if (settingViewModel.read("temp") == null || settingViewModel.read("language") == null || settingViewModel.read(
                    "wind"
                ) == null
            ) {
                settingViewModel.apply {
                    write("language", "english")
                    write("wind", "meter/s")
                    write("temp", "C")
                }
            }
        }

        binding.rgLocation.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.rbtnGPS.id) {
                lifecycleScope.launch {
                    settingViewModel.write("location", "gps")
                    if (settingViewModel.read("location") == "map") {
                        withContext(Dispatchers.Main) {
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
                }
            } else {
                lifecycleScope.launch {
                    settingViewModel.write("location", "map")
                    if (settingViewModel.read("location") == "gps") {
                        withContext(Dispatchers.Main) {
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
                }
            }
        }

        binding.rgWindSpeed.setOnCheckedChangeListener { group, checkedId ->
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

        binding.rgTemp.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding.rbtnF.id) {
                lifecycleScope.launch {
                    settingViewModel.write("temp", "F")
                }
            } else if (checkedId == binding.rbtnK.id) {
                lifecycleScope.launch {
                    settingViewModel.write("temp", "K")
                }
            } else if (checkedId == binding.rbtnC.id) {
                lifecycleScope.launch {
                    settingViewModel.write("temp", "C")
                }
            }
        }

        binding.rgLanguage.setOnCheckedChangeListener { group, checkedId ->

            if (checkedId == binding.rbtnEng.id) {
                lifecycleScope.launch {
                    settingViewModel.write("language", "english")
                }
            } else if (checkedId == binding.rbtnArabic.id) {
                lifecycleScope.launch {
                    settingViewModel.write("language", "arabic")
                }
            }

        }

    }
}