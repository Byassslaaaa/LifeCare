package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
                title = { Text("Tekanan Darah") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE91E63),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFE91E63)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Data Terbaru",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${latest.systolic}/${latest.diastolic} mmHg",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                            if (latest.heartRate != null) {
                                Text(
                                    "Detak Jantung: ${latest.heartRate} BPM",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // History List
            Text(
                "Riwayat",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(bpList) { bp ->
                    BloodPressureHistoryItem(bp)
                    Spacer(modifier = Modifier.height(8.dp))
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    dateFormat.format(Date(bp.timestamp)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${bp.systolic}/${bp.diastolic} mmHg",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getBPColor(bp.systolic, bp.diastolic)
                )
                if (bp.heartRate != null) {
                    Text(
                        "Detak: ${bp.heartRate} BPM",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Text(
                getBPCategory(bp.systolic, bp.diastolic),
                fontSize = 12.sp,
                color = getBPColor(bp.systolic, bp.diastolic),
                fontWeight = FontWeight.Medium
            )
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
        title = { Text("Tambah Data Tekanan Darah") },
        text = {
            Column {
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
                            Text(systolicError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                            Text(diastolicError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                            Text(heartRateError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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
                                "⚠️ PERINGATAN: Tekanan darah sangat tinggi! Segera konsultasi dokter!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (sys >= 140 || dia >= 90) {
                            Toast.makeText(
                                context,
                                "⚠️ Tekanan darah tinggi. Pertimbangkan konsultasi dokter.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (sys < 90 || dia < 60) {
                            Toast.makeText(
                                context,
                                "⚠️ Tekanan darah rendah. Perhatikan kondisi Anda.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        onSave(sys, dia, hr)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
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
        systolic < 120 && diastolic < 80 -> Color(0xFF4CAF50) // Normal - Green
        systolic < 130 && diastolic < 80 -> Color(0xFFFFC107) // Elevated - Yellow
        systolic < 140 || diastolic < 90 -> Color(0xFFFF9800) // Stage 1 - Orange
        systolic < 180 || diastolic < 120 -> Color(0xFFFF5722) // Stage 2 - Deep Orange
        else -> Color(0xFFF44336) // Crisis - Red
    }
}
