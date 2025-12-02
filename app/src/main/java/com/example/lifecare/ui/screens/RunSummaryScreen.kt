package com.example.lifecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import com.example.lifecare.viewmodel.RunTrackingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunSummaryScreen(
    viewModel: RunTrackingViewModel,
    onSaveAndExit: () -> Unit,
    onDiscardAndExit: () -> Unit
) {
    val context = LocalContext.current
    val runState by viewModel.liveRunState.collectAsState()
    val selectedActivityType by viewModel.selectedActivityType.collectAsState()
    val targetDistance by viewModel.targetDistance.collectAsState()
    val targetDuration by viewModel.targetDuration.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    // Achievement check
    val targetDistanceAchieved = targetDistance?.let { runState.distance >= it } ?: false
    val targetDurationAchieved = targetDuration?.let { runState.duration >= it } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ringkasan ${selectedActivityType}") },
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
        ) {
            // Header Section with main stat
            Surface(
                color = HealthColors.Activity,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(HealthSpacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
                    Text(
                        text = "Selesai!",
                        style = HealthTypography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale("id", "ID"))
                            .format(Date()),
                        style = HealthTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(HealthSpacing.medium))

            // Achievement badges (if targets met)
            if (targetDistanceAchieved || targetDurationAchieved) {
                Column(
                    modifier = Modifier.padding(horizontal = HealthSpacing.medium)
                ) {
                    if (targetDistanceAchieved) {
                        AchievementBadge(
                            icon = Icons.Default.EmojiEvents,
                            title = "Target Jarak Tercapai!",
                            description = "Kamu mencapai target ${targetDistance} km"
                        )
                    }
                    if (targetDurationAchieved) {
                        Spacer(modifier = Modifier.height(HealthSpacing.small))
                        AchievementBadge(
                            icon = Icons.Default.Timer,
                            title = "Target Durasi Tercapai!",
                            description = "Kamu mencapai target waktu"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(HealthSpacing.medium))
            }

            // Stats Cards
            Column(
                modifier = Modifier.padding(horizontal = HealthSpacing.medium)
            ) {
                // Primary Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small)
                ) {
                    SummaryStatCard(
                        icon = Icons.Outlined.Straighten,
                        label = "Jarak",
                        value = viewModel.formatDistance(runState.distance),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.Timer,
                        label = "Durasi",
                        value = viewModel.formatDuration(runState.duration),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(HealthSpacing.small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.Speed,
                        label = "Pace Rata-rata",
                        value = viewModel.formatPace(runState.averagePace),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Kalori",
                        value = "${runState.calories} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(HealthSpacing.small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.FlashOn,
                        label = "Kecepatan",
                        value = viewModel.formatSpeed(runState.averageSpeed),
                        modifier = Modifier.weight(1f)
                    )
                    if (runState.elevationGain > 0) {
                        SummaryStatCard(
                            icon = Icons.Default.Terrain,
                            label = "Elevasi",
                            value = "${runState.elevationGain.toInt()} m",
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(HealthSpacing.medium))

                // GPS Route Info
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(HealthSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = null,
                            tint = HealthColors.Activity,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(HealthSpacing.small))
                        Column {
                            Text(
                                text = "Rute GPS Terekam",
                                style = HealthTypography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${runState.routePoints.size} titik koordinat",
                                style = HealthTypography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(HealthSpacing.medium))

                // Notes (Optional)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan (opsional)") },
                    placeholder = { Text("Tambahkan catatan tentang aktivitas ini...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(HealthSpacing.large))

            // Action Buttons
            Column(
                modifier = Modifier.padding(horizontal = HealthSpacing.medium)
            ) {
                Button(
                    onClick = {
                        viewModel.stopTracking(context, saveRun = true)
                        onSaveAndExit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HealthColors.Activity
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(HealthSpacing.small))
                    Text(
                        text = "Simpan Aktivitas",
                        style = HealthTypography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(HealthSpacing.small))

                OutlinedButton(
                    onClick = { showDiscardDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Buang")
                }
            }

            Spacer(modifier = Modifier.height(HealthSpacing.medium))
        }
    }

    // Discard confirmation dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text("Buang Aktivitas?") },
            text = { Text("Data tracking akan hilang dan tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.stopTracking(context, saveRun = false)
                        showDiscardDialog = false
                        onDiscardAndExit()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Buang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SummaryStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(HealthSpacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = HealthColors.Activity,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(HealthSpacing.extraSmall))
            Text(
                text = value,
                style = HealthTypography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = HealthTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementBadge(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        color = HealthColors.Activity.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(HealthSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = HealthColors.Activity,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(HealthSpacing.medium))
            Column {
                Text(
                    text = title,
                    style = HealthTypography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = HealthColors.Activity
                )
                Text(
                    text = description,
                    style = HealthTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
