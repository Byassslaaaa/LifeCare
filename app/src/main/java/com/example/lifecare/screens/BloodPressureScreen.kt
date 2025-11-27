package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.data.BloodPressure
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodPressureScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var bpList by remember { mutableStateOf(healthDataManager.getBloodPressureList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tekanan Darah",
                        style = HealthTypography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.BloodPressure,
                    titleContentColor = HealthColors.TextOnPrimary,
                    navigationIconContentColor = HealthColors.TextOnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = HealthColors.BloodPressure,
                contentColor = HealthColors.TextOnPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Latest BP Card
            bpList.firstOrNull()?.let { latest ->
                FeaturedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(HealthSpacing.screenPadding),
                    backgroundColor = HealthColors.BloodPressure,
                    contentColor = HealthColors.TextOnPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(HealthSpacing.iconSizeLarge),
                            tint = HealthColors.TextOnPrimary
                        )
                        Spacer(modifier = Modifier.width(HealthSpacing.medium))
                        Column {
                            Text(
                                "Data Terbaru",
                                style = HealthTypography.bodySmall,
                                color = HealthColors.TextOnPrimary.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                            Text(
                                "${latest.systolic}/${latest.diastolic} mmHg",
                                style = HealthTypography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = HealthColors.TextOnPrimary
                            )
                            if (latest.heartRate != null) {
                                Text(
                                    "Detak Jantung: ${latest.heartRate} BPM",
                                    style = HealthTypography.bodySmall,
                                    color = HealthColors.TextOnPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // History List
            Text(
                "Riwayat",
                style = HealthTypography.headlineSmall,
                modifier = Modifier.padding(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            ) {
                items(bpList) { bp ->
                    BloodPressureHistoryItem(bp)
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
                }
            }
        }

        if (showDialog) {
            AddBloodPressureDialog(
                onDismiss = { showDialog = false },
                onSave = { systolic, diastolic, heartRate ->
                    val bp = BloodPressure(
                        systolic = systolic,
                        diastolic = diastolic,
                        heartRate = heartRate
                    )
                    healthDataManager.saveBloodPressure(bp)
                    bpList = healthDataManager.getBloodPressureList()
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun BloodPressureHistoryItem(bp: BloodPressure) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    HealthCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    dateFormat.format(Date(bp.timestamp)),
                    style = HealthTypography.bodySmall,
                    color = HealthColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                Text(
                    "${bp.systolic}/${bp.diastolic} mmHg",
                    style = HealthTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getBPColor(bp.systolic, bp.diastolic)
                )
                if (bp.heartRate != null) {
                    Text(
                        "Detak: ${bp.heartRate} BPM",
                        style = HealthTypography.bodySmall,
                        color = HealthColors.TextSecondary
                    )
                }
            }
            Surface(
                color = getBPColor(bp.systolic, bp.diastolic).copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    getBPCategory(bp.systolic, bp.diastolic),
                    style = HealthTypography.labelMedium,
                    color = getBPColor(bp.systolic, bp.diastolic),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = HealthSpacing.small, vertical = HealthSpacing.xxSmall)
                )
            }
        }
    }
}

@Composable
fun AddBloodPressureDialog(
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int?) -> Unit
) {
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var heartRate by remember { mutableStateOf("") }
    var systolicError by remember { mutableStateOf<String?>(null) }
    var diastolicError by remember { mutableStateOf<String?>(null) }
    var heartRateError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Tambah Data Tekanan Darah",
                style = HealthTypography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(HealthSpacing.small)
            ) {
                OutlinedTextField(
                    value = systolic,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 3)) {
                            systolic = it
                            systolicError = null
                        }
                    },
                    label = { Text("Sistolik (atas) mmHg") },
                    placeholder = { Text("Normal: 90-120") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = systolicError != null,
                    supportingText = {
                        if (systolicError != null) {
                            Text(systolicError!!, color = HealthColors.Error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BloodPressure,
                        focusedLabelColor = HealthColors.BloodPressure
                    )
                )

                OutlinedTextField(
                    value = diastolic,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 3)) {
                            diastolic = it
                            diastolicError = null
                        }
                    },
                    label = { Text("Diastolik (bawah) mmHg") },
                    placeholder = { Text("Normal: 60-80") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = diastolicError != null,
                    supportingText = {
                        if (diastolicError != null) {
                            Text(diastolicError!!, color = HealthColors.Error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BloodPressure,
                        focusedLabelColor = HealthColors.BloodPressure
                    )
                )

                OutlinedTextField(
                    value = heartRate,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 3)) {
                            heartRate = it
                            heartRateError = null
                        }
                    },
                    label = { Text("Detak Jantung (opsional)") },
                    placeholder = { Text("Normal: 60-100 BPM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = heartRateError != null,
                    supportingText = {
                        if (heartRateError != null) {
                            Text(heartRateError!!, color = HealthColors.Error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BloodPressure,
                        focusedLabelColor = HealthColors.BloodPressure
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false

                    // Validate systolic
                    val sys = systolic.toIntOrNull()
                    if (systolic.isEmpty()) {
                        systolicError = "Sistolik harus diisi"
                        hasError = true
                    } else if (sys == null) {
                        systolicError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (sys < 70 || sys > 250) {
                        systolicError = "Nilai harus antara 70-250"
                        hasError = true
                    }

                    // Validate diastolic
                    val dia = diastolic.toIntOrNull()
                    if (diastolic.isEmpty()) {
                        diastolicError = "Diastolik harus diisi"
                        hasError = true
                    } else if (dia == null) {
                        diastolicError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (dia < 40 || dia > 150) {
                        diastolicError = "Nilai harus antara 40-150"
                        hasError = true
                    }

                    // Validate heart rate if provided
                    val hr = if (heartRate.isNotEmpty()) {
                        val hrValue = heartRate.toIntOrNull()
                        if (hrValue == null) {
                            heartRateError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (hrValue < 30 || hrValue > 250) {
                            heartRateError = "Nilai harus antara 30-250"
                            hasError = true
                            null
                        } else {
                            hrValue
                        }
                    } else null

                    // Check if systolic > diastolic
                    if (sys != null && dia != null && sys <= dia) {
                        systolicError = "Sistolik harus lebih besar dari diastolik"
                        hasError = true
                    }

                    if (!hasError && sys != null && dia != null) {
                        // Show warning for abnormal values
                        if (sys >= 180 || dia >= 120) {
                            Toast.makeText(
                                context,
                                "PERINGATAN: Tekanan darah sangat tinggi! Segera konsultasi dokter!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (sys >= 140 || dia >= 90) {
                            Toast.makeText(
                                context,
                                "Tekanan darah tinggi. Pertimbangkan konsultasi dokter.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (sys < 90 || dia < 60) {
                            Toast.makeText(
                                context,
                                "Tekanan darah rendah. Perhatikan kondisi Anda.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        onSave(sys, dia, hr)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.BloodPressure
                )
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = HealthColors.TextSecondary)
            }
        }
    )
}

fun getBPCategory(systolic: Int, diastolic: Int): String {
    return when {
        systolic < 120 && diastolic < 80 -> "Normal"
        systolic < 130 && diastolic < 80 -> "Elevated"
        systolic < 140 || diastolic < 90 -> "Stage 1"
        systolic < 180 || diastolic < 120 -> "Stage 2"
        else -> "Crisis"
    }
}

fun getBPColor(systolic: Int, diastolic: Int): Color {
    return when {
        systolic < 120 && diastolic < 80 -> HealthColors.Success
        systolic < 130 && diastolic < 80 -> HealthColors.Warning
        systolic < 140 || diastolic < 90 -> Color(0xFFFF9800)
        systolic < 180 || diastolic < 120 -> Color(0xFFFF5722)
        else -> HealthColors.Error
    }
}
