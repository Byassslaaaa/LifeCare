package com.example.lifecare.reminder

/**
 * Types of health reminders
 */
enum class ReminderType(val displayName: String, val defaultHour: Int, val defaultMinute: Int) {
    BLOOD_PRESSURE_MORNING("Tekanan Darah (Pagi)", 8, 0),
    BLOOD_PRESSURE_EVENING("Tekanan Darah (Malam)", 20, 0),
    BLOOD_SUGAR("Gula Darah", 7, 0),
    WEIGHT("Berat Badan", 7, 30),
    ACTIVITY("Aktivitas Fisik", 17, 0),
    FOOD("Asupan Makanan", 19, 0);

    companion object {
        fun fromName(name: String): ReminderType? {
            return values().find { it.name == name }
        }
    }
}

/**
 * Reminder configuration data class
 */
data class ReminderConfig(
    val type: ReminderType,
    val enabled: Boolean = false,
    val hour: Int = type.defaultHour,
    val minute: Int = type.defaultMinute
)
