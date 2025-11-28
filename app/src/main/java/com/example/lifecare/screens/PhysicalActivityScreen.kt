package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.data.PhysicalActivity
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.components.*
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing
import com.example.lifecare.ui.theme.HealthTypography
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhysicalActivityScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onStartGPSTracking: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var activityList by remember { mutableStateOf(healthDataManager.getPhysicalActivityList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aktivitas Fisik", style = HealthTypography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HealthColors.Activity,
                    titleContentColor = HealthColors.TextOnPrimary,
                    navigationIconContentColor = HealthColors.TextOnPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = HealthColors.Activity,
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
            // Summary Card
            FeaturedCard(
                modifier = Modifier.fillMaxWidth().padding(HealthSpacing.screenPadding),
                backgroundColor = HealthColors.Activity,
                contentColor = HealthColors.TextOnPrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(HealthSpacing.iconSizeLarge), tint = HealthColors.TextOnPrimary)
                    Spacer(modifier = Modifier.width(HealthSpacing.medium))
                    Column {
                        Text("Hari Ini", style = HealthTypography.bodySmall, color = HealthColors.TextOnPrimary.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(HealthSpacing.xxSmall))
                        Text("${healthDataManager.getTodayTotalSteps()} langkah", style = HealthTypography.displaySmall, fontWeight = FontWeight.Bold, color = HealthColors.TextOnPrimary)
                        Text("${healthDataManager.getTodayTotalExerciseMinutes()} menit olahraga", style = HealthTypography.bodySmall, color = HealthColors.TextOnPrimary.copy(alpha = 0.8f))
                    }
                }
            }

            // GPS Tracking Button
            Button(
                onClick = onStartGPSTracking,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HealthSpacing.screenPadding)
                    .padding(top = HealthSpacing.medium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HealthColors.Activity
                )
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = null,
                    modifier = Modifier.size(HealthSpacing.iconSizeMedium)
                )
                Spacer(modifier = Modifier.width(HealthSpacing.small))
                Text("Track dengan GPS", style = HealthTypography.titleMedium)
            }

            Text("Riwayat", style = HealthTypography.headlineSmall, modifier = Modifier.padding(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = HealthSpacing.screenPadding, vertical = HealthSpacing.small)
            ) {
                items(activityList) { activity ->
                    PhysicalActivityHistoryItem(activity)
                    Spacer(modifier = Modifier.height(HealthSpacing.small))
                }
            }
        }

        if (showDialog) {
            AddPhysicalActivityDialog(
                onDismiss = { showDialog = false },
                onSave = { type, duration, steps, calories ->
                    val activity = PhysicalActivity(activityType = type, duration = duration, steps = steps, caloriesBurned = calories)
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

    HealthCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(activity.activityType, style = HealthTypography.titleMedium, fontWeight = FontWeight.Bold, color = HealthColors.Activity)
                Text(dateFormat.format(Date(activity.timestamp)), style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
            }
            Text("${activity.duration} min", style = HealthTypography.headlineSmall, fontWeight = FontWeight.Bold, color = HealthColors.Activity)
        }

        if (activity.steps != null || activity.caloriesBurned != null) {
            Spacer(modifier = Modifier.height(HealthSpacing.small))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(HealthSpacing.medium)) {
                if (activity.steps != null) {
                    Text("${activity.steps} langkah", style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
                }
                if (activity.caloriesBurned != null) {
                    Text("${activity.caloriesBurned} kal", style = HealthTypography.bodySmall, color = HealthColors.TextSecondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhysicalActivityDialog(onDismiss: () -> Unit, onSave: (String, Int, Int?, Int?) -> Unit) {
    var activityType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var activityError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val activityTypes = listOf("Jalan Kaki", "Lari", "Bersepeda", "Berenang", "Yoga", "Gym", "Aerobik", "Basket", "Sepak Bola", "Lainnya")

    val estimatedCalories = remember(activityType, duration) {
        val dur = duration.toIntOrNull()
        if (dur != null && activityType.isNotEmpty() && calories.isEmpty()) {
            val caloriesPerMinute = when (activityType) {
                "Jalan Kaki" -> 4; "Lari" -> 10; "Bersepeda" -> 7; "Berenang" -> 8
                "Yoga" -> 3; "Gym" -> 6; "Aerobik" -> 7; "Basket" -> 8; "Sepak Bola" -> 9
                else -> 5
            }
            dur * caloriesPerMinute
        } else null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Aktivitas Fisik", style = HealthTypography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(HealthSpacing.small)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = activityType, onValueChange = {}, label = { Text("Jenis Aktivitas") }, readOnly = true,
                        isError = activityError != null,
                        supportingText = { if (activityError != null) Text(activityError!!, color = HealthColors.Error) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Activity, focusedLabelColor = HealthColors.Activity)
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        activityTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { activityType = type; activityError = null; expanded = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 4)) { duration = it; durationError = null } },
                    label = { Text("Durasi (menit)") }, placeholder = { Text("Contoh: 30") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                    isError = durationError != null,
                    supportingText = { if (durationError != null) Text(durationError!!, color = HealthColors.Error) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Activity, focusedLabelColor = HealthColors.Activity)
                )

                OutlinedTextField(
                    value = steps,
                    onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 6)) steps = it },
                    label = { Text("Langkah (opsional)") }, placeholder = { Text("Jumlah langkah") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Activity, focusedLabelColor = HealthColors.Activity)
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { if (it.isEmpty() || (it.all { c -> c.isDigit() } && it.length <= 5)) calories = it },
                    label = { Text("Kalori Terbakar (opsional)") },
                    placeholder = { if (estimatedCalories != null) Text("Estimasi: ~$estimatedCalories kal") else Text("Jumlah kalori") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true,
                    supportingText = { if (estimatedCalories != null && calories.isEmpty()) Text("Estimasi: ~$estimatedCalories kal", color = HealthColors.TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HealthColors.Activity, focusedLabelColor = HealthColors.Activity)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false
                    if (activityType.isEmpty()) { activityError = "Pilih jenis aktivitas"; hasError = true }

                    val dur = duration.toIntOrNull()
                    if (duration.isEmpty()) { durationError = "Durasi harus diisi"; hasError = true }
                    else if (dur == null) { durationError = "Masukkan angka yang valid"; hasError = true }
                    else if (dur <= 0) { durationError = "Durasi harus lebih dari 0"; hasError = true }
                    else if (dur > 1440) { durationError = "Durasi maksimal 1440 menit"; hasError = true }

                    val stp = if (steps.isNotEmpty()) steps.toIntOrNull() else null
                    val cal = if (calories.isNotEmpty()) calories.toIntOrNull() else estimatedCalories

                    if (!hasError && dur != null) {
                        val message = when {
                            dur >= 60 -> "Luar biasa! Aktivitas $dur menit sangat baik!"
                            dur >= 30 -> "Bagus! Pertahankan aktivitas rutin!"
                            else -> "Tersimpan. Usahakan minimal 30 menit!"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        onSave(activityType, dur, stp, cal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HealthColors.Activity)
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = HealthColors.TextSecondary) } }
    )
}
