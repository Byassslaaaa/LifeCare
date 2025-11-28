package com.example.lifecare.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import com.example.lifecare.utils.PermissionHelper

@Composable
fun RunTrackingPermissionScreen(
    onPermissionGranted: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var permissionDeniedPermanently by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }

        if (granted) {
            onPermissionGranted()
        } else {
            // Check if user denied permanently
            val allDenied = permissions.values.none { it }
            permissionDeniedPermanently = allDenied
        }
    }

    LaunchedEffect(Unit) {
        if (PermissionHelper.hasLocationPermission(context)) {
            onPermissionGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HealthSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = HealthColors.Activity
        )

        Spacer(modifier = Modifier.height(HealthSpacing.large))

        Text(
            text = "Akses Lokasi Diperlukan",
            style = HealthTypography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(HealthSpacing.medium))

        Text(
            text = "Untuk melacak aktivitas Anda dengan GPS, LifeCare memerlukan akses ke lokasi perangkat.",
            style = HealthTypography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(HealthSpacing.extraLarge))

        // Permission benefits
        Surface(
            color = HealthColors.Activity.copy(alpha = 0.1f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(HealthSpacing.medium)
            ) {
                PermissionBenefit(
                    icon = Icons.Default.Route,
                    text = "Rekam rute lari Anda secara real-time"
                )
                Spacer(modifier = Modifier.height(HealthSpacing.small))
                PermissionBenefit(
                    icon = Icons.Default.Speed,
                    text = "Hitung jarak, kecepatan, dan pace akurat"
                )
                Spacer(modifier = Modifier.height(HealthSpacing.small))
                PermissionBenefit(
                    icon = Icons.Default.Timeline,
                    text = "Lihat statistik dan riwayat aktivitas"
                )
            }
        }

        Spacer(modifier = Modifier.height(HealthSpacing.extraLarge))

        if (permissionDeniedPermanently) {
            // Show settings button if permanently denied
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(HealthSpacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
                    Text(
                        text = "Izin lokasi ditolak. Silakan aktifkan di Pengaturan aplikasi.",
                        style = HealthTypography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(HealthSpacing.medium))

            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.Activity
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(HealthSpacing.small))
                Text("Buka Pengaturan", style = HealthTypography.titleMedium)
            }
        } else {
            // Show permission request button
            Button(
                onClick = {
                    permissionLauncher.launch(PermissionHelper.getRequiredLocationPermissions())
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.Activity
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(HealthSpacing.small))
                Text("Izinkan Akses Lokasi", style = HealthTypography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(HealthSpacing.small))

        TextButton(onClick = onNavigateBack) {
            Text("Kembali", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(HealthSpacing.large))

        // Privacy note
        Text(
            text = "🔒 Data lokasi Anda hanya digunakan untuk tracking aktivitas dan disimpan secara lokal di perangkat Anda.",
            style = HealthTypography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun PermissionBenefit(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HealthColors.Activity,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(HealthSpacing.small))
        Text(
            text = text,
            style = HealthTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
