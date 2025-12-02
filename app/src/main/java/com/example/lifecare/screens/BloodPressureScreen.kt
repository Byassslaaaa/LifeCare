package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Get latest body metrics and blood sugar data
    val metricsList = healthDataManager.getBodyMetricsList()
    val bsList = healthDataManager.getBloodSugarList()

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
        ) {
            // Summary Card with 3 sections
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = HealthColors.NeonGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Berat Badan
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MonitorWeight,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Berat Badan",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            metricsList.firstOrNull()?.let { "${it.weight.toInt()} kg" } ?: "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Tekanan Darah
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tekanan Darah",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            bpList.firstOrNull()?.let { "${it.systolic}/${it.diastolic}" } ?: "-/-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Gula Darah
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Bloodtype,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Gula Darah",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            bsList.firstOrNull()?.let { "${it.level.toInt()}" } ?: "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Input Data Baru Section
            Text(
                "Input Data Baru",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Input fields
            var systolic by remember { mutableStateOf("") }
            var diastolic by remember { mutableStateOf("") }

            // Card 1: Tekanan Darah
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Section header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = HealthColors.NeonGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Tekanan Darah",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextField(
                            value = systolic,
                            onValueChange = { systolic = it },
                            placeholder = {
                                Text(
                                    "Sistolik",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        TextField(
                            value = diastolic,
                            onValueChange = { diastolic = it },
                            placeholder = {
                                Text(
                                    "Diastolik",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Normal: 120/80 mmHg",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button and note
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val sys = systolic.toIntOrNull()
                        val dia = diastolic.toIntOrNull()
                        if (sys != null && dia != null) {
                            val bp = BloodPressure(systolic = sys, diastolic = dia, heartRate = null)
                            healthDataManager.saveBloodPressure(bp)
                            bpList = healthDataManager.getBloodPressureList()
                            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            systolic = ""
                            diastolic = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthColors.NeonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Simpan Data",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Anda dapat mengisi satu, dua, atau semua data sekaligus",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
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
