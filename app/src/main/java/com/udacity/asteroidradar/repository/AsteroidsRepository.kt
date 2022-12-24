package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.ObserverType
import com.udacity.asteroidradar.utils.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.utils.getTodayString
import com.udacity.asteroidradar.utils.isAfter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidDatabase) {
    // ViewModel will directly observe this data
    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    /**
     * UI will get data from here
     */
    suspend fun getAsteroids(filter: ObserverType) {
        var listResult = listOf<Asteroid>()
        withContext(Dispatchers.IO) {
            println("Query database -> START")
            listResult = when (filter) {
                ObserverType.TODAY -> {
                    database.asteroidDao.getTodayAsteroids().asDomainModel()
                }
                ObserverType.SAVED -> {
                    database.asteroidDao.getSavedAsteroids().asDomainModel()
                }
                ObserverType.WEEKS -> {
                    // check whether the database have already contained a week from today
                    val lastDay = database.asteroidDao.getLatestAsteroid()?.closeApproachDate
                    val sevenDay = getNextSevenDaysFormattedDates()
                    if (lastDay != null) {
                        // if lasted day already in range of target week, get the remains
                        if (lastDay.isAfter(sevenDay.first()) && sevenDay.last()
                                .isAfter(lastDay)
                        ) updateAsteroidDB(
                            lastDay,
                            sevenDay.last()
                        )
                        // if the latest day older than first day of week, get full week
                        else if (sevenDay.first().isAfter(lastDay)) {
                            updateAsteroidDB(sevenDay.first(), sevenDay.last())
                        }
                    } else {
                        // no items in database -> get full week
                        updateAsteroidDB(sevenDay.first(), sevenDay.last())
                    }
                    // query database
                    database.asteroidDao.getWeekAsteroids().asDomainModel()
                }
            }
            println("Query database -> END")
        }
        /**
         * get the today data if database have no items
         */
        if (listResult.isEmpty()) {
            val today = getTodayString()
            updateAsteroidDB(
                today, today
            )
        }
        /**
         * only update livedata on Main Thread
         */
        withContext(Dispatchers.Main) {
            _asteroidList.value = listResult
        }
    }

    /**
     * insert data to Database
     */
    suspend fun updateAsteroidDB(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            try {
                println("get data from retrofit and insert to DB -> START")
                val response = AsteroidApi.retrofitService.getProperties(startDate, endDate)
                database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(response)).asDatabaseModel())
                println("get data from retrofit and insert to DB -> END")
            } catch (e: Exception) {
                println("get data from retrofit and insert to DB -> ERROR")
                println("Failure : ${e.localizedMessage}")
            }
        }
    }

}