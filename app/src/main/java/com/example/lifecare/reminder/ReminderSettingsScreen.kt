package com.example.lifecare.reminder

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Settings screen for health reminders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val reminderManager = remember { ReminderManager(context) }
    var reminders by remember { mutableStateOf(reminderManager.getAllReminders()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Pengingat") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header info card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Pengingat Kesehatan",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Aktifkan pengingat untuk membantu Anda rutin mencatat data kesehatan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Reminder items
            items(reminders) { reminder ->
                ReminderItem(
                    config = reminder,
                    onToggle = { enabled ->
                        val updated = reminder.copy(enabled = enabled)
                        reminderManager.saveReminder(updated)
                        reminders = reminderManager.getAllReminders()
                    },
                    onTimeChange = { hour, minute ->
                        val updated = reminder.copy(hour = hour, minute = minute)
                        reminderManager.saveReminder(updated)
                        reminders = reminderManager.getAllReminders()
                    }
                )
            }

            // Help text
            item {
                Text(
                    "💡 Tip: Pengingat akan muncul setiap hari pada waktu yang Anda tentukan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ReminderItem(
    config: ReminderConfig,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        config.type.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        getDescription(config.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = config.enabled,
                    onCheckedChange = onToggle
                )
            }

            if (config.enabled) {
                Spacer(modifier = Modifier.height(12.dp))

                // Time selector
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                onTimeChange(hour, minute)
                            },
                            config.hour,
                            config.minute,
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatTime(config.hour, config.minute))
                }
            }
        }
    }
}

/**
 * Get description for reminder type
 */
private fun getDescription(type: ReminderType): String {
    return when (type) {
        ReminderType.BLOOD_PRESSURE_MORNING -> "Pengingat untuk cek tekanan darah pagi hari"
        ReminderType.BLOOD_PRESSURE_EVENING -> "Pengingat untuk cek tekanan darah malam hari"
        ReminderType.BLOOD_SUGAR -> "Pengingat untuk cek gula darah"
        ReminderType.WEIGHT -> "Pengingat mingguan untuk timbang berat badan"
        ReminderType.ACTIVITY -> "Pengingat untuk catat aktivitas fisik harian"
        ReminderType.FOOD -> "Pengingat untuk catat asupan makanan"
    }
}

/**
 * Format time to HH:mm
 */
private fun formatTime(hour: Int, minute: Int): String {
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}
