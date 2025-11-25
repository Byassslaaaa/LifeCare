package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.lifecare.data.BodyMetrics
import com.example.lifecare.data.HealthDataManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMetricsScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var metricsList by remember { mutableStateOf(healthDataManager.getBodyMetricsList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Berat & Tinggi Badan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2196F3),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF2196F3)
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
            // Latest Metrics Card
            metricsList.firstOrNull()?.let { latest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Data Terbaru",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricItem("Berat", "${latest.weight} kg")
                            MetricItem("Tinggi", "${latest.height} cm")
                            MetricItem("BMI", String.format("%.1f", latest.bmi))
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
                items(metricsList) { metric ->
                    BodyMetricsHistoryItem(metric)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDialog) {
            AddBodyMetricsDialog(
                onDismiss = { showDialog = false },
                onSave = { weight, height ->
                    val metrics = BodyMetrics(weight = weight, height = height)
                    healthDataManager.saveBodyMetrics(metrics)
                    metricsList = healthDataManager.getBodyMetricsList()
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )
    }
}

@Composable
fun BodyMetricsHistoryItem(metric: BodyMetrics) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    dateFormat.format(Date(metric.timestamp)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Berat: ${metric.weight} kg | Tinggi: ${metric.height} cm",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("BMI", fontSize = 10.sp, color = Color.Gray)
                Text(
                    String.format("%.1f", metric.bmi),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getBMIColor(metric.bmi)
                )
            }
        }
    }
}

@Composable
fun AddBodyMetricsDialog(
    onDismiss: () -> Unit,
    onSave: (Double, Double) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Calculate BMI preview
    val bmiPreview = remember(weight, height) {
        val w = weight.toDoubleOrNull()
        val h = height.toDoubleOrNull()
        if (w != null && h != null && w > 0 && h > 0) {
            val bmi = w / ((h / 100) * (h / 100))
            String.format("%.1f", bmi)
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Data Berat & Tinggi") },
        text = {
            Column {
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            weight = it
                            weightError = null
                        }
                    },
                    label = { Text("Berat Badan (kg)") },
                    placeholder = { Text("Contoh: 65.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = weightError != null,
                    supportingText = {
                        if (weightError != null) {
                            Text(weightError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = {
                        if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) {
                            height = it
                            heightError = null
                        }
                    },
                    label = { Text("Tinggi Badan (cm)") },
                    placeholder = { Text("Contoh: 170") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = heightError != null,
                    supportingText = {
                        if (heightError != null) {
                            Text(heightError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // BMI Preview
                if (bmiPreview != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "BMI Anda",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Text(
                                bmiPreview,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = getBMIColor(bmiPreview.toDouble())
                            )
                            Text(
                                getBMICategory(bmiPreview.toDouble()),
                                fontSize = 11.sp,
                                color = Color.Gray
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

                    // Validate weight
                    val w = weight.toDoubleOrNull()
                    if (weight.isEmpty()) {
                        weightError = "Berat badan harus diisi"
                        hasError = true
                    } else if (w == null) {
                        weightError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (w < 20 || w > 300) {
                        weightError = "Nilai harus antara 20-300 kg"
                        hasError = true
                    }

                    // Validate height
                    val h = height.toDoubleOrNull()
                    if (height.isEmpty()) {
                        heightError = "Tinggi badan harus diisi"
                        hasError = true
                    } else if (h == null) {
                        heightError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (h < 50 || h > 250) {
                        heightError = "Nilai harus antara 50-250 cm"
                        hasError = true
                    }

                    if (!hasError && w != null && h != null) {
                        val bmi = w / ((h / 100) * (h / 100))

                        // Show BMI category info
                        val message = when {
                            bmi < 18.5 -> "BMI Anda: ${String.format("%.1f", bmi)} (Kurang Berat Badan). Pertimbangkan konsultasi ahli gizi."
                            bmi < 25.0 -> "BMI Anda: ${String.format("%.1f", bmi)} (Normal). Pertahankan pola hidup sehat!"
                            bmi < 30.0 -> "BMI Anda: ${String.format("%.1f", bmi)} (Kelebihan Berat Badan). Pertimbangkan diet sehat dan olahraga."
                            else -> "⚠️ BMI Anda: ${String.format("%.1f", bmi)} (Obesitas). Sangat disarankan konsultasi dokter."
                        }

                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        onSave(w, h)
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

fun getBMICategory(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Kurang Berat Badan"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Kelebihan Berat Badan"
        else -> "Obesitas"
    }
}

fun getBMIColor(bmi: Double): Color {
    return when {
        bmi < 18.5 -> Color(0xFF2196F3) // Underweight - Blue
        bmi < 25.0 -> Color(0xFF4CAF50) // Normal - Green
        bmi < 30.0 -> Color(0xFFFFC107) // Overweight - Yellow
        else -> Color(0xFFF44336) // Obese - Red
    }
}
