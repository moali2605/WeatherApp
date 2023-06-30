package com.example.weatherapp.datastore

interface DataStoreInterface {
    suspend fun write(key: String, value: String)
    suspend fun read(key: String): String?
}