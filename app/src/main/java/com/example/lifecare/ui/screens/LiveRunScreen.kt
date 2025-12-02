package com.example.lifecare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import com.example.lifecare.viewmodel.RunTrackingViewModel

@Composable
fun LiveRunScreen(
    viewModel: RunTrackingViewModel,
    onFinishRun: () -> Unit,
    onDiscardRun: () -> Unit
) {
    val context = LocalContext.current
    val runState by viewModel.liveRunState.collectAsState()
    val targetDistance by viewModel.targetDistance.collectAsState()
    val targetDuration by viewModel.targetDuration.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with discard button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(HealthSpacing.medium)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDiscardDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Buang",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (runState.isPaused) "DIJEDA" else "BERJALAN",
                    style = HealthTypography.labelLarge,
                    color = if (runState.isPaused) Color.Yellow else HealthColors.Activity,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Main Stats Area
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(HealthSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Distance (Main stat)
            Text(
                text = "%.2f".format(runState.distance),
                style = HealthTypography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "kilometer",
                style = HealthTypography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            // Target Progress (if set)
            targetDistance?.let { target ->
                Spacer(modifier = Modifier.height(HealthSpacing.small))
                val progress = (runState.distance / target).coerceIn(0.0, 1.0)
                LinearProgressIndicator(
                    progress = { progress.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = HealthColors.Activity,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "Target: %.2f km (%.0f%%)".format(target, progress * 100),
                    style = HealthTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = HealthSpacing.extraSmall)
                )
            }

            Spacer(modifier = Modifier.height(HealthSpacing.extraLarge))

            // Secondary Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Pace",
                    value = viewModel.formatPace(runState.averagePace),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Waktu",
                    value = viewModel.formatDuration(runState.duration),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Kalori",
                    value = "${runState.calories}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(HealthSpacing.medium))

            // Additional Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Kecepatan",
                    value = viewModel.formatSpeed(runState.averageSpeed),
                    modifier = Modifier.weight(1f)
                )
                if (runState.elevationGain > 0) {
                    StatItem(
                        label = "Elevasi",
                        value = "${runState.elevationGain.toInt()}m",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Control Buttons
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(HealthSpacing.large)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause/Resume Button
                FloatingActionButton(
                    onClick = {
                        if (runState.isPaused) {
                            viewModel.resumeTracking()
                        } else {
                            viewModel.pauseTracking()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (runState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (runState.isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Finish Button
                Button(
                    onClick = onFinishRun,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HealthColors.Activity
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(64.dp),
                    enabled = runState.distance > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Finish",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    // Discard confirmation dialog
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Buang Aktivitas?") },
            text = { Text("Data tracking akan hilang dan tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.stopTracking(context, saveRun = false)
                        showDiscardDialog = false
                        onDiscardRun()
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
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = HealthTypography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = HealthTypography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
