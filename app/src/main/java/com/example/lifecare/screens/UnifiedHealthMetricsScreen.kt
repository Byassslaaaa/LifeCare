package com.example.lifecare.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.theme.HealthColors

/**
 * UnifiedHealthMetricsScreen - Menu selector untuk Data Kesehatan
 * Mengarahkan ke screen individual: BodyMetrics, BloodPressure, BloodSugar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedHealthMetricsScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    // Navigation state untuk sub-screens
    var currentSubScreen by remember { mutableStateOf<String?>(null) }

    // Jika ada sub-screen aktif, tampilkan sub-screen
    when (currentSubScreen) {
        "body_metrics" -> {
            BodyMetricsScreen(
                healthDataManager = healthDataManager,
                onBackClick = { currentSubScreen = null }
            )
        }
        "blood_pressure" -> {
            BloodPressureScreen(
                healthDataManager = healthDataManager,
                onBackClick = { currentSubScreen = null }
            )
        }
        "blood_sugar" -> {
            BloodSugarScreen(
                healthDataManager = healthDataManager,
                onBackClick = { currentSubScreen = null }
            )
        }
        else -> {
            // Main menu untuk memilih data kesehatan
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Data Kesehatan",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = HealthColors.NeonGreen
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header text
                    Text(
                        "Pilih jenis data kesehatan yang ingin Anda tambahkan",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Card 1: Berat & Tinggi Badan
                    HealthDataMenuCard(
                        title = "Berat & Tinggi Badan",
                        description = "BMI dan berat badan",
                        icon = Icons.Default.MonitorWeight,
                        iconColor = HealthColors.NeonGreen,
                        onClick = { currentSubScreen = "body_metrics" }
                    )

                    // Card 2: Tekanan Darah
                    HealthDataMenuCard(
                        title = "Tekanan Darah",
                        description = "Sistolik dan diastolik",
                        icon = Icons.Default.Favorite,
                        iconColor = HealthColors.NeonGreen,
                        onClick = { currentSubScreen = "blood_pressure" }
                    )

                    // Card 3: Gula Darah
                    HealthDataMenuCard(
                        title = "Gula Darah",
                        description = "Kadar gula darah",
                        icon = Icons.Default.Bloodtype,
                        iconColor = HealthColors.NeonGreen,
                        onClick = { currentSubScreen = "blood_sugar" }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Info text di bawah
                    Text(
                        "Setiap data akan disimpan dengan timestamp saat ini",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthDataMenuCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor.copy(alpha = 0.1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Arrow icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
