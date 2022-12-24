package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.utils.getTodayString
import timber.log.Timber

class AutoRemoveOldDataWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    /**
     * this background task will run
     * once a day for remove the data older than today
     */
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        return try {
            // sync today data
            val today = getTodayString()
            Timber.d("Background AutoRemoveOldDataWork -> START")
            database.asteroidDao.deleteOldAsteroids()
            Timber.d("Background AutoRemoveOldDataWork -> END")
            Result.success()
        } catch (e: Exception) {
            Timber.d("Background AutoRemoveOldDataWork Retry due to Error: ${e.localizedMessage}")
            Result.retry()
        }
    }
}