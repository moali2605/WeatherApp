package com.example.weatherapp.favouritefragment.viewmodel

import app.cash.turbine.test
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.repo.ApiState
import com.example.weatherapp.model.repo.FakeRepository
import com.example.weatherapp.model.repo.MainDispatcherRule
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class FavViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule= MainDispatcherRule()

    lateinit var favViewModel: FavViewModel
    lateinit var repo: RepositoryInterface

    @Before
    fun setViewModel() {
        //given
        repo = FakeRepository()
        favViewModel = FavViewModel(repo)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getFavCity() = runBlockingTest{
        //when
        var result= listOf<City>()
         favViewModel.favCity.test {
             result=this.awaitItem()
         }
        //then
        assertEquals(3, result.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getWeather() = runBlockingTest{
        //when
        favViewModel.getWeather(5.5,5.5,"en")
        var result: ApiState?=null
        favViewModel.weather.test {
            result= this.awaitItem()
        }
        when(result) {
            is ApiState.Success -> {
                //then
               assertEquals(5.5, (result as ApiState.Success).weather.lat,0.0)
            }
            else -> {}
        }
    }

    @Test
     fun deleteFavCity() = runBlockingTest{
        //when
        val city1=City("egy",5.5,5.5)
        favViewModel.deleteFavCity(city1)
        var result= listOf<City>()
        favViewModel.favCity.test {
            result=this.awaitItem()
        }
        //then
        assertEquals(2, result.size)
    }
    @Test
    fun write()= runBlocking{
        //when
       favViewModel.write("location","gps")
        //then
        assertEquals("gps",favViewModel.read("location"))
    }

}