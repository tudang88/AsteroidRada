package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.utils.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.utils.getTodayString
import retrofit2.HttpException

class AutoSyncWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "AsteroidRadaAutoSyncWorker"
    }

    private val _TAG = AutoSyncWork::class.java.simpleName
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            // sync today data
            val weekDays = getNextSevenDaysFormattedDates()
            Log.i(_TAG, "Background AutoSyncWork -> START")
            repository.updateAsteroidDB(weekDays.first(), weekDays.last())
            Log.i(_TAG, "Background AutoSyncWork -> END")
            Result.success()
        } catch (e: HttpException) {
            Log.i(_TAG, "Background AutoSyncWork Retry due to Error: ${e.message()}")
            Result.retry()
        }
    }
}