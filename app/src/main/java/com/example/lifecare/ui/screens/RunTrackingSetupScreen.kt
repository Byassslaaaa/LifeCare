package com.example.lifecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import com.example.lifecare.viewmodel.RunTrackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunTrackingSetupScreen(
    viewModel: RunTrackingViewModel,
    onStartRun: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedActivityType by viewModel.selectedActivityType.collectAsState()
    val targetDistance by viewModel.targetDistance.collectAsState()
    val targetDuration by viewModel.targetDuration.collectAsState()

    var showTargetDistanceDialog by remember { mutableStateOf(false) }
    var showTargetDurationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Aktivitas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.Activity
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(HealthSpacing.medium)
        ) {
            // Activity Type Selection
            Text(
                text = "Pilih Jenis Aktivitas",
                style = HealthTypography.titleMedium,
                modifier = Modifier.padding(bottom = HealthSpacing.small)
            )

            val activityTypes = listOf(
                ActivityTypeOption("Lari", Icons.Default.DirectionsRun),
                ActivityTypeOption("Jalan Kaki", Icons.Default.DirectionsWalk),
                ActivityTypeOption("Bersepeda", Icons.Default.DirectionsBike)
            )

            activityTypes.forEach { option ->
                ActivityTypeCard(
                    label = option.label,
                    icon = option.icon,
                    selected = selectedActivityType == option.label,
                    onClick = { viewModel.setActivityType(option.label) }
                )
                Spacer(modifier = Modifier.height(HealthSpacing.small))
            }

            Spacer(modifier = Modifier.height(HealthSpacing.large))

            // Target Settings (Optional)
            Text(
                text = "Target (Opsional)",
                style = HealthTypography.titleMedium,
                modifier = Modifier.padding(bottom = HealthSpacing.small)
            )

            // Target Distance
            TargetCard(
                label = "Target Jarak",
                value = targetDistance?.let { "%.2f km".format(it) } ?: "Tidak ada target",
                icon = Icons.Outlined.Straighten,
                onClick = { showTargetDistanceDialog = true },
                onClear = if (targetDistance != null) {
                    { viewModel.setTargetDistance(null) }
                } else null
            )

            Spacer(modifier = Modifier.height(HealthSpacing.small))

            // Target Duration
            TargetCard(
                label = "Target Durasi",
                value = targetDuration?.let { formatDurationTarget(it) } ?: "Tidak ada target",
                icon = Icons.Default.Timer,
                onClick = { showTargetDurationDialog = true },
                onClear = if (targetDuration != null) {
                    { viewModel.setTargetDuration(null) }
                } else null
            )

            Spacer(modifier = Modifier.height(HealthSpacing.extraLarge))

            // Start Button
            Button(
                onClick = onStartRun,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.Activity
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(HealthSpacing.small))
                Text(
                    text = "Mulai ${selectedActivityType}",
                    style = HealthTypography.titleMedium
                )
            }
        }
    }

    // Target Distance Dialog
    if (showTargetDistanceDialog) {
        TargetDistanceDialog(
            currentValue = targetDistance,
            onDismiss = { showTargetDistanceDialog = false },
            onConfirm = { distance ->
                viewModel.setTargetDistance(distance)
                showTargetDistanceDialog = false
            }
        )
    }

    // Target Duration Dialog
    if (showTargetDurationDialog) {
        TargetDurationDialog(
            currentValue = targetDuration,
            onDismiss = { showTargetDurationDialog = false },
            onConfirm = { duration ->
                viewModel.setTargetDuration(duration)
                showTargetDurationDialog = false
            }
        )
    }
}

@Composable
private fun ActivityTypeCard(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (selected) HealthColors.Activity.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (selected) 4.dp else 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(HealthSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = if (selected) HealthColors.Activity else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(HealthSpacing.medium))
            Text(
                text = label,
                style = HealthTypography.titleMedium,
                color = if (selected) HealthColors.Activity else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = HealthColors.Activity
                )
            }
        }
    }
}

@Composable
private fun TargetCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    onClear: (() -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(HealthSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                tint = HealthColors.Activity
            )
            Spacer(modifier = Modifier.width(HealthSpacing.medium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = HealthTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = HealthTypography.bodyLarge
                )
            }
            if (onClear != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus target",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TargetDistanceDialog(
    currentValue: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var distanceText by remember { mutableStateOf(currentValue?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Target Jarak") },
        text = {
            Column {
                Text("Masukkan target jarak (km):")
                Spacer(modifier = Modifier.height(HealthSpacing.small))
                OutlinedTextField(
                    value = distanceText,
                    onValueChange = { distanceText = it },
                    label = { Text("Jarak (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    distanceText.toDoubleOrNull()?.let { distance ->
                        if (distance > 0) {
                            onConfirm(distance)
                        }
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun TargetDurationDialog(
    currentValue: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var hoursText by remember { mutableStateOf((currentValue?.div(3600) ?: 0).toString()) }
    var minutesText by remember { mutableStateOf(((currentValue?.rem(3600))?.div(60) ?: 0).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Target Durasi") },
        text = {
            Column {
                Text("Masukkan target durasi:")
                Spacer(modifier = Modifier.height(HealthSpacing.small))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small)
                ) {
                    OutlinedTextField(
                        value = hoursText,
                        onValueChange = { hoursText = it },
                        label = { Text("Jam") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it },
                        label = { Text("Menit") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hours = hoursText.toLongOrNull() ?: 0
                    val minutes = minutesText.toLongOrNull() ?: 0
                    val totalSeconds = (hours * 3600) + (minutes * 60)
                    if (totalSeconds > 0) {
                        onConfirm(totalSeconds)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private data class ActivityTypeOption(
    val label: String,
    val icon: ImageVector
)

private fun formatDurationTarget(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}j ${minutes}m"
        else -> "${minutes}m"
    }
}
