package com.example.weatherapp.model.repo

import com.example.weatherapp.datastore.DataStoreInterface

class FakeDataStore(private var valueReader: String):DataStoreInterface {

    override suspend fun write(key: String, value: String) {
        valueReader=value
    }

    override suspend fun read(key: String): String? {
        return valueReader
    }

}