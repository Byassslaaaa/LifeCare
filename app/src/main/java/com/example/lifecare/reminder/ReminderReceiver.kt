package com.example.lifecare.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BroadcastReceiver that handles reminder alarms
 */
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderTypeName = intent.getStringExtra("reminder_type") ?: return
        val reminderType = ReminderType.fromName(reminderTypeName) ?: return

        Log.d(TAG, "Reminder received: ${reminderType.displayName}")

        // Show notification
        val notificationHelper = ReminderNotification(context)
        notificationHelper.showReminderNotification(reminderType)
    }
}
