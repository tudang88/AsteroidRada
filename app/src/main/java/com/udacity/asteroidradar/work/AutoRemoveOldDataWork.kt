package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.utils.getTodayString

class AutoRemoveOldDataWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private val _TAG = AutoRemoveOldDataWork::class.java.simpleName

    /**
     * this background task will run
     * once a day for remove the data older than today
     */
    override suspend fun doWork(): Result {
        val database = AsteroidDatabase.getDatabase(applicationContext)
        return try {
            // sync today data
            val today = getTodayString()
            Log.i(_TAG, "Background AutoRemoveOldDataWork -> START")
            database.asteroidDao.deleteOldAsteroids()
            Log.i(_TAG, "Background AutoRemoveOldDataWork -> END")
            Result.success()
        } catch (e: Exception) {
            Log.i(_TAG, "Background AutoRemoveOldDataWork Retry due to Error: ${e.localizedMessage}")
            Result.retry()
        }
    }
}