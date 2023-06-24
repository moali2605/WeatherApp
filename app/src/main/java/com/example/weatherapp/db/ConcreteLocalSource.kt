package com.example.weatherapp.dp

import android.content.Context
import com.example.weatherapp.model.dto.City
import kotlinx.coroutines.flow.Flow


class ConcreteLocalSource private constructor(context: Context) : LocalSource {

    private val database: DataBase by lazy { DataBase.getInstance(context) }
    private val productDAO: ProductDAO by lazy { database.getProductDao() }

    companion object {
        private var concreteLocalSource: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource {
            return concreteLocalSource ?: synchronized(this) {
                concreteLocalSource ?: ConcreteLocalSource(context).also {
                    concreteLocalSource = it
                }
            }
        }

    }

    override suspend fun insertCity(city: City) {
        productDAO.insertProduct(city)
    }

    override suspend fun deleteCity(city: City) {
        productDAO.deleteProduct(city)
    }

    override fun getStoredCity(): Flow<List<City>> {
        return productDAO.getAllProduct()
    }
}