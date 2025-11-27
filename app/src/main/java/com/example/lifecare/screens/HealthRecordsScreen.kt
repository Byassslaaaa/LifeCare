package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import java.text.SimpleDateFormat
import java.util.*

enum class DateFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH
}

enum class SortOrder {
    DATE_DESC, DATE_ASC, VALUE_DESC, VALUE_ASC
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordsScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    var dateFilter by remember { mutableStateOf(DateFilter.ALL) }
    var sortOrder by remember { mutableStateOf(SortOrder.DATE_DESC) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Kesehatan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                    }
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", tint = Color.White)
                    }
                    var showExportDialog by remember { mutableStateOf(false) }
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Share, contentDescription = "Export", tint = Color.White)
                    }

                    if (showExportDialog) {
                        ExportDialog(
                            healthDataManager = healthDataManager,
                            onDismiss = { showExportDialog = false }
                        )
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
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Filter & Sort Info
            if (dateFilter != DateFilter.ALL || sortOrder != SortOrder.DATE_DESC) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filter: ${getFilterLabel(dateFilter)} • Sort: ${getSortLabel(sortOrder)}",
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748)
                        )
                        TextButton(
                            onClick = {
                                dateFilter = DateFilter.ALL
                                sortOrder = SortOrder.DATE_DESC
                            }
                        ) {
                            Text("Reset", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Unified Timeline - All Records
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    AllRecordsSection(healthDataManager, dateFilter, sortOrder)
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Filter Dialog
        if (showFilterDialog) {
            FilterDialog(
                currentFilter = dateFilter,
                onDismiss = { showFilterDialog = false },
                onFilterSelected = { filter ->
                    dateFilter = filter
                    showFilterDialog = false
                }
            )
        }

        // Sort Dialog
        if (showSortDialog) {
            SortDialog(
                currentSort = sortOrder,
                onDismiss = { showSortDialog = false },
                onSortSelected = { sort ->
                    sortOrder = sort
                    showSortDialog = false
                }
            )
        }
    }
}

@Composable
fun AllRecordsSection(
    healthDataManager: HealthDataManager,
    dateFilter: DateFilter,
    sortOrder: SortOrder
) {
    // Collect all records with timestamp
    data class HealthRecord(
        val timestamp: Long,
        val type: String,
        val content: @Composable () -> Unit
    )

    val allRecords = mutableListOf<HealthRecord>()

    // Add Body Metrics
    filterAndSortBodyMetrics(healthDataManager.getBodyMetricsList(), dateFilter, sortOrder).forEach { data ->
        allRecords.add(HealthRecord(
            timestamp = data.timestamp,
            type = "body_metrics",
            content = { BodyMetricsCard(data, healthDataManager) {} }
        ))
    }

    // Add Blood Pressure
    filterAndSortBloodPressure(healthDataManager.getBloodPressureList(), dateFilter, sortOrder).forEach { data ->
        allRecords.add(HealthRecord(
            timestamp = data.timestamp,
            type = "blood_pressure",
            content = { BloodPressureCard(data, healthDataManager) {} }
        ))
    }

    // Add Blood Sugar
    filterAndSortBloodSugar(healthDataManager.getBloodSugarList(), dateFilter, sortOrder).forEach { data ->
        allRecords.add(HealthRecord(
            timestamp = data.timestamp,
            type = "blood_sugar",
            content = { BloodSugarCard(data, healthDataManager) {} }
        ))
    }

    // Add Physical Activity
    filterAndSortActivity(healthDataManager.getPhysicalActivityList(), dateFilter, sortOrder).forEach { data ->
        allRecords.add(HealthRecord(
            timestamp = data.timestamp,
            type = "activity",
            content = { ActivityCard(data, healthDataManager) {} }
        ))
    }

    // Add Food Intake
    filterAndSortFood(healthDataManager.getFoodIntakeList(), dateFilter, sortOrder).forEach { data ->
        allRecords.add(HealthRecord(
            timestamp = data.timestamp,
            type = "food",
            content = { FoodCard(data, healthDataManager) {} }
        ))
    }

    // Sort all records by timestamp (newest first by default)
    val sortedRecords = when (sortOrder) {
        SortOrder.DATE_DESC -> allRecords.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> allRecords.sortedBy { it.timestamp }
        else -> allRecords.sortedByDescending { it.timestamp }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Summary Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF5DCCB4)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Total Riwayat Kesehatan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${healthDataManager.getBodyMetricsList().size + healthDataManager.getBloodPressureList().size + healthDataManager.getBloodSugarList().size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Data Kesehatan",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${healthDataManager.getPhysicalActivityList().size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Aktivitas",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${healthDataManager.getFoodIntakeList().size}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Makanan",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Timeline Header
        if (sortedRecords.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Timeline Riwayat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )
                Text(
                    "${sortedRecords.size} records",
                    fontSize = 14.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }

        // All Records Timeline
        if (sortedRecords.isEmpty()) {
            EmptyState("Belum ada riwayat kesehatan")
        } else {
            sortedRecords.forEach { record ->
                record.content()
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, count: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
            }
            Text(
                "$count data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun BodyMetricsCard(
    data: com.example.lifecare.data.BodyMetrics,
    healthDataManager: HealthDataManager,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Berat: ${data.weight} kg | Tinggi: ${data.height} cm", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("BMI", fontSize = 10.sp, color = Color.Gray)
                    Text(String.format("%.1f", data.bmi), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = getBMIColor(data.bmi))
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Data Berat & Tinggi",
            message = "Apakah Anda yakin ingin menghapus data ini?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                // Delete logic would go here if HealthDataManager had a delete method
                Toast.makeText(context, "Fitur hapus akan segera tersedia", Toast.LENGTH_SHORT).show()
                showDeleteDialog = false
                onDelete()
            }
        )
    }
}

@Composable
fun BloodPressureCard(
    data: com.example.lifecare.data.BloodPressure,
    healthDataManager: HealthDataManager,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${data.systolic}/${data.diastolic} mmHg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                if (data.heartRate != null) {
                    Text("Detak: ${data.heartRate} BPM", fontSize = 12.sp, color = Color.Gray)
                }
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Data Tekanan Darah",
            message = "Apakah Anda yakin ingin menghapus data ini?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                Toast.makeText(context, "Fitur hapus akan segera tersedia", Toast.LENGTH_SHORT).show()
                showDeleteDialog = false
                onDelete()
            }
        )
    }
}

@Composable
fun BloodSugarCard(
    data: com.example.lifecare.data.BloodSugar,
    healthDataManager: HealthDataManager,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${data.level} mg/dL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9C27B0))
                Text(data.measurementType, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Data Gula Darah",
            message = "Apakah Anda yakin ingin menghapus data ini?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                Toast.makeText(context, "Fitur hapus akan segera tersedia", Toast.LENGTH_SHORT).show()
                showDeleteDialog = false
                onDelete()
            }
        )
    }
}

@Composable
fun ActivityCard(
    data: com.example.lifecare.data.PhysicalActivity,
    healthDataManager: HealthDataManager,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(data.activityType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = Color.Gray)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${data.duration} min", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Data Aktivitas Fisik",
            message = "Apakah Anda yakin ingin menghapus data ini?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                Toast.makeText(context, "Fitur hapus akan segera tersedia", Toast.LENGTH_SHORT).show()
                showDeleteDialog = false
                onDelete()
            }
        )
    }
}

@Composable
fun FoodCard(
    data: com.example.lifecare.data.FoodIntake,
    healthDataManager: HealthDataManager,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(data.foodName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("${data.mealType} • ${dateFormat.format(Date(data.timestamp))}", fontSize = 12.sp, color = Color.Gray)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${data.calories} kal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Hapus Data Asupan Makanan",
            message = "Apakah Anda yakin ingin menghapus data ini?",
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                Toast.makeText(context, "Fitur hapus akan segera tersedia", Toast.LENGTH_SHORT).show()
                showDeleteDialog = false
                onDelete()
            }
        )
    }
}

// Helper Functions for Filtering and Sorting
fun isWithinDateFilter(timestamp: Long, filter: DateFilter): Boolean {
    val calendar = Calendar.getInstance()
    val today = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    return when (filter) {
        DateFilter.ALL -> true
        DateFilter.TODAY -> timestamp >= today
        DateFilter.THIS_WEEK -> {
            val weekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            timestamp >= weekStart
        }
        DateFilter.THIS_MONTH -> {
            val monthStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            timestamp >= monthStart
        }
    }
}

fun filterAndSortBodyMetrics(
    list: List<com.example.lifecare.data.BodyMetrics>,
    filter: DateFilter,
    sort: SortOrder
): List<com.example.lifecare.data.BodyMetrics> {
    var filtered = list.filter { isWithinDateFilter(it.timestamp, filter) }

    filtered = when (sort) {
        SortOrder.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> filtered.sortedBy { it.timestamp }
        SortOrder.VALUE_DESC -> filtered.sortedByDescending { it.bmi }
        SortOrder.VALUE_ASC -> filtered.sortedBy { it.bmi }
    }

    return filtered
}

fun filterAndSortBloodPressure(
    list: List<com.example.lifecare.data.BloodPressure>,
    filter: DateFilter,
    sort: SortOrder
): List<com.example.lifecare.data.BloodPressure> {
    var filtered = list.filter { isWithinDateFilter(it.timestamp, filter) }

    filtered = when (sort) {
        SortOrder.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> filtered.sortedBy { it.timestamp }
        SortOrder.VALUE_DESC -> filtered.sortedByDescending { it.systolic }
        SortOrder.VALUE_ASC -> filtered.sortedBy { it.systolic }
    }

    return filtered
}

fun filterAndSortBloodSugar(
    list: List<com.example.lifecare.data.BloodSugar>,
    filter: DateFilter,
    sort: SortOrder
): List<com.example.lifecare.data.BloodSugar> {
    var filtered = list.filter { isWithinDateFilter(it.timestamp, filter) }

    filtered = when (sort) {
        SortOrder.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> filtered.sortedBy { it.timestamp }
        SortOrder.VALUE_DESC -> filtered.sortedByDescending { it.level }
        SortOrder.VALUE_ASC -> filtered.sortedBy { it.level }
    }

    return filtered
}

fun filterAndSortActivity(
    list: List<com.example.lifecare.data.PhysicalActivity>,
    filter: DateFilter,
    sort: SortOrder
): List<com.example.lifecare.data.PhysicalActivity> {
    var filtered = list.filter { isWithinDateFilter(it.timestamp, filter) }

    filtered = when (sort) {
        SortOrder.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> filtered.sortedBy { it.timestamp }
        SortOrder.VALUE_DESC -> filtered.sortedByDescending { it.duration }
        SortOrder.VALUE_ASC -> filtered.sortedBy { it.duration }
    }

    return filtered
}

fun filterAndSortFood(
    list: List<com.example.lifecare.data.FoodIntake>,
    filter: DateFilter,
    sort: SortOrder
): List<com.example.lifecare.data.FoodIntake> {
    var filtered = list.filter { isWithinDateFilter(it.timestamp, filter) }

    filtered = when (sort) {
        SortOrder.DATE_DESC -> filtered.sortedByDescending { it.timestamp }
        SortOrder.DATE_ASC -> filtered.sortedBy { it.timestamp }
        SortOrder.VALUE_DESC -> filtered.sortedByDescending { it.calories }
        SortOrder.VALUE_ASC -> filtered.sortedBy { it.calories }
    }

    return filtered
}

fun getFilterLabel(filter: DateFilter): String {
    return when (filter) {
        DateFilter.ALL -> "Semua"
        DateFilter.TODAY -> "Hari Ini"
        DateFilter.THIS_WEEK -> "Minggu Ini"
        DateFilter.THIS_MONTH -> "Bulan Ini"
    }
}

fun getSortLabel(sort: SortOrder): String {
    return when (sort) {
        SortOrder.DATE_DESC -> "Terbaru"
        SortOrder.DATE_ASC -> "Terlama"
        SortOrder.VALUE_DESC -> "Nilai Tertinggi"
        SortOrder.VALUE_ASC -> "Nilai Terendah"
    }
}

// Dialog Components
@Composable
fun FilterDialog(
    currentFilter: DateFilter,
    onDismiss: () -> Unit,
    onFilterSelected: (DateFilter) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Berdasarkan Tanggal") },
        text = {
            Column {
                DateFilter.values().forEach { filter ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFilterSelected(filter) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentFilter == filter,
                            onClick = { onFilterSelected(filter) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getFilterLabel(filter))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun SortDialog(
    currentSort: SortOrder,
    onDismiss: () -> Unit,
    onSortSelected: (SortOrder) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Urutkan Data") },
        text = {
            Column {
                SortOrder.values().forEach { sort ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortSelected(sort) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSort == sort,
                            onClick = { onSortSelected(sort) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(getSortLabel(sort))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                tint = Color(0xFFF44336),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ExportDialog(
    healthDataManager: HealthDataManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("CSV") }
    val formats = listOf("CSV", "JSON", "TXT")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Share,
                contentDescription = null,
                tint = Color(0xFF5DCCB4),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Export Data Kesehatan") },
        text = {
            Column {
                Text(
                    "Pilih format export data:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                formats.forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFormat = format }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedFormat == format,
                            onClick = { selectedFormat = format }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                format,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                when (format) {
                                    "CSV" -> "Comma-Separated Values (untuk Excel)"
                                    "JSON" -> "JavaScript Object Notation"
                                    "TXT" -> "Plain Text (untuk print)"
                                    else -> ""
                                },
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5F3))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF5DCCB4),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Data akan disimpan ke folder Downloads",
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    exportHealthData(context, healthDataManager, selectedFormat)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DCCB4))
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

fun exportHealthData(
    context: android.content.Context,
    healthDataManager: HealthDataManager,
    format: String
) {
    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("id", "ID"))
        val fileName = "lifecare_export_${System.currentTimeMillis()}"

        val content = when (format) {
            "CSV" -> generateCSV(healthDataManager, dateFormat)
            "JSON" -> generateJSON(healthDataManager)
            "TXT" -> generateTXT(healthDataManager, dateFormat)
            else -> generateCSV(healthDataManager, dateFormat)
        }

        // Save to external storage (Downloads folder)
        val file = java.io.File(
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
            "$fileName.${format.lowercase()}"
        )

        file.writeText(content)

        Toast.makeText(
            context,
            "Data berhasil diexport ke ${file.absolutePath}",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Gagal export data: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun generateCSV(healthDataManager: HealthDataManager, dateFormat: SimpleDateFormat): String {
    val sb = StringBuilder()

    // Body Metrics
    sb.appendLine("BERAT & TINGGI BADAN")
    sb.appendLine("Tanggal,Berat (kg),Tinggi (cm),BMI")
    healthDataManager.getBodyMetricsList().forEach {
        sb.appendLine("${dateFormat.format(Date(it.timestamp))},${it.weight},${it.height},${String.format("%.1f", it.bmi)}")
    }
    sb.appendLine()

    // Blood Pressure
    sb.appendLine("TEKANAN DARAH")
    sb.appendLine("Tanggal,Sistolik,Diastolik,Detak Jantung")
    healthDataManager.getBloodPressureList().forEach {
        sb.appendLine("${dateFormat.format(Date(it.timestamp))},${it.systolic},${it.diastolic},${it.heartRate ?: "-"}")
    }
    sb.appendLine()

    // Blood Sugar
    sb.appendLine("GULA DARAH")
    sb.appendLine("Tanggal,Level (mg/dL),Tipe Pengukuran")
    healthDataManager.getBloodSugarList().forEach {
        sb.appendLine("${dateFormat.format(Date(it.timestamp))},${it.level},${it.measurementType}")
    }
    sb.appendLine()

    // Physical Activity
    sb.appendLine("AKTIVITAS FISIK")
    sb.appendLine("Tanggal,Jenis Aktivitas,Durasi (menit),Langkah,Kalori Terbakar")
    healthDataManager.getPhysicalActivityList().forEach {
        sb.appendLine("${dateFormat.format(Date(it.timestamp))},${it.activityType},${it.duration},${it.steps ?: "-"},${it.caloriesBurned ?: "-"}")
    }
    sb.appendLine()

    // Food Intake
    sb.appendLine("ASUPAN MAKANAN")
    sb.appendLine("Tanggal,Nama Makanan,Kalori,Waktu Makan,Protein (g),Karbo (g),Lemak (g)")
    healthDataManager.getFoodIntakeList().forEach {
        sb.appendLine("${dateFormat.format(Date(it.timestamp))},${it.foodName},${it.calories},${it.mealType},${it.protein ?: "-"},${it.carbs ?: "-"},${it.fat ?: "-"}")
    }

    return sb.toString()
}

fun generateJSON(healthDataManager: HealthDataManager): String {
    val data = mapOf(
        "exportDate" to Date().toString(),
        "bodyMetrics" to healthDataManager.getBodyMetricsList(),
        "bloodPressure" to healthDataManager.getBloodPressureList(),
        "bloodSugar" to healthDataManager.getBloodSugarList(),
        "physicalActivity" to healthDataManager.getPhysicalActivityList(),
        "foodIntake" to healthDataManager.getFoodIntakeList()
    )

    return com.google.gson.Gson().toJson(data)
}

fun generateTXT(healthDataManager: HealthDataManager, dateFormat: SimpleDateFormat): String {
    val sb = StringBuilder()

    sb.appendLine("=" .repeat(50))
    sb.appendLine("LIFECARE - LAPORAN KESEHATAN")
    sb.appendLine("Tanggal Export: ${dateFormat.format(Date())}")
    sb.appendLine("=" .repeat(50))
    sb.appendLine()

    // Body Metrics
    sb.appendLine("BERAT & TINGGI BADAN")
    sb.appendLine("-".repeat(50))
    healthDataManager.getBodyMetricsList().forEach {
        sb.appendLine("Tanggal: ${dateFormat.format(Date(it.timestamp))}")
        sb.appendLine("  Berat: ${it.weight} kg")
        sb.appendLine("  Tinggi: ${it.height} cm")
        sb.appendLine("  BMI: ${String.format("%.1f", it.bmi)}")
        sb.appendLine()
    }

    // Blood Pressure
    sb.appendLine("TEKANAN DARAH")
    sb.appendLine("-".repeat(50))
    healthDataManager.getBloodPressureList().forEach {
        sb.appendLine("Tanggal: ${dateFormat.format(Date(it.timestamp))}")
        sb.appendLine("  Tekanan: ${it.systolic}/${it.diastolic} mmHg")
        if (it.heartRate != null) {
            sb.appendLine("  Detak Jantung: ${it.heartRate} BPM")
        }
        sb.appendLine()
    }

    // Blood Sugar
    sb.appendLine("GULA DARAH")
    sb.appendLine("-".repeat(50))
    healthDataManager.getBloodSugarList().forEach {
        sb.appendLine("Tanggal: ${dateFormat.format(Date(it.timestamp))}")
        sb.appendLine("  Level: ${it.level} mg/dL")
        sb.appendLine("  Tipe: ${it.measurementType}")
        sb.appendLine()
    }

    // Physical Activity
    sb.appendLine("AKTIVITAS FISIK")
    sb.appendLine("-".repeat(50))
    healthDataManager.getPhysicalActivityList().forEach {
        sb.appendLine("Tanggal: ${dateFormat.format(Date(it.timestamp))}")
        sb.appendLine("  Aktivitas: ${it.activityType}")
        sb.appendLine("  Durasi: ${it.duration} menit")
        if (it.steps != null) {
            sb.appendLine("  Langkah: ${it.steps}")
        }
        if (it.caloriesBurned != null) {
            sb.appendLine("  Kalori Terbakar: ${it.caloriesBurned} kal")
        }
        sb.appendLine()
    }

    // Food Intake
    sb.appendLine("ASUPAN MAKANAN")
    sb.appendLine("-".repeat(50))
    healthDataManager.getFoodIntakeList().forEach {
        sb.appendLine("Tanggal: ${dateFormat.format(Date(it.timestamp))}")
        sb.appendLine("  Makanan: ${it.foodName}")
        sb.appendLine("  Kalori: ${it.calories} kal")
        sb.appendLine("  Waktu: ${it.mealType}")
        if (it.protein != null || it.carbs != null || it.fat != null) {
            sb.append("  Nutrisi: ")
            val nutrients = mutableListOf<String>()
            if (it.protein != null) nutrients.add("Protein ${it.protein}g")
            if (it.carbs != null) nutrients.add("Karbo ${it.carbs}g")
            if (it.fat != null) nutrients.add("Lemak ${it.fat}g")
            sb.appendLine(nutrients.joinToString(", "))
        }
        sb.appendLine()
    }

    sb.appendLine("=" .repeat(50))
    sb.appendLine("Akhir Laporan")

    return sb.toString()
}
