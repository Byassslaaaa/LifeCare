package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.data.BodyMetrics
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
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
                title = { Text("Berat & Tinggi Badan", style = HealthTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.BodyMetrics,
                    titleContentColor = HealthColors.TextOnPrimary,
                    navigationIconContentColor = HealthColors.TextOnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = HealthColors.BodyMetrics,
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
            metricsList.firstOrNull()?.let { latest ->
                FeaturedCard(
                    modifier = Modifier.fillMaxWidth().padding(HealthSpacing.screenPadding),
                    backgroundColor = HealthColors.BodyMetrics,
                    contentColor = HealthColors.TextOnPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null, modifier = Modifier.size(HealthSpacing.iconSizeLarge), tint = HealthColors.TextOnPrimary)
                        Spacer(modifier = Modifier.width(HealthSpacing.medium))
                        Column {
                            Text("Data Terbaru", style = HealthTypography.bodySmall, color = HealthColors.TextOnPrimary.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                            Text("${latest.weight} kg / ${latest.height} cm", style = HealthTypography.displaySmall, fontWeight = FontWeight.Bold, color = HealthColors.TextOnPrimary)
                            Text("BMI: ${String.format("%.1f", latest.bmi)}", style = HealthTypography.bodySmall, color = HealthColors.TextOnPrimary.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            Text("Riwayat", style = HealthTypography.headlineSmall, modifier = Modifier.padding(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            ) {
                items(metricsList) { metric ->
                    BodyMetricsHistoryItem(metric)
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
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
fun BodyMetricsHistoryItem(metric: BodyMetrics) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    HealthCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(dateFormat.format(Date(metric.timestamp)), style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
                Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                Text("${metric.weight} kg / ${metric.height} cm", style = HealthTypography.titleMedium, fontWeight = FontWeight.Medium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("BMI", style = HealthTypography.labelSmall, color = HealthColors.TextSecondary)
                Text(String.format("%.1f", metric.bmi), style = HealthTypography.titleLarge, fontWeight = FontWeight.Bold, color = getBMIColor(metric.bmi))
                Text(getBMICategory(metric.bmi), style = HealthTypography.labelSmall, color = getBMIColor(metric.bmi))
            }
        }
    }
}

@Composable
fun AddBodyMetricsDialog(onDismiss: () -> Unit, onSave: (Double, Double) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

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
        title = { Text("Tambah Data Berat & Tinggi", style = HealthTypography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(HealthSpacing.small)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) { weight = it; weightError = null } },
                    label = { Text("Berat Badan (kg)") },
                    placeholder = { Text("Contoh: 65.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = weightError != null,
                    supportingText = { if (weightError != null) Text(weightError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.BodyMetrics, focusedLabelColor = HealthColors.BodyMetrics)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { if (it.isEmpty() || (it.matches(Regex("^\\d*\\.?\\d*$")) && it.length <= 5)) { height = it; heightError = null } },
                    label = { Text("Tinggi Badan (cm)") },
                    placeholder = { Text("Contoh: 170") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = heightError != null,
                    supportingText = { if (heightError != null) Text(heightError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.BodyMetrics, focusedLabelColor = HealthColors.BodyMetrics)
                )

                if (bmiPreview != null) {
                    Surface(modifier = Modifier.fillMaxWidth(), color = HealthColors.InfoLight, shape = MaterialTheme.shapes.small) {
                        Column(modifier = Modifier.padding(HealthSpacing.small), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BMI Anda", style = HealthTypography.labelSmall, color = HealthColors.TextSecondary)
                            Text(bmiPreview, style = HealthTypography.headlineMedium, fontWeight = FontWeight.Bold, color = getBMIColor(bmiPreview.toDouble()))
                            Text(getBMICategory(bmiPreview.toDouble()), style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    val w = weight.toDoubleOrNull()
                    if (weight.isEmpty()) { weightError = "Berat badan harus diisi"; hasError = true }
                    else if (w == null) { weightError = "Masukkan angka yang valid"; hasError = true }
                    else if (w < 20 || w > 300) { weightError = "Nilai harus antara 20-300 kg"; hasError = true }

                    val h = height.toDoubleOrNull()
                    if (height.isEmpty()) { heightError = "Tinggi badan harus diisi"; hasError = true }
                    else if (h == null) { heightError = "Masukkan angka yang valid"; hasError = true }
                    else if (h < 50 || h > 250) { heightError = "Nilai harus antara 50-250 cm"; hasError = true }

                    if (!hasError && w != null && h != null) {
                        val bmi = w / ((h / 100) * (h / 100))
                        val message = when {
                            bmi < 18.5 -> "BMI: ${String.format("%.1f", bmi)} (Kurang). Konsultasi ahli gizi."
                            bmi < 25.0 -> "BMI: ${String.format("%.1f", bmi)} (Normal). Pertahankan!"
                            bmi < 30.0 -> "BMI: ${String.format("%.1f", bmi)} (Berlebih). Diet & olahraga."
                            else -> "BMI: ${String.format("%.1f", bmi)} (Obesitas). Konsultasi dokter!"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        onSave(w, h)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HealthColors.BodyMetrics)
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = HealthColors.TextSecondary) } }
    )
}

fun getBMICategory(bmi: Double): String = when {
    bmi < 18.5 -> "Kurang"
    bmi < 25.0 -> "Normal"
    bmi < 30.0 -> "Berlebih"
    else -> "Obesitas"
}

fun getBMIColor(bmi: Double): Color = when {
    bmi < 18.5 -> HealthColors.Info
    bmi < 25.0 -> HealthColors.Success
    bmi < 30.0 -> HealthColors.Warning
    else -> HealthColors.Error
}
