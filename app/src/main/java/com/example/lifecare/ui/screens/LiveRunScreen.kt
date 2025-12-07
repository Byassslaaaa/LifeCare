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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import com.example.lifecare.viewmodel.RunTrackingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    // Map state
    val hasGpsLocation = runState.routePoints.isNotEmpty()
    val currentLocation = remember(runState.routePoints) {
        runState.routePoints.lastOrNull()?.let { LatLng(it.latitude, it.longitude) }
            ?: LatLng(-6.200000, 106.816666) // Default Jakarta
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 17f)
    }

    // Update camera when location changes (only if we have GPS location)
    LaunchedEffect(currentLocation, hasGpsLocation) {
        if (hasGpsLocation) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f),
                durationMs = 1000
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasGpsLocation) {
            // Google Maps as background (only show when GPS ready)
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            ) {
                // Draw route polyline
                if (runState.routePoints.size > 1) {
                    Polyline(
                        points = runState.routePoints.map { LatLng(it.latitude, it.longitude) },
                        color = HealthColors.NeonGreen,
                        width = 12f
                    )
                }

                // Current location marker
                runState.routePoints.lastOrNull()?.let { location ->
                    Marker(
                        state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                        title = "Lokasi Saat Ini",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }
            }
        } else {
            // GPS Loading State - Clean placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Animated GPS icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(HealthColors.NeonGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Mencari GPS",
                            tint = HealthColors.NeonGreen,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CircularProgressIndicator(
                        color = HealthColors.NeonGreen,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Mencari Sinyal GPS...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Pastikan Anda berada di area terbuka",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "untuk akurasi GPS yang lebih baik",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Top Bar with close button and status
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HealthSpacing.medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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

                Surface(
                    color = if (!hasGpsLocation) Color(0xFFF59E0B) else if (runState.isPaused) Color(0xFFFBBF24) else HealthColors.NeonGreen,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (!hasGpsLocation) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "GPS",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (!hasGpsLocation) "MENCARI GPS" else if (runState.isPaused) "DIJEDA" else "BERJALAN",
                            style = HealthTypography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Placeholder for symmetry
                Box(modifier = Modifier.size(48.dp))
            }
        }

        // Floating Stats Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(HealthSpacing.medium)
        ) {
            // Main Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Distance - Main Stat
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "%.2f".format(runState.distance),
                            style = HealthTypography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = HealthColors.NeonGreen
                        )
                        Text(
                            text = "kilometer",
                            style = HealthTypography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        // Target Progress
                        targetDistance?.let { target ->
                            Spacer(modifier = Modifier.height(8.dp))
                            val progress = (runState.distance / target).coerceIn(0.0, 1.0)
                            LinearProgressIndicator(
                                progress = { progress.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = HealthColors.NeonGreen,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Text(
                                text = "Target: %.2f km".format(target),
                                style = HealthTypography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Secondary Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FloatingStatItem(
                            label = "Waktu",
                            value = viewModel.formatDuration(runState.duration),
                            icon = Icons.Default.Timer
                        )
                        FloatingStatItem(
                            label = "Pace",
                            value = viewModel.formatPace(runState.averagePace),
                            icon = Icons.Default.Speed
                        )
                        FloatingStatItem(
                            label = "Kalori",
                            value = "${runState.calories}",
                            icon = Icons.Default.LocalFireDepartment
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    containerColor = if (runState.isPaused) HealthColors.NeonGreen else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (runState.isPaused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (runState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (runState.isPaused) "Resume" else "Pause",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Finish Button
                FloatingActionButton(
                    onClick = onFinishRun,
                    containerColor = HealthColors.NeonGreen,
                    contentColor = Color.White,
                    modifier = Modifier.size(72.dp),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Finish",
                        modifier = Modifier.size(36.dp)
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
private fun FloatingStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = HealthColors.NeonGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = HealthTypography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = HealthTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

