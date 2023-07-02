package com.example.weatherapp.main.view

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.dp.ConcreteLocalSource
import com.example.weatherapp.homefragment.view.HomeActivity
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.main.viewmodel.MainFactory
import com.example.weatherapp.main.viewmodel.MainViewModel
import com.example.weatherapp.model.repo.Repository
import com.example.weatherapp.network.NetworkClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel: MainViewModel
    lateinit var mainFactory: MainFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

        mainFactory = MainFactory(
            Repository.getInstance(
                ConcreteLocalSource.getInstance(this),
                NetworkClient,
                LocationService.getInstance(
                    this,
                    LocationServices.getFusedLocationProviderClient(this)
                ),
                DataStoreClass.getInstance(this)
            )
        )

        mainViewModel = ViewModelProvider(this, mainFactory)[MainViewModel::class.java]

        lifecycleScope.launch{
            if (mainViewModel.read("language")=="eng"){
                updateLocale("en")
            }else if (mainViewModel.read("language")=="ar"){
                updateLocale("ar")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch{
            if (mainViewModel.read("language")=="eng"){
                updateLocale("en")
            }else if (mainViewModel.read("language")=="ar"){
                updateLocale("ar")
            }
        }
    }


    fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
    }
}