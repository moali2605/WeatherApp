    package com.example.weatherapp.datastore

    import android.content.Context
    import androidx.datastore.core.DataStore
    import androidx.datastore.preferences.core.Preferences
    import androidx.datastore.preferences.core.edit
    import androidx.datastore.preferences.core.stringPreferencesKey
    import androidx.datastore.preferences.preferencesDataStore
    import kotlinx.coroutines.flow.first

    class DataStoreClass private constructor(context: Context):DataStoreInterface {

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val dataStore=context.dataStore

        companion object {
            @Volatile
            private var instance: DataStoreClass? = null

            fun getInstance(context: Context): DataStoreClass {
                return instance ?: synchronized(this) {
                    instance ?: DataStoreClass(context).also { instance = it }
                }
            }
        }

        override suspend fun write(key: String, value: String) {
            val dataStoreKey = stringPreferencesKey(key)
            dataStore.edit {
                it[dataStoreKey] = value
            }
        }

        override suspend fun read(key: String): String? {
            val dataStoreKey = stringPreferencesKey(key)
            val preferences = dataStore.data.first()
            return preferences[dataStoreKey]
        }

    }