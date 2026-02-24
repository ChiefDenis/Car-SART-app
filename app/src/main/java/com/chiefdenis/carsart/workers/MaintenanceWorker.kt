package com.chiefdenis.carsart.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chiefdenis.carsart.MainActivity
import com.chiefdenis.carsart.R
import com.chiefdenis.carsart.data.database.MaintenanceTask
import com.chiefdenis.carsart.data.database.Vehicle
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import com.chiefdenis.carsart.data.repository.VehicleRepository
import com.chiefdenis.carsart.receiver.NotificationActionReceiver
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
            val allVehicles = vehicleRepository.getAllVehicles().first().associateBy { it.id }

            activeTasks.forEach { task ->
                val vehicle = allVehicles[task.vehicleId]
                if (vehicle != null) {
                    checkTaskAndNotify(task, vehicle)
                }
            }
            Result.success()
        } catch (e: Exception) {
            // For debugging purposes, in production you might want to handle this differently
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun checkTaskAndNotify(task: MaintenanceTask, vehicle: Vehicle) {
        // Default to now for date and 0 for mileage if never checked
        val lastCheckedDate = task.lastCheckedDate ?: System.currentTimeMillis()
        val lastCheckedMileage = task.lastCheckedMileage ?: 0

        // --- Date-based check ---
        val nextDueDateCalendar = Calendar.getInstance().apply {
            timeInMillis = lastCheckedDate
            add(Calendar.MONTH, task.intervalMonths)
        }
        val nextDueDateMillis = nextDueDateCalendar.timeInMillis
        val daysUntilDue = TimeUnit.MILLISECONDS.toDays(nextDueDateMillis - System.currentTimeMillis())

        // --- Mileage-based check ---
        val nextDueMileage = if (task.intervalMileageKm != null) {
            lastCheckedMileage + task.intervalMileageKm
        } else null
        val mileageUntilDue = nextDueMileage?.let { it - vehicle.currentMileage }

        val advanceWarningDays = 7 // Default, will be from settings later
        val advanceWarningKm = 500   // Default

        val isDueByDate = daysUntilDue in 0..advanceWarningDays
        val isDueByMileage = mileageUntilDue != null && mileageUntilDue in 0..advanceWarningKm

        if (isDueByDate || isDueByMileage) {
            val reason = when {
                isDueByDate && isDueByMileage -> "due in $daysUntilDue days or $mileageUntilDue km"
                isDueByDate -> "due in $daysUntilDue days"
                isDueByMileage -> "due in $mileageUntilDue km"
                else -> "due soon"
            }
            showNotification(
                vehicle = vehicle,
                task = task,
                reason = reason
            )
        }
    }

    private fun showNotification(vehicle: Vehicle, task: MaintenanceTask, reason: String) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "maintenance_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Maintenance Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 1. Log Check Action -> Deep Link to MaintenanceCheckScreen
        val logCheckIntent = Intent(
            Intent.ACTION_VIEW,
            "carsart://maintenance/check/${task.id}".toUri(),
            appContext,
            MainActivity::class.java
        )
        val logCheckPendingIntent: PendingIntent = TaskStackBuilder.create(appContext).run {
            addNextIntentWithParentStack(logCheckIntent)
            getPendingIntent(task.id.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        // 2. Snooze Action -> Broadcast
        val snoozeIntent = Intent(appContext, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_SNOOZE
            putExtra(NotificationActionReceiver.EXTRA_TASK_ID, task.id.toString())
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            appContext, 
            task.id.hashCode() + 1, 
            snoozeIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Done Action -> Broadcast
        val doneIntent = Intent(appContext, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_DONE
            putExtra(NotificationActionReceiver.EXTRA_TASK_ID, task.id.toString())
            putExtra(NotificationActionReceiver.EXTRA_VEHICLE_MILEAGE, vehicle.currentMileage)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            appContext, 
            task.id.hashCode() + 2, 
            doneIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notification_icon) // Replace with a real icon
            .setContentTitle("${vehicle.nickname}: ${task.taskName}")
            .setContentText("This task is $reason.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_log_check, "Log Check", logCheckPendingIntent) // Replace with real icons
            .addAction(R.drawable.ic_snooze, "Snooze 3 days", snoozePendingIntent)
            .addAction(R.drawable.ic_done, "Done", donePendingIntent)
            .build()

        notificationManager.notify(task.id.hashCode(), notification)
    }
}
