package com.example.lifecare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.theme.HealthColors
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Physical Activity Home Screen - Strava-like Design
 * Features:
 * - Large map view at top showing current location
 * - Quick stats (steps, distance, duration, calories)
 * - Prominent Start Activity button
 * - Recent activity feed
 * - Clean, modern UI with neon green accent colors
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalActivityHomeScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onStartActivity: () -> Unit = {},
    onViewHistory: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Default location (Jakarta) - will be replaced with actual GPS location
    val defaultLocation = LatLng(-6.200000, 106.816666)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    // Calculate today's stats
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayActivities = healthDataManager.getPhysicalActivityList()
        .filter { it.timestamp >= todayStart }

    val totalSteps = todayActivities.sumOf { it.steps ?: 0 }
    val totalDistance = (totalSteps * 0.0008) // Estimasi: 1 langkah ≈ 0.8 meter = 0.0008 km
    val totalDuration = todayActivities.sumOf { it.duration }
    val totalCalories = todayActivities.sumOf { it.caloriesBurned ?: 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Aktivitas Fisik",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // MAP SECTION - Large map view at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = false
                    )
                ) {
                    // Add marker for current location
                    Marker(
                        state = MarkerState(position = defaultLocation),
                        title = "Lokasi Saat Ini"
                    )
                }

                // Overlay gradient at bottom for smooth transition
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // GPS button overlay
                FloatingActionButton(
                    onClick = { /* Center to current location */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(48.dp),
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "My Location",
                        tint = HealthColors.NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // QUICK STATS SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Section Title
                Text(
                    "Hari Ini",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Stats Grid - 2x2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Steps
                    StatCard(
                        icon = Icons.Default.DirectionsWalk,
                        value = formatNumber(totalSteps),
                        unit = "langkah",
                        iconColor = HealthColors.NeonGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Distance
                    StatCard(
                        icon = Icons.Default.Route,
                        value = String.format("%.2f", totalDistance),
                        unit = "km",
                        iconColor = HealthColors.NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Duration
                    StatCard(
                        icon = Icons.Default.Timer,
                        value = formatDuration(totalDuration),
                        unit = "menit",
                        iconColor = HealthColors.NeonGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Calories
                    StatCard(
                        icon = Icons.Default.LocalFireDepartment,
                        value = formatNumber(totalCalories),
                        unit = "kal",
                        iconColor = HealthColors.NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // START ACTIVITY BUTTON - Prominent CTA
            Button(
                onClick = onStartActivity,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(8.dp, RoundedCornerShape(32.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.NeonGreen
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Mulai Aktivitas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // RECENT ACTIVITY SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Aktivitas Terbaru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(onClick = onViewHistory) {
                        Text(
                            "Lihat Semua",
                            color = HealthColors.NeonGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recent activities list (last 3)
                val recentActivities = healthDataManager.getPhysicalActivityList()
                    .sortedByDescending { it.timestamp }
                    .take(3)

                if (recentActivities.isEmpty()) {
                    // Empty state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = HealthColors.NeonGreen.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Belum ada aktivitas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Mulai aktivitas pertamamu sekarang!",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                } else {
                    recentActivities.forEach { activity ->
                        RecentActivityCard(
                            activity = activity,
                            onClick = { /* Navigate to activity detail */ }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Stat Card Component - Shows individual stat with icon
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    unit: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Value and unit
            Column {
                Text(
                    value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    unit,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Recent Activity Card Component
 */
@Composable
private fun RecentActivityCard(
    activity: com.example.lifecare.data.PhysicalActivity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HealthColors.NeonGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (activity.activityType) {
                        "Lari" -> Icons.Default.DirectionsRun
                        "Bersepeda" -> Icons.Default.DirectionsBike
                        "Jalan" -> Icons.Default.DirectionsWalk
                        else -> Icons.Default.FitnessCenter
                    },
                    contentDescription = null,
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Activity details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.activityType ?: "Aktivitas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (activity.steps != null && activity.steps > 0) {
                        Text(
                            "${String.format("%.2f", activity.steps * 0.0008)} km",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        "${activity.duration} min",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (activity.caloriesBurned != null) {
                        Text(
                            "${activity.caloriesBurned} kal",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Date
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatDate(activity.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// Helper functions
private fun formatNumber(number: Int): String {
    return when {
        number >= 1000 -> String.format("%.1fk", number / 1000.0)
        else -> number.toString()
    }
}

private fun formatDuration(minutes: Int): String {
    return if (minutes >= 60) {
        String.format("%.1f", minutes / 60.0)
    } else {
        minutes.toString()
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}
