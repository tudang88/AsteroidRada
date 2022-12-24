package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.utils.getNextSevenDaysFormattedDates
import retrofit2.HttpException
import timber.log.Timber

class AutoSyncWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "AsteroidRadaAutoSyncWorker"
    }

    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            // sync today data
            val weekDays = getNextSevenDaysFormattedDates()
            Timber.d("Background AutoSyncWork -> START")
            repository.updateAsteroidDB(weekDays.first(), weekDays.last())
            Timber.d("Background AutoSyncWork -> END")
            Result.success()
        } catch (e: HttpException) {
            Timber.d("Background AutoSyncWork Retry due to Error: ${e.message()}")
            Result.retry()
        }
    }
}