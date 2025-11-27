package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedHealthMetricsScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk form inputs
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var bloodSugar by remember { mutableStateOf("") }
    var bloodSugarType by remember { mutableStateOf("Puasa") }
    var expanded by remember { mutableStateOf(false) }

    // Latest data
    val latestBodyMetrics = healthDataManager.getLatestBodyMetrics()
    val latestBloodPressure = healthDataManager.getLatestBloodPressure()
    val latestBloodSugar = healthDataManager.getLatestBloodSugar()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Kesehatan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5DCCB4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Text(
                "Masukkan data kesehatan Anda hari ini",
                fontSize = 14.sp,
                color = Color(0xFF6C757D)
            )

            // Latest Values Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Data Terakhir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Berat Badan
                        LatestMetricItem(
                            icon = Icons.Default.MonitorWeight,
                            label = "Berat Badan",
                            value = latestBodyMetrics?.let { "${it.weight} kg" } ?: "-",
                            color = Color(0xFF2196F3)
                        )

                        // Tekanan Darah
                        LatestMetricItem(
                            icon = Icons.Default.Favorite,
                            label = "Tekanan Darah",
                            value = latestBloodPressure?.let { "${it.systolic}/${it.diastolic}" } ?: "-",
                            color = Color(0xFFE91E63)
                        )

                        // Gula Darah
                        LatestMetricItem(
                            icon = Icons.Default.Bloodtype,
                            label = "Gula Darah",
                            value = latestBloodSugar?.let { "${it.level} mg/dL" } ?: "-",
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            Divider(color = Color(0xFFE0E0E0))

            // Input Section
            Text(
                "Input Data Baru",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            // Berat & Tinggi Badan Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.MonitorWeight,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Berat & Tinggi Badan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )
                    }

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Berat Badan (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Tinggi Badan (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // BMI Display
                    if (weight.isNotBlank() && height.isNotBlank()) {
                        val w = weight.toDoubleOrNull()
                        val h = height.toDoubleOrNull()
                        if (w != null && h != null && h > 0) {
                            val bmi = w / ((h / 100) * (h / 100))
                            val bmiCategory = when {
                                bmi < 18.5 -> "Kurang"
                                bmi < 25 -> "Normal"
                                bmi < 30 -> "Berlebih"
                                else -> "Obesitas"
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "BMI: ${"%.1f".format(bmi)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2196F3)
                                    )
                                    Text(
                                        bmiCategory,
                                        fontSize = 12.sp,
                                        color = Color(0xFF2196F3)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tekanan Darah Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Tekanan Darah",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = systolic,
                            onValueChange = { systolic = it },
                            label = { Text("Sistolik") },
                            placeholder = { Text("120") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = diastolic,
                            onValueChange = { diastolic = it },
                            label = { Text("Diastolik") },
                            placeholder = { Text("80") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Text(
                        "Normal: 120/80 mmHg",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D)
                    )
                }
            }

            // Gula Darah Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Gula Darah",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D3748)
                        )
                    }

                    OutlinedTextField(
                        value = bloodSugar,
                        onValueChange = { bloodSugar = it },
                        label = { Text("Kadar Gula Darah (mg/dL)") },
                        placeholder = { Text("100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Dropdown for Blood Sugar Type
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = bloodSugarType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jenis Pengukuran") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("Puasa", "Setelah Makan", "Random").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        bloodSugarType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        "Normal (Puasa): 70-100 mg/dL",
                        fontSize = 12.sp,
                        color = Color(0xFF6C757D)
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    var saved = false

                    // Save Body Metrics if filled
                    if (weight.isNotBlank() && height.isNotBlank()) {
                        val w = weight.toDoubleOrNull()
                        val h = height.toDoubleOrNull()
                        if (w != null && h != null) {
                            val bodyMetrics = BodyMetrics(
                                weight = w,
                                height = h,
                                timestamp = System.currentTimeMillis()
                            )
                            healthDataManager.saveBodyMetrics(bodyMetrics)
                            saved = true
                        }
                    }

                    // Save Blood Pressure if filled
                    if (systolic.isNotBlank() && diastolic.isNotBlank()) {
                        val sys = systolic.toIntOrNull()
                        val dia = diastolic.toIntOrNull()
                        if (sys != null && dia != null) {
                            val bloodPressure = BloodPressure(
                                systolic = sys,
                                diastolic = dia,
                                heartRate = null,
                                timestamp = System.currentTimeMillis()
                            )
                            healthDataManager.saveBloodPressure(bloodPressure)
                            saved = true
                        }
                    }

                    // Save Blood Sugar if filled
                    if (bloodSugar.isNotBlank()) {
                        val bs = bloodSugar.toDoubleOrNull()
                        if (bs != null) {
                            val bloodSugarData = BloodSugar(
                                level = bs,
                                measurementType = bloodSugarType,
                                timestamp = System.currentTimeMillis()
                            )
                            healthDataManager.saveBloodSugar(bloodSugarData)
                            saved = true
                        }
                    }

                    if (saved) {
                        Toast.makeText(context, "✅ Data kesehatan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        // Reset form
                        weight = ""
                        height = ""
                        systolic = ""
                        diastolic = ""
                        bloodSugar = ""
                        bloodSugarType = "Puasa"
                    } else {
                        Toast.makeText(context, "⚠️ Isi minimal satu data untuk disimpan", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5DCCB4)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Simpan Data",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Info Text
            Text(
                "💡 Anda dapat mengisi satu, dua, atau semua data sekaligus",
                fontSize = 12.sp,
                color = Color(0xFF6C757D),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun LatestMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            label,
            fontSize = 10.sp,
            color = Color(0xFF6C757D)
        )
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
