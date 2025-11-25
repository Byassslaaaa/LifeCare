package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.lifecare.data.BloodSugar
import com.example.lifecare.data.HealthDataManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodSugarScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var bsList by remember { mutableStateOf(healthDataManager.getBloodSugarList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kadar Gula Darah") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9C27B0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF9C27B0)
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
            // Latest BS Card
            bsList.firstOrNull()?.let { latest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF9C27B0)
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
                                "${latest.level} mg/dL",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9C27B0)
                            )
                            Text(
                                latest.measurementType,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
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
                items(bsList) { bs ->
                    BloodSugarHistoryItem(bs)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDialog) {
            AddBloodSugarDialog(
                onDismiss = { showDialog = false },
                onSave = { level, type ->
                    val bs = BloodSugar(level = level, measurementType = type)
                    healthDataManager.saveBloodSugar(bs)
                    bsList = healthDataManager.getBloodSugarList()
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun BloodSugarHistoryItem(bs: BloodSugar) {
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
                    dateFormat.format(Date(bs.timestamp)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${bs.level} mg/dL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getBloodSugarColor(bs.level, bs.measurementType)
                )
                Text(
                    bs.measurementType,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                getBloodSugarCategory(bs.level, bs.measurementType),
                fontSize = 12.sp,
                color = getBloodSugarColor(bs.level, bs.measurementType),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodSugarDialog(
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit
) {
    var level by remember { mutableStateOf("") }
    var measurementType by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var levelError by remember { mutableStateOf<String?>(null) }
    var typeError by remember { mutableStateOf<String?>(null) }
    val types = listOf("Puasa", "Setelah Makan", "Random", "Sebelum Tidur")
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Data Gula Darah") },
        text = {
            Column {
                OutlinedTextField(
                    value = level,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            level = it
                            levelError = null
                        }
                    },
                    label = { Text("Kadar Gula (mg/dL)") },
                    placeholder = { Text("Normal: 70-140") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = levelError != null,
                    supportingText = {
                        if (levelError != null) {
                            Text(levelError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = measurementType,
                        onValueChange = {},
                        label = { Text("Jenis Pengukuran") },
                        readOnly = true,
                        isError = typeError != null,
                        supportingText = {
                            if (typeError != null) {
                                Text(typeError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    measurementType = type
                                    typeError = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false

                    // Validate blood sugar level
                    val lvl = level.toDoubleOrNull()
                    if (level.isEmpty()) {
                        levelError = "Kadar gula harus diisi"
                        hasError = true
                    } else if (lvl == null) {
                        levelError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (lvl < 20 || lvl > 600) {
                        levelError = "Nilai harus antara 20-600"
                        hasError = true
                    }

                    // Validate measurement type
                    if (measurementType.isEmpty()) {
                        typeError = "Pilih jenis pengukuran"
                        hasError = true
                    }

                    if (!hasError && lvl != null) {
                        // Show warning for abnormal values
                        when (measurementType) {
                            "Puasa" -> {
                                if (lvl >= 126) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ PERINGATAN: Gula darah puasa sangat tinggi! Konsultasi dokter!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (lvl >= 100) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ Gula darah puasa tinggi (Prediabetes). Jaga pola makan.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (lvl < 70) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ Gula darah rendah (Hipoglikemia). Konsumsi makanan/minuman manis.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            "Setelah Makan" -> {
                                if (lvl >= 200) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ PERINGATAN: Gula darah sangat tinggi! Konsultasi dokter!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (lvl >= 140) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ Gula darah tinggi. Perhatikan pola makan.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else -> {
                                if (lvl >= 200) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ PERINGATAN: Gula darah sangat tinggi! Konsultasi dokter!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (lvl < 70) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ Gula darah rendah. Konsumsi makanan/minuman manis.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        onSave(lvl, measurementType)
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

fun getBloodSugarCategory(level: Double, type: String): String {
    return when (type) {
        "Puasa" -> when {
            level < 100 -> "Normal"
            level < 126 -> "Prediabetes"
            else -> "Diabetes"
        }
        "Setelah Makan" -> when {
            level < 140 -> "Normal"
            level < 200 -> "Prediabetes"
            else -> "Diabetes"
        }
        else -> when {
            level < 140 -> "Normal"
            level < 200 -> "Tinggi"
            else -> "Sangat Tinggi"
        }
    }
}

fun getBloodSugarColor(level: Double, type: String): Color {
    return when (type) {
        "Puasa" -> when {
            level < 100 -> Color(0xFF4CAF50) // Normal - Green
            level < 126 -> Color(0xFFFFC107) // Prediabetes - Yellow
            else -> Color(0xFFF44336) // Diabetes - Red
        }
        "Setelah Makan" -> when {
            level < 140 -> Color(0xFF4CAF50)
            level < 200 -> Color(0xFFFFC107)
            else -> Color(0xFFF44336)
        }
        else -> when {
            level < 140 -> Color(0xFF4CAF50)
            level < 200 -> Color(0xFFFFC107)
            else -> Color(0xFFF44336)
        }
    }
}
