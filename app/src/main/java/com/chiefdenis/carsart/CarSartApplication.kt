package com.chiefdenis.carsart

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.chiefdenis.carsart.workers.MaintenanceWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class CarSartApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val repeatingRequest = PeriodicWorkRequestBuilder<MaintenanceWorker>(
            1, 
            TimeUnit.DAYS
        ).build()
        
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "maintenance_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
