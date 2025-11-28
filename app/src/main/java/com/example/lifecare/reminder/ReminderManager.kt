package com.example.lifecare.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

/**
 * Manages health reminders using AlarmManager
 */
class ReminderManager(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "reminder_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "ReminderManager"
        private const val PREFS_KEY = "reminder_configs"
        private const val REQUEST_CODE_BASE = 10000
    }

    /**
     * Get all reminder configurations
     */
    fun getAllReminders(): List<ReminderConfig> {
        val json = sharedPreferences.getString(PREFS_KEY, null) ?: return getDefaultReminders()
        return try {
            val type = object : TypeToken<List<ReminderConfig>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading reminders", e)
            getDefaultReminders()
        }
    }

    /**
     * Save reminder configuration
     */
    fun saveReminder(config: ReminderConfig) {
        val reminders = getAllReminders().toMutableList()
        val index = reminders.indexOfFirst { it.type == config.type }

        if (index >= 0) {
            reminders[index] = config
        } else {
            reminders.add(config)
        }

        val json = gson.toJson(reminders)
        sharedPreferences.edit().putString(PREFS_KEY, json).apply()

        // Schedule or cancel alarm based on enabled state
        if (config.enabled) {
            scheduleReminder(config)
        } else {
            cancelReminder(config.type)
        }
    }

    /**
     * Schedule a reminder alarm
     */
    private fun scheduleReminder(config: ReminderConfig) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.hour)
            set(Calendar.MINUTE, config.minute)
            set(Calendar.SECOND, 0)

            // If time has passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_type", config.type.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(config.type),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Set repeating alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            Log.d(TAG, "Scheduled reminder: ${config.type.displayName} at ${config.hour}:${config.minute}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for setting alarm", e)
        }
    }

    /**
     * Cancel a reminder alarm
     */
    fun cancelReminder(type: ReminderType) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(type),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled reminder: ${type.displayName}")
    }

    /**
     * Cancel all reminders
     */
    fun cancelAllReminders() {
        ReminderType.values().forEach { type ->
            cancelReminder(type)
        }
        sharedPreferences.edit().remove(PREFS_KEY).apply()
    }

    /**
     * Get unique request code for each reminder type
     */
    private fun getRequestCode(type: ReminderType): Int {
        return REQUEST_CODE_BASE + type.ordinal
    }

    /**
     * Get default reminder configurations
     */
    private fun getDefaultReminders(): List<ReminderConfig> {
        return ReminderType.values().map { type ->
            ReminderConfig(
                type = type,
                enabled = false,
                hour = type.defaultHour,
                minute = type.defaultMinute
            )
        }
    }

    /**
     * Check if exact alarm permission is granted (Android 12+)
     */
    fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}
