package com.example.weatherapp.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationServiceInterface {
    fun getLastLocation()
    fun getLocationUpdates(): Flow<Location>
}