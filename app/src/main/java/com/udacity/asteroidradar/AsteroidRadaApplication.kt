package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.work.AutoRemoveOldDataWork
import com.udacity.asteroidradar.work.AutoSyncWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidRadaApplication : Application() {
    private val applicationScrope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initBackgroundWork()
    }

    /**
     * run long time setup on background
     */
    private fun initBackgroundWork() = applicationScrope.launch {
        setupAutoSyncWork()
        setupAutoClearOldData()
    }

    /**
     * setup background task for auto sync
     * 1 per day
     */
    private fun setupAutoSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val periodicRequest =
            PeriodicWorkRequestBuilder<AutoSyncWork>(1, TimeUnit.DAYS).setConstraints(constraints)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            AutoSyncWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, periodicRequest
        )
    }

    /**
     * This worker will clear old data once a day
     */
    private fun setupAutoClearOldData() {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()
        val periodicRequest =
            PeriodicWorkRequestBuilder<AutoRemoveOldDataWork>(1, TimeUnit.DAYS).setConstraints(
                constraints
            )
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            AutoSyncWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, periodicRequest
        )
    }
}