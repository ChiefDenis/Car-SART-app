package com.chiefdenis.carsart.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chiefdenis.carsart.MainActivity
import com.chiefdenis.carsart.R
import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.VehicleRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class MaintenanceWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val activeTasks = maintenanceRepository.getActiveTasks().first()
            if (activeTasks.isEmpty()) return Result.success()

            val allVehicles = vehicleRepository.getAllVehicles().first().associateBy { it.id }

            activeTasks.forEach { task ->
                val vehicle = allVehicles[task.vehicleId]
                if (vehicle != null) {
                    checkTaskAndNotify(task, vehicle, 7) // Using a fixed value for now.
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun checkTaskAndNotify(task: MaintenanceTask, vehicle: Vehicle, advanceWarningDays: Int): Boolean {
        val lastCheckedDate = task.lastCheckedDate ?: vehicle.purchaseDate ?: System.currentTimeMillis()
        val lastCheckedMileage = task.lastCheckedMileage ?: 0

        val nextDueDateCalendar = Calendar.getInstance().apply {
            timeInMillis = lastCheckedDate
            add(Calendar.MONTH, task.intervalMonths)
        }
        val nextDueDateMillis = nextDueDateCalendar.timeInMillis
        val daysUntilDue = TimeUnit.MILLISECONDS.toDays(nextDueDateMillis - System.currentTimeMillis())

        val nextDueMileage = task.intervalMileageKm?.let { lastCheckedMileage + it }
        val mileageUntilDue = nextDueMileage?.minus(vehicle.currentMileage)

        val isDueByDate = daysUntilDue in 0..advanceWarningDays
        val isDueByMileage = mileageUntilDue != null && mileageUntilDue in 0..500

        if (isDueByDate || isDueByMileage) {
            val reason = when {
                isDueByDate && isDueByMileage -> "due in $daysUntilDue days or $mileageUntilDue km"
                isDueByDate -> "due in $daysUntilDue days"
                isDueByMileage -> "due in $mileageUntilDue km"
                else -> "due soon"
            }
            showNotification(vehicle, task, reason)
            return true
        }
        return false
    }

    private fun showNotification(vehicle: Vehicle, task: MaintenanceTask, reason: String): Boolean {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "maintenance_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Maintenance Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Placeholder intents. These will be replaced with real implementations.
        val placeholderIntent = PendingIntent.getActivity(appContext, 0, Intent(appContext, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon) // Placeholder icon
            .setContentTitle("${vehicle.nickname}: ${task.taskName}")
            .setContentText("This task is $reason.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_log_check, "Log Check", placeholderIntent) 
            .addAction(R.drawable.ic_snooze, "Snooze 3 days", placeholderIntent)
            .addAction(R.drawable.ic_done, "Done", placeholderIntent)
            .build()

        notificationManager.notify(task.id.hashCode(), notification)
        return true
    }
}
