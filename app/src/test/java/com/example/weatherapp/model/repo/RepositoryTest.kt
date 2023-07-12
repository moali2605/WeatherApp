package com.example.weatherapp.model.repo

import android.location.Location
import androidx.datastore.core.DataStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.pojo.City
import com.example.weatherapp.model.pojo.Current
import com.example.weatherapp.model.pojo.WeatherDto
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private val weatherDto: WeatherDto = WeatherDto(
        Current(0, 0.0, 1, 0.0, 1, 1, 1, 1, 0.0, 0.0, 1, listOf(), 1, 0.0, 0.0),
        listOf(),
        listOf(),
        5.5,
        0.0,
        "",
        0.0,
        listOf()
    )

    var listWeatherDto = mutableListOf(weatherDto)

    val city1: City = City("Egy", 5.5, 5.5)
    val city2: City = City("man", 6.5, 6.5)
    var listCity = mutableListOf(city1, city2)

    val alarm1 = Alarm(1, "25jun", "2am", "notification", 5.5, 5.5)
    val alarm2 = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)

    val listAlarm = mutableListOf(alarm1, alarm2)


    val location1 = Location("fakeProvider").apply {
        latitude = 5.5
        longitude = 5.5
    }

    val location2 = Location("fakeProvider").apply {
        latitude = 6.5
        longitude = 2.5
    }
    lateinit var repo: RepositoryInterface

    @Before
    fun ini() {
        //Given
        val fakeLocalSource = FakeLocalSource(listWeatherDto, listCity, listAlarm)
        val fakeRemoteSource = FakeRemoteSource(weatherDto)
        val fakeLocation = FakeLocation(mutableListOf(location1, location2))
        val fakeDataStore = FakeDataStore("fake")
        repo =
            Repository.getInstance(fakeLocalSource, fakeRemoteSource, fakeLocation, fakeDataStore)
    }


    @Test
    fun getWeather(): Unit = runBlocking {
        //when
        val response = repo.getWeather(5.5, 5.5, "metric", "en")
        response.collectLatest {
            //then
            if (it.isSuccessful) {
                assertEquals(weatherDto, it.body())
            }
        }
    }

    @Test
    fun insertCity() = runBlocking {
        //when
        val city = City("Egy", 5.5, 5.5)
        repo.insertCity(city)
        //then
        assertEquals(2, listCity.size)
    }

    @Test
    fun deleteCity() = runBlocking {
        //when
        val city = City("Egy", 5.5, 5.5)
        repo.deleteCity(city)
        //then
        assertEquals(2, listCity.size)
    }

    @Test
    fun getStoredCity() = runBlocking {
        //when
        val storedCity = repo.getStoredCity()
        storedCity.collectLatest {
            //then
            assertEquals(listCity.size, it.size)
        }
    }

    @Test
    fun write() = runBlocking {
        //when
        repo.write("location", "gps")
        val storedValue = repo.read("location")
        //then
        assertEquals("gps", storedValue)
    }


    @Test
    fun getLocationUpdates() = runBlocking {
        //when
        val location = repo.getLocationUpdates()
        location.collectLatest {
            //then
            assertEquals(location1.latitude, it.latitude, 0.0)
            assertEquals(location1.longitude, it.longitude, 0.0)
        }

    }

    @Test
    fun getStoredWeather() = runBlocking {
        //when
        val weather = repo.getStoredWeather()
        weather.collectLatest {
            //then
            assertEquals(5.5, it.lat, 0.0)
        }
    }

    @Test
    fun insert() = runBlocking {

    }


    @Test
    fun deleteAll() = runBlocking {
        //when
        //repo.deleteAll()
        //then
        //assertEquals(1, listWeatherDto.size)
    }

    @Test
    fun getAlarm() = runBlocking {
        //when
        repo.getAlarm().collectLatest {
            //then
            assertEquals(2,it.size)
        }

    }

    @Test
    fun insertAlarm() = runBlocking{
        //when
        val alarm = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)
        repo.insertAlarm(alarm)
        //then
        assertEquals(2,listAlarm.size)
    }

    @Test
    fun deleteAlarm() = runBlocking{
        //when
        val alarm = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)
        repo.deleteAlarm(alarm)
        //then
        assertEquals(2,listAlarm.size)
    }
}