package com.example.weatherapp

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.datastore.DataStoreClass
import com.example.weatherapp.homefragment.view.HomeActivity
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

        lateinit var dataStoreClass: DataStoreClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)

        dataStoreClass= DataStoreClass.getInstance(this)
        lifecycleScope.launch{
            if (dataStoreClass.read("language")=="eng"){
                updateLocale("en")
            }else if (dataStoreClass.read("language")=="ar"){
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