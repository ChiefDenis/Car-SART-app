package com.chiefdenis.carsart.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chiefdenis.carsart.R
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.VehicleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MaintenanceWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val activeTasks = maintenanceRepository.getActiveTasks().first()

        activeTasks.forEach { task ->
            val vehicle = vehicleRepository.getVehicleById(task.vehicleId)
            if (vehicle != null) {
                val nextDueDate = task.nextDueDate
                val nextDueMileage = task.nextDueMileage

                val isDue = (nextDueDate != null && nextDueDate <= System.currentTimeMillis() + SEVEN_DAYS_IN_MILLIS) ||
                            (nextDueMileage != null && nextDueMileage <= vehicle.currentMileage + 500)

                if (isDue) {
                    showNotification(task.taskName, "Your ${vehicle.nickname} is due for maintenance.")
                }
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, 
                "Maintenance Reminders", 
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "maintenance_reminders"
        const val SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000
    }
}
