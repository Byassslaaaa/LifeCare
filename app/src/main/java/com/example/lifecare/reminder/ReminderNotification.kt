package com.example.lifecare.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lifecare.MainActivity
import com.example.lifecare.R

/**
 * Handles creation and display of reminder notifications
 */
class ReminderNotification(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "health_reminders"
        private const val CHANNEL_NAME = "Pengingat Kesehatan"
        private const val CHANNEL_DESCRIPTION = "Notifikasi pengingat untuk mencatat data kesehatan"
        private const val NOTIFICATION_ID_BASE = 20000
    }

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show reminder notification
     */
    fun showReminderNotification(type: ReminderType) {
        val (title, message) = getNotificationContent(type)

        // Create intent to open app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_screen", getTargetScreen(type))
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_BASE + type.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use app icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Show notification
        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_BASE + type.ordinal,
                notification
            )
        } catch (e: SecurityException) {
            // Permission not granted - ignore
        }
    }

    /**
     * Get notification content based on reminder type
     */
    private fun getNotificationContent(type: ReminderType): Pair<String, String> {
        return when (type) {
            ReminderType.BLOOD_PRESSURE_MORNING -> {
                "Pengingat Tekanan Darah" to "Waktunya mengukur tekanan darah pagi Anda. Jangan lupa catat hasilnya!"
            }
            ReminderType.BLOOD_PRESSURE_EVENING -> {
                "Pengingat Tekanan Darah" to "Waktunya mengukur tekanan darah malam Anda. Catat untuk tracking yang lebih baik!"
            }
            ReminderType.BLOOD_SUGAR -> {
                "Pengingat Gula Darah" to "Saatnya cek gula darah Anda. Monitoring rutin sangat penting untuk kesehatan!"
            }
            ReminderType.WEIGHT -> {
                "Pengingat Berat Badan" to "Waktunya timbang berat badan mingguan Anda. Tracking progress itu penting!"
            }
            ReminderType.ACTIVITY -> {
                "Pengingat Aktivitas Fisik" to "Sudahkah Anda berolahraga hari ini? Catat aktivitas fisik Anda!"
            }
            ReminderType.FOOD -> {
                "Pengingat Asupan Makanan" to "Jangan lupa catat makanan yang Anda konsumsi hari ini untuk tracking kalori!"
            }
        }
    }

    /**
     * Get target screen to open when notification is tapped
     */
    private fun getTargetScreen(type: ReminderType): String {
        return when (type) {
            ReminderType.BLOOD_PRESSURE_MORNING,
            ReminderType.BLOOD_PRESSURE_EVENING -> "health_records"
            ReminderType.BLOOD_SUGAR -> "health_records"
            ReminderType.WEIGHT -> "health_records"
            ReminderType.ACTIVITY -> "physical_activity"
            ReminderType.FOOD -> "food_tracker"
        }
    }
}
