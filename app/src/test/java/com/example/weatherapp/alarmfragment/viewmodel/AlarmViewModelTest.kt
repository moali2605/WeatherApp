package com.example.weatherapp.alarmfragment.viewmodel

import app.cash.turbine.test
import com.example.weatherapp.favouritefragment.viewmodel.FavViewModel
import com.example.weatherapp.model.pojo.Alarm
import com.example.weatherapp.model.repo.FakeRepository
import com.example.weatherapp.model.repo.MainDispatcherRule
import com.example.weatherapp.model.repo.RepositoryInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class AlarmViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule= MainDispatcherRule()

    lateinit var alarmViewModel: AlarmViewModel
    lateinit var repo: RepositoryInterface

    @Before
    fun setViewModel() {
        //given
        repo = FakeRepository()
        alarmViewModel = AlarmViewModel(repo)

    }

    @Test
    fun getAlarm() = runBlocking {
        //when
        var result= listOf<Alarm>()
        alarmViewModel.alarm.test {
            result=this.awaitItem()
        }
        //then
        assertEquals(2,result.size)
    }



    @Test
    fun insertAlarm() = runBlocking {
        //when
        val alarm = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)
        alarmViewModel.insertAlarm(alarm)
        var result= listOf<Alarm>()
        alarmViewModel.alarm.test {
            result=this.awaitItem()
        }
        //then
        assertEquals(3,result.size)
    }

    @Test
    fun deleteAlarm()= runBlocking {
        //when
        val alarm = Alarm(1, "25jun", "2am", "dialog", 5.5, 5.5)
        alarmViewModel.deleteAlarm(alarm)
        var result= listOf<Alarm>()
        alarmViewModel.alarm.test {
            result=this.awaitItem()
        }
        //then
        assertEquals(1,result.size)
    }

    @Test
    fun write() = runBlocking {
        //when
        alarmViewModel.write("location","gps")
        //then
        assertEquals("gps" ,alarmViewModel.read("location"))
    }

}