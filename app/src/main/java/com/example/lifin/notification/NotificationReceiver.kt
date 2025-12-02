package com.example.lifin.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule notifikasi setelah device restart
                val prefs = com.example.lifin.data.local.EncryptedPreferences(context)
                if (prefs.isNotificationEnabled()) {
                    val hour = prefs.getNotificationHour()
                    val minute = prefs.getNotificationMinute()
                    NotificationHelper.scheduleDaily(context, hour, minute)
                }
            }
            else -> {
                // Trigger notifikasi harian
                val permissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!permissionGranted) {
                    // Silently skip if no permission; UI should request it elsewhere when enabling notifications
                    return
                }
                val notification = NotificationHelper.buildNotification(context)
                with(NotificationManagerCompat.from(context)) {
                    notify(NotificationHelper.NOTIFICATION_ID, notification)
                }
                
                // Reschedule untuk hari berikutnya
                val prefs = com.example.lifin.data.local.EncryptedPreferences(context)
                if (prefs.isNotificationEnabled()) {
                    val hour = prefs.getNotificationHour()
                    val minute = prefs.getNotificationMinute()
                    NotificationHelper.scheduleDaily(context, hour, minute)
                }
            }
        }
    }
}
