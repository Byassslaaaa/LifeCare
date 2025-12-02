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
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.MonitorWeight
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
import com.example.lifecare.data.BloodSugar
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
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
                title = { Text("Data Kesehatan", style = HealthTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = HealthColors.NeonGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
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
                    .padding(HealthSpacing.screenPadding),
                colors = CardDefaults.cardColors(containerColor = HealthColors.NeonGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Berat Badan
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MonitorWeight,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Berat Badan",
                            style = HealthTypography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            "-",
                            style = HealthTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Tekanan Darah
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tekanan Darah",
                            style = HealthTypography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            "70/50",
                            style = HealthTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Gula Darah
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Bloodtype,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Gula Darah",
                            style = HealthTypography.bodySmall,
                            color = Color.White
                        )
                        Text(
                            bsList.firstOrNull()?.let { "${it.level}" } ?: "-",
                            style = HealthTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Input Data Baru Section
            Text(
                "Input Data Baru",
                style = HealthTypography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input fields
            var bloodSugar by remember { mutableStateOf("") }
            var measurementType by remember { mutableStateOf("") }
            var expanded by remember { mutableStateOf(false) }
            val types = listOf("Puasa", "Setelah Makan", "Random", "Sebelum Tidur")

            // Card 1: Gula Darah
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HealthSpacing.screenPadding),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Section header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Bloodtype,
                            contentDescription = null,
                            tint = HealthColors.NeonGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Gula Darah",
                            style = HealthTypography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input fields
                    TextField(
                        value = bloodSugar,
                        onValueChange = { bloodSugar = it },
                        placeholder = { Text("Kadar Gula Darah (mg/dL)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(50.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = measurementType,
                            onValueChange = {},
                            placeholder = { Text("Jenis Pengukuran") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(50.dp)
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
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Normal (puasa): 70-100 mg/dL",
                        style = HealthTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button and note
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HealthSpacing.screenPadding)
            ) {
                Button(
                    onClick = {
                        val level = bloodSugar.toDoubleOrNull()
                        if (level != null && measurementType.isNotEmpty()) {
                            val bs = BloodSugar(level = level, measurementType = measurementType)
                            healthDataManager.saveBloodSugar(bs)
                            bsList = healthDataManager.getBloodSugarList()
                            Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            bloodSugar = ""
                            measurementType = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthColors.NeonGreen),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text("Simpan Data", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Anda dapat mengisi satu, dua, atau semua data sekaligus",
                    style = HealthTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BloodSugarHistoryItem(bs: BloodSugar) {
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
                    dateFormat.format(Date(bs.timestamp)),
                    style = HealthTypography.bodySmall,
                    color = HealthColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                Text(
                    "${bs.level} mg/dL",
                    style = HealthTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getBloodSugarColor(bs.level, bs.measurementType)
                )
                Text(
                    bs.measurementType,
                    style = HealthTypography.bodySmall,
                    color = HealthColors.TextSecondary
                )
            }
            Surface(
                color = getBloodSugarColor(bs.level, bs.measurementType).copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    getBloodSugarCategory(bs.level, bs.measurementType),
                    style = HealthTypography.labelMedium,
                    color = getBloodSugarColor(bs.level, bs.measurementType),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = HealthSpacing.small, vertical = HealthSpacing.xxSmall)
                )
            }
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
        title = {
            Text(
                "Tambah Data Gula Darah",
                style = HealthTypography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(HealthSpacing.small)
            ) {
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
                            Text(levelError!!, color = HealthColors.Error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.BloodSugar,
                        focusedLabelColor = HealthColors.BloodSugar
                    )
                )

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
                                Text(typeError!!, color = HealthColors.Error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthColors.BloodSugar,
                            focusedLabelColor = HealthColors.BloodSugar
                        )
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

                    if (measurementType.isEmpty()) {
                        typeError = "Pilih jenis pengukuran"
                        hasError = true
                    }

                    if (!hasError && lvl != null) {
                        when (measurementType) {
                            "Puasa" -> {
                                if (lvl >= 126) {
                                    Toast.makeText(context, "PERINGATAN: Gula darah puasa sangat tinggi! Konsultasi dokter!", Toast.LENGTH_LONG).show()
                                } else if (lvl >= 100) {
                                    Toast.makeText(context, "Gula darah puasa tinggi (Prediabetes). Jaga pola makan.", Toast.LENGTH_LONG).show()
                                } else if (lvl < 70) {
                                    Toast.makeText(context, "Gula darah rendah (Hipoglikemia). Konsumsi makanan/minuman manis.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            "Setelah Makan" -> {
                                if (lvl >= 200) {
                                    Toast.makeText(context, "PERINGATAN: Gula darah sangat tinggi! Konsultasi dokter!", Toast.LENGTH_LONG).show()
                                } else if (lvl >= 140) {
                                    Toast.makeText(context, "Gula darah tinggi. Perhatikan pola makan.", Toast.LENGTH_LONG).show()
                                }
                            }
                            else -> {
                                if (lvl >= 200) {
                                    Toast.makeText(context, "PERINGATAN: Gula darah sangat tinggi! Konsultasi dokter!", Toast.LENGTH_LONG).show()
                                } else if (lvl < 70) {
                                    Toast.makeText(context, "Gula darah rendah. Konsumsi makanan/minuman manis.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        onSave(lvl, measurementType)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.BloodSugar
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
            level < 100 -> HealthColors.Success
            level < 126 -> HealthColors.Warning
            else -> HealthColors.Error
        }
        "Setelah Makan" -> when {
            level < 140 -> HealthColors.Success
            level < 200 -> HealthColors.Warning
            else -> HealthColors.Error
        }
        else -> when {
            level < 140 -> HealthColors.Success
            level < 200 -> HealthColors.Warning
            else -> HealthColors.Error
        }
    }
}
