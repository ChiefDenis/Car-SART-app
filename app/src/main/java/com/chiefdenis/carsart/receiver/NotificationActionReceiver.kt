package com.chiefdenis.carsart.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chiefdenis.carsart.data.repository.MaintenanceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var maintenanceRepository: MaintenanceRepository

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)?.let { UUID.fromString(it) }

        when (intent.action) {
            ACTION_SNOOZE -> {
                // Implement snooze logic here, e.g., reschedule the worker
            }
            ACTION_DONE -> {
                if (taskId != null) {
                    val mileage = intent.getIntExtra(EXTRA_VEHICLE_MILEAGE, 0)
                    GlobalScope.launch {
                        maintenanceRepository.markTaskAsDone(taskId, mileage)
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_SNOOZE = "com.chiefdenis.carsart.ACTION_SNOOZE"
        const val ACTION_DONE = "com.chiefdenis.carsart.ACTION_DONE"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_VEHICLE_MILEAGE = "extra_vehicle_mileage"
    }
}
