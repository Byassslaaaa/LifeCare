package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.data.*
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography

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
                title = {
                    Text(
                        "Data Kesehatan",
                        style = HealthTypography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.Primary,
                    titleContentColor = HealthColors.TextOnPrimary,
                    navigationIconContentColor = HealthColors.TextOnPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HealthColors.Background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(HealthSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(HealthSpacing.cardSpacing)
        ) {
            // Header Info
            Text(
                "Masukkan data kesehatan Anda hari ini",
                style = HealthTypography.bodyMedium,
                color = HealthColors.TextSecondary
            )

            // Latest Values Summary Card
            HealthCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Data Terakhir",
                    style = HealthTypography.titleMedium,
                    color = HealthColors.TextPrimary
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
                        color = HealthColors.BodyMetrics
                    )

                    // Tekanan Darah
                    LatestMetricItem(
                        icon = Icons.Default.Favorite,
                        label = "Tekanan Darah",
                        value = latestBloodPressure?.let { "${it.systolic}/${it.diastolic}" } ?: "-",
                        color = HealthColors.BloodPressure
                    )

                    // Gula Darah
                    LatestMetricItem(
                        icon = Icons.Default.Bloodtype,
                        label = "Gula Darah",
                        value = latestBloodSugar?.let { "${it.level} mg/dL" } ?: "-",
                        color = HealthColors.BloodSugar
                    )
                }
            }

            Divider(color = HealthColors.Divider)

            // Input Section Header
            Text(
                "Input Data Baru",
                style = HealthTypography.headlineSmall,
                color = HealthColors.TextPrimary
            )

            // Berat & Tinggi Badan Section
            HealthDataCard(
                modifier = Modifier.fillMaxWidth(),
                dataType = HealthDataType.BODY_METRICS
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.xSmall)
                ) {
                    Icon(
                        Icons.Default.MonitorWeight,
                        contentDescription = null,
                        tint = HealthColors.BodyMetrics,
                        modifier = Modifier.size(HealthSpacing.iconSize)
                    )
                    Text(
                        "Berat & Tinggi Badan",
                        style = HealthTypography.titleMedium,
                        color = HealthColors.TextPrimary
                    )
                }

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Berat Badan (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BodyMetrics,
                        focusedLabelColor = HealthColors.BodyMetrics
                    )
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Tinggi Badan (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BodyMetrics,
                        focusedLabelColor = HealthColors.BodyMetrics
                    )
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
                            color = HealthColors.InfoLight,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(HealthSpacing.small),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "BMI: ${"%.1f".format(bmi)}",
                                    style = HealthTypography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = HealthColors.Info
                                )
                                Text(
                                    bmiCategory,
                                    style = HealthTypography.bodySmall,
                                    color = HealthColors.Info
                                )
                            }
                        }
                    }
                }
            }

            // Tekanan Darah Section
            HealthDataCard(
                modifier = Modifier.fillMaxWidth(),
                dataType = HealthDataType.BLOOD_PRESSURE
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.xSmall)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = HealthColors.BloodPressure,
                        modifier = Modifier.size(HealthSpacing.iconSize)
                    )
                    Text(
                        "Tekanan Darah",
                        style = HealthTypography.titleMedium,
                        color = HealthColors.TextPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.xSmall)
                ) {
                    OutlinedTextField(
                        value = systolic,
                        onValueChange = { systolic = it },
                        label = { Text("Sistolik") },
                        placeholder = { Text("120") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthColors.BloodPressure,
                            focusedLabelColor = HealthColors.BloodPressure
                        )
                    )

                    OutlinedTextField(
                        value = diastolic,
                        onValueChange = { diastolic = it },
                        label = { Text("Diastolik") },
                        placeholder = { Text("80") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthColors.BloodPressure,
                            focusedLabelColor = HealthColors.BloodPressure
                        )
                    )
                }

                Text(
                    "Normal: 120/80 mmHg",
                    style = HealthTypography.bodySmall,
                    color = HealthColors.TextSecondary
                )
            }

            // Gula Darah Section
            HealthDataCard(
                modifier = Modifier.fillMaxWidth(),
                dataType = HealthDataType.BLOOD_SUGAR
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HealthSpacing.xSmall)
                ) {
                    Icon(
                        Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = HealthColors.BloodSugar,
                        modifier = Modifier.size(HealthSpacing.iconSize)
                    )
                    Text(
                        "Gula Darah",
                        style = HealthTypography.titleMedium,
                        color = HealthColors.TextPrimary
                    )
                }

                OutlinedTextField(
                    value = bloodSugar,
                    onValueChange = { bloodSugar = it },
                    label = { Text("Kadar Gula Darah (mg/dL)") },
                    placeholder = { Text("100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BloodSugar,
                        focusedLabelColor = HealthColors.BloodSugar
                    )
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthColors.BloodSugar,
                            focusedLabelColor = HealthColors.BloodSugar
                        )
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
                    style = HealthTypography.bodySmall,
                    color = HealthColors.TextSecondary
                )
            }

            // Save Button using new PrimaryButton component
            PrimaryButton(
                text = "Simpan Data",
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
                        Toast.makeText(context, "Data kesehatan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                        // Reset form
                        weight = ""
                        height = ""
                        systolic = ""
                        diastolic = ""
                        bloodSugar = ""
                        bloodSugarType = "Puasa"
                    } else {
                        Toast.makeText(context, "Isi minimal satu data untuk disimpan", Toast.LENGTH_SHORT).show()
                    }
                },
                icon = Icons.Default.Save,
                modifier = Modifier.fillMaxWidth()
            )

            // Info Text
            Text(
                "Anda dapat mengisi satu, dua, atau semua data sekaligus",
                style = HealthTypography.bodySmall,
                color = HealthColors.TextSecondary,
                modifier = Modifier.padding(vertical = HealthSpacing.xSmall)
            )
        }
    }
}

@Composable
private fun LatestMetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HealthSpacing.xxSmall)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(HealthSpacing.iconSize)
        )
        Text(
            label,
            style = HealthTypography.labelSmall,
            color = HealthColors.TextSecondary
        )
        Text(
            value,
            style = HealthTypography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
