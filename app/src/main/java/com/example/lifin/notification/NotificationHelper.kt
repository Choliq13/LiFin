package com.example.lifin.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lifin.R
import java.util.Calendar

// Import the receiver explicitly
import com.example.lifin.notification.NotificationReceiver

object NotificationHelper {
    const val CHANNEL_ID = "lifin_daily_channel"
    const val CHANNEL_NAME = "Daily Reminders"
    const val NOTIFICATION_ID = 1001

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Daily health and weight logging reminders"

            // Enable lights
            channel.enableLights(true)
            channel.lightColor = Color.GREEN

            // Enable vibration
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(0, 500, 200, 500)

            // Set sound - menggunakan nada notifikasi default sistem
            val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()
            channel.setSound(soundUri, audioAttributes)

            nm.createNotificationChannel(channel)
        }
    }

    fun scheduleDaily(context: Context, hour: Int = 8, minute: Int = 0) {
        ensureChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        // Gunakan setExactAndAllowWhileIdle untuk Android 6+ agar lebih reliable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pi
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pi
            )
        }
    }

    fun cancelDaily(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi)
    }

    private val healthMessages = listOf(
        "🌟 Terus hidup sehat! Jangan lupa olahraga hari ini",
        "💪 Sudah waktunya bergerak! Tubuh sehat dimulai dari sekarang",
        "🏃 Jangan lupa olahraga! Hidup sehat adalah investasi terbaik",
        "🥗 Makan sehat, olahraga rutin, hidup jadi lebih semangat!",
        "✨ Tubuh sehat, pikiran cerdas! Ayo catat kesehatan Anda hari ini",
        "🎯 Ingat target kesehatan Anda! Catat berat badan hari ini",
        "💚 Jaga kesehatan, jaga masa depan! Yuk olahraga 30 menit",
        "🌈 Sehat itu nikmat! Jangan lupa bergerak dan berolahraga",
        "⚡ Energi positif dimulai dari tubuh sehat! Ayo olahraga!",
        "🌸 Hidup sehat itu pilihan! Mulai dari olahraga hari ini"
    )

    fun buildNotification(context: Context): android.app.Notification {
        // Pilih pesan random untuk variasi
        val randomMessage = healthMessages.random()

        // Nada notifikasi default sistem
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        // Intent untuk membuka app ke halaman PIN
        val intent = Intent(context, com.example.lifin.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_pin_entry", true) // Flag untuk membuka PIN entry
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🏥 LiFin - Pengingat Kesehatan")
            .setContentText(randomMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(randomMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setSound(soundUri)
            .setContentIntent(pendingIntent) // Tambahkan intent saat notifikasi diklik
            .build()
    }
}
