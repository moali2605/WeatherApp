package com.example.weatherapp.model.repo

import android.location.Location
import com.example.weatherapp.location.LocationServiceInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocation(private val listLocation:MutableList<Location> = mutableListOf()):LocationServiceInterface {
    override fun getLastLocation() {
        TODO("Not yet implemented")
    }

    override fun getLocationUpdates(): Flow<Location> {
        return flowOf(listLocation[0])
    }
}