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
import androidx.compose.material3.MaterialTheme
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
                .background(MaterialTheme.colorScheme.background)
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
                            color = MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${sortedRecords.size} records",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface
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
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    if (showDetailDialog) {
        BodyMetricsDetailDialog(data = data, onDismiss = { showDetailDialog = false })
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
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${data.systolic}/${data.diastolic} mmHg", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63))
                if (data.heartRate != null) {
                    Text("Detak: ${data.heartRate} BPM", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    if (showDetailDialog) {
        BloodPressureDetailDialog(data = data, onDismiss = { showDetailDialog = false })
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
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${data.level} mg/dL", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9C27B0))
                Text(data.measurementType, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showDetailDialog) {
        BloodSugarDetailDialog(data = data, onDismiss = { showDetailDialog = false })
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
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(dateFormat.format(Date(data.timestamp)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    if (showDetailDialog) {
        ActivityDetailDialog(data = data, onDismiss = { showDetailDialog = false })
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
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDetailDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                Text(data.foodName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("${data.mealType} • ${dateFormat.format(Date(data.timestamp))}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    if (showDetailDialog) {
        FoodDetailDialog(data = data, onDismiss = { showDetailDialog = false })
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            color = MaterialTheme.colorScheme.onSurface
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

// Detail Dialog Components
@Composable
fun BodyMetricsDetailDialog(data: com.example.lifecare.data.BodyMetrics, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.MonitorWeight,
                contentDescription = null,
                tint = Color(0xFF60A5FA),
                modifier = Modifier.size(40.dp)
            )
        },
        title = { Text("Detail Berat & Tinggi Badan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timestamp
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(Date(data.timestamp)), fontSize = 13.sp, color = Color(0xFF2D3748))
                    }
                }

                // Measurements
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Berat Badan", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${data.weight} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Tinggi Badan", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${data.height} cm", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                        }
                    }
                }

                // BMI Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = getBMIColor(data.bmi).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Body Mass Index (BMI)", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(String.format("%.1f", data.bmi), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = getBMIColor(data.bmi))
                        Text(getBMICategory(data.bmi), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = getBMIColor(data.bmi))
                    }
                }

                // Health Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                data.bmi < 18.5 -> "BMI Anda tergolong kurang. Konsultasikan dengan ahli gizi untuk meningkatkan berat badan secara sehat."
                                data.bmi < 25.0 -> "BMI Anda normal. Pertahankan pola makan sehat dan olahraga teratur."
                                data.bmi < 30.0 -> "BMI Anda tergolong berlebih. Perhatikan pola makan dan tingkatkan aktivitas fisik."
                                else -> "BMI Anda tergolong obesitas. Sangat disarankan untuk berkonsultasi dengan dokter."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA))
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun BloodPressureDetailDialog(data: com.example.lifecare.data.BloodPressure, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFFF6B9D),
                modifier = Modifier.size(40.dp)
            )
        },
        title = { Text("Detail Tekanan Darah", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timestamp
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(Date(data.timestamp)), fontSize = 13.sp, color = Color(0xFF2D3748))
                    }
                }

                // Blood Pressure
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Tekanan Darah", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${data.systolic}/${data.diastolic}", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF6B9D))
                        Text("mmHg", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                // Detailed values
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Sistolik", fontSize = 11.sp, color = Color.Gray)
                            Text("${data.systolic}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                            Text("(Atas)", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Diastolik", fontSize = 11.sp, color = Color.Gray)
                            Text("${data.diastolic}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                            Text("(Bawah)", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                // Heart Rate if available
                if (data.heartRate != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Detak Jantung", fontSize = 14.sp, color = Color(0xFF2D3748))
                            Text("${data.heartRate} BPM", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                    }
                }

                // Category
                Card(
                    colors = CardDefaults.cardColors(containerColor = getBPColor(data.systolic, data.diastolic).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Kategori", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(getBPCategoryLabel(data.systolic, data.diastolic), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = getBPColor(data.systolic, data.diastolic))
                    }
                }

                // Health Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                data.systolic >= 180 || data.diastolic >= 120 -> "KRISIS! Segera hubungi dokter atau layanan darurat!"
                                data.systolic >= 140 || data.diastolic >= 90 -> "Tekanan darah tinggi. Konsultasikan dengan dokter Anda."
                                data.systolic >= 130 && data.diastolic < 80 -> "Tekanan darah meningkat. Perhatikan pola makan dan kurangi garam."
                                data.systolic < 90 || data.diastolic < 60 -> "Tekanan darah rendah. Konsultasikan jika ada gejala seperti pusing."
                                else -> "Tekanan darah normal. Pertahankan gaya hidup sehat!"
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B9D))
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun BloodSugarDetailDialog(data: com.example.lifecare.data.BloodSugar, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Bloodtype,
                contentDescription = null,
                tint = Color(0xFFB794F6),
                modifier = Modifier.size(40.dp)
            )
        },
        title = { Text("Detail Gula Darah", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timestamp
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(Date(data.timestamp)), fontSize = 13.sp, color = Color(0xFF2D3748))
                    }
                }

                // Blood Sugar Level
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Kadar Gula Darah", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${data.level}", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB794F6))
                        Text("mg/dL", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                // Measurement Type
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jenis Pengukuran", fontSize = 14.sp, color = Color.Gray)
                        Text(data.measurementType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                    }
                }

                // Category
                Card(
                    colors = CardDefaults.cardColors(containerColor = getBloodSugarColor(data.level, data.measurementType).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Kategori", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(getBloodSugarCategory(data.level, data.measurementType), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = getBloodSugarColor(data.level, data.measurementType))
                    }
                }

                // Normal Range Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("Rentang Normal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                        Spacer(modifier = Modifier.height(8.dp))
                        when (data.measurementType) {
                            "Puasa" -> {
                                Text("• Normal: < 100 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Prediabetes: 100-125 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Diabetes: ≥ 126 mg/dL", fontSize = 11.sp, color = Color.Gray)
                            }
                            "Setelah Makan" -> {
                                Text("• Normal: < 140 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Prediabetes: 140-199 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Diabetes: ≥ 200 mg/dL", fontSize = 11.sp, color = Color.Gray)
                            }
                            else -> {
                                Text("• Normal: 70-140 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Tinggi: 140-200 mg/dL", fontSize = 11.sp, color = Color.Gray)
                                Text("• Sangat Tinggi: > 200 mg/dL", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // Health Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when (data.measurementType) {
                                "Puasa" -> when {
                                    data.level >= 126 -> "Kadar gula darah puasa sangat tinggi! Konsultasi dokter segera!"
                                    data.level >= 100 -> "Prediabetes. Jaga pola makan dan tingkatkan aktivitas fisik."
                                    data.level < 70 -> "Hipoglikemia (gula darah rendah). Konsumsi makanan/minuman manis."
                                    else -> "Kadar gula darah puasa normal. Pertahankan gaya hidup sehat!"
                                }
                                "Setelah Makan" -> when {
                                    data.level >= 200 -> "Kadar gula darah sangat tinggi! Konsultasi dokter segera!"
                                    data.level >= 140 -> "Gula darah tinggi setelah makan. Perhatikan porsi dan jenis makanan."
                                    else -> "Kadar gula darah setelah makan normal."
                                }
                                else -> when {
                                    data.level >= 200 -> "Kadar gula darah sangat tinggi! Konsultasi dokter!"
                                    data.level < 70 -> "Gula darah rendah. Konsumsi makanan/minuman manis segera."
                                    else -> "Kadar gula darah dalam batas normal."
                                }
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB794F6))
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun ActivityDetailDialog(data: com.example.lifecare.data.PhysicalActivity, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.DirectionsRun,
                contentDescription = null,
                tint = Color(0xFF4ADE80),
                modifier = Modifier.size(40.dp)
            )
        },
        title = { Text("Detail Aktivitas Fisik", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timestamp
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(Date(data.timestamp)), fontSize = 13.sp, color = Color(0xFF2D3748))
                    }
                }

                // Activity Type
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Jenis Aktivitas", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(data.activityType, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                    }
                }

                // Duration
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Durasi", fontSize = 14.sp, color = Color.Gray)
                        Text("${data.duration} menit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                    }
                }

                // Steps if available
                if (data.steps != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Langkah", fontSize = 14.sp, color = Color.Gray)
                            }
                            Text("${data.steps}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                        }
                    }
                }

                // Calories if available
                if (data.caloriesBurned != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Kalori Terbakar", fontSize = 14.sp, color = Color.Gray)
                            }
                            Text("${data.caloriesBurned} kal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                    }
                }

                // Health Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                data.duration >= 60 -> "Luar biasa! Aktivitas ${data.duration} menit sangat baik untuk kesehatan jantung dan metabolisme."
                                data.duration >= 30 -> "Bagus! WHO merekomendasikan minimal 30 menit aktivitas fisik per hari. Pertahankan!"
                                else -> "Usahakan minimal 30 menit aktivitas fisik per hari untuk kesehatan optimal."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun FoodDetailDialog(data: com.example.lifecare.data.FoodIntake, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy 'pukul' HH:mm", Locale("id", "ID"))

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(40.dp)
            )
        },
        title = { Text("Detail Asupan Makanan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Timestamp
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(Date(data.timestamp)), fontSize = 13.sp, color = Color(0xFF2D3748))
                    }
                }

                // Food Name
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Nama Makanan", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(data.foodName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748), maxLines = 2)
                    }
                }

                // Meal Type and Calories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Waktu Makan", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(data.mealType, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Kalori", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${data.calories} kal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }
                    }
                }

                // Nutrients if available
                if (data.protein != null || data.carbs != null || data.fat != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text("Informasi Nutrisi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3748))
                            Spacer(modifier = Modifier.height(12.dp))

                            if (data.protein != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Protein", fontSize = 13.sp, color = Color.Gray)
                                    Text("${data.protein} g", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (data.carbs != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Karbohidrat", fontSize = 13.sp, color = Color.Gray)
                                    Text("${data.carbs} g", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (data.fat != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Lemak", fontSize = 13.sp, color = Color.Gray)
                                    Text("${data.fat} g", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
                                }
                            }
                        }
                    }
                }

                // Health Info
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                data.calories > 800 -> "Makanan tinggi kalori (${data.calories} kal). Pastikan Anda mengimbanginya dengan aktivitas fisik yang cukup."
                                data.calories > 500 -> "Asupan ${data.calories} kalori cukup signifikan. Jaga keseimbangan nutrisi harian Anda."
                                data.calories > 300 -> "Asupan kalori moderat (${data.calories} kal). Bagus untuk menjaga energi."
                                else -> "Asupan kalori ringan (${data.calories} kal). Cocok untuk snack atau makanan ringan."
                            },
                            fontSize = 12.sp,
                            color = Color(0xFF2D3748),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
            ) {
                Text("Tutup")
            }
        }
    )
}

// Helper function for BP category label
fun getBPCategoryLabel(systolic: Int, diastolic: Int): String {
    return when {
        systolic < 120 && diastolic < 80 -> "Normal"
        systolic < 130 && diastolic < 80 -> "Meningkat"
        systolic < 140 || diastolic < 90 -> "Hipertensi Tahap 1"
        systolic < 180 || diastolic < 120 -> "Hipertensi Tahap 2"
        else -> "Krisis Hipertensi"
    }
}
