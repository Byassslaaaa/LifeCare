package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
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
import com.example.lifecare.data.PhysicalActivity
import com.example.lifecare.data.HealthDataManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalActivityScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var activityList by remember { mutableStateOf(healthDataManager.getPhysicalActivityList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aktivitas Fisik") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF4CAF50)
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
            // Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Hari Ini",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${healthDataManager.getTodayTotalSteps()} langkah",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "${healthDataManager.getTodayTotalExerciseMinutes()} menit olahraga",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
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
                items(activityList) { activity ->
                    PhysicalActivityHistoryItem(activity)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showDialog) {
            AddPhysicalActivityDialog(
                onDismiss = { showDialog = false },
                onSave = { type, duration, steps, calories ->
                    val activity = PhysicalActivity(
                        activityType = type,
                        duration = duration,
                        steps = steps,
                        caloriesBurned = calories
                    )
                    healthDataManager.savePhysicalActivity(activity)
                    activityList = healthDataManager.getPhysicalActivityList()
                    Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun PhysicalActivityHistoryItem(activity: PhysicalActivity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        activity.activityType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        dateFormat.format(Date(activity.timestamp)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    "${activity.duration} min",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }

            if (activity.steps != null || activity.caloriesBurned != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (activity.steps != null) {
                        Text(
                            "${activity.steps} langkah",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    if (activity.caloriesBurned != null) {
                        Text(
                            "${activity.caloriesBurned} kal terbakar",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhysicalActivityDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int?, Int?) -> Unit
) {
    var activityType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var activityError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var stepsError by remember { mutableStateOf<String?>(null) }
    var caloriesError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val activityTypes = listOf(
        "Jalan Kaki", "Lari", "Bersepeda", "Berenang",
        "Yoga", "Gym", "Aerobik", "Basket", "Sepak Bola", "Lainnya"
    )

    // Auto-calculate estimated calories based on activity and duration
    val estimatedCalories = remember(activityType, duration) {
        val dur = duration.toIntOrNull()
        if (dur != null && activityType.isNotEmpty() && calories.isEmpty()) {
            // Rough estimates (calories per minute)
            val caloriesPerMinute = when (activityType) {
                "Jalan Kaki" -> 4
                "Lari" -> 10
                "Bersepeda" -> 7
                "Berenang" -> 8
                "Yoga" -> 3
                "Gym" -> 6
                "Aerobik" -> 7
                "Basket" -> 8
                "Sepak Bola" -> 9
                else -> 5
            }
            dur * caloriesPerMinute
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Aktivitas Fisik") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = activityType,
                        onValueChange = {},
                        label = { Text("Jenis Aktivitas") },
                        readOnly = true,
                        isError = activityError != null,
                        supportingText = {
                            if (activityError != null) {
                                Text(activityError!!, color = MaterialTheme.colorScheme.error)
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
                        activityTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    activityType = type
                                    activityError = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = duration,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 4)) {
                            duration = it
                            durationError = null
                        }
                    },
                    label = { Text("Durasi (menit)") },
                    placeholder = { Text("Contoh: 30") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = durationError != null,
                    supportingText = {
                        if (durationError != null) {
                            Text(durationError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = steps,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 6)) {
                            steps = it
                            stepsError = null
                        }
                    },
                    label = { Text("Langkah (opsional)") },
                    placeholder = { Text("Jumlah langkah") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = stepsError != null,
                    supportingText = {
                        if (stepsError != null) {
                            Text(stepsError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = calories,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 5)) {
                            calories = it
                            caloriesError = null
                        }
                    },
                    label = { Text("Kalori Terbakar (opsional)") },
                    placeholder = {
                        if (estimatedCalories != null) {
                            Text("Estimasi: ~$estimatedCalories kal")
                        } else {
                            Text("Jumlah kalori")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = caloriesError != null,
                    supportingText = {
                        if (caloriesError != null) {
                            Text(caloriesError!!, color = MaterialTheme.colorScheme.error)
                        } else if (estimatedCalories != null && calories.isEmpty()) {
                            Text("Estimasi: ~$estimatedCalories kal", color = Color.Gray)
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

                    // Validate activity type
                    if (activityType.isEmpty()) {
                        activityError = "Pilih jenis aktivitas"
                        hasError = true
                    }

                    // Validate duration
                    val dur = duration.toIntOrNull()
                    if (duration.isEmpty()) {
                        durationError = "Durasi harus diisi"
                        hasError = true
                    } else if (dur == null) {
                        durationError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (dur <= 0) {
                        durationError = "Durasi harus lebih dari 0"
                        hasError = true
                    } else if (dur > 1440) {
                        durationError = "Durasi maksimal 1440 menit (24 jam)"
                        hasError = true
                    }

                    // Validate steps if provided
                    val stp = if (steps.isNotEmpty()) {
                        val stpValue = steps.toIntOrNull()
                        if (stpValue == null) {
                            stepsError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (stpValue < 0) {
                            stepsError = "Nilai tidak boleh negatif"
                            hasError = true
                            null
                        } else {
                            stpValue
                        }
                    } else null

                    // Validate calories if provided, otherwise use estimated
                    val cal = if (calories.isNotEmpty()) {
                        val calValue = calories.toIntOrNull()
                        if (calValue == null) {
                            caloriesError = "Masukkan angka yang valid"
                            hasError = true
                            null
                        } else if (calValue < 0) {
                            caloriesError = "Nilai tidak boleh negatif"
                            hasError = true
                            null
                        } else {
                            calValue
                        }
                    } else estimatedCalories

                    if (!hasError && dur != null) {
                        // Show encouragement message
                        val message = when {
                            dur >= 60 -> "🎉 Luar biasa! Aktivitas ${dur} menit sangat baik untuk kesehatan!"
                            dur >= 30 -> "👍 Bagus! Pertahankan aktivitas fisik rutin Anda!"
                            else -> "Aktivitas tersimpan. Usahakan minimal 30 menit untuk hasil optimal!"
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onSave(activityType, dur, stp, cal)
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
