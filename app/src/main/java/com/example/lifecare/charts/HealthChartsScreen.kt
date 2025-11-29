package com.example.lifecare.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lifecare.data.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * HealthChartsScreen - Visualisasi data kesehatan
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthChartsScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(HealthChartCategory.BLOOD_PRESSURE) }
    var selectedDateRange by remember { mutableStateOf(DateRange.WEEK) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grafik Kesehatan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = HealthChartCategory.values().indexOf(selectedCategory),
                modifier = Modifier.fillMaxWidth()
            ) {
                HealthChartCategory.values().forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = { Text(category.label, maxLines = 1) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Range Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRange.values().forEach { range ->
                    FilterChip(
                        selected = selectedDateRange == range,
                        onClick = { selectedDateRange = range },
                        label = { Text(range.label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display chart based on selected category
            when (selectedCategory) {
                HealthChartCategory.BLOOD_PRESSURE -> {
                    BloodPressureChart(healthDataManager, selectedDateRange)
                }
                HealthChartCategory.BLOOD_SUGAR -> {
                    BloodSugarChart(healthDataManager, selectedDateRange)
                }
                HealthChartCategory.BMI -> {
                    BMIChart(healthDataManager, selectedDateRange)
                }
                HealthChartCategory.ACTIVITY -> {
                    ActivityChart(healthDataManager, selectedDateRange)
                }
                HealthChartCategory.FOOD -> {
                    FoodChart(healthDataManager, selectedDateRange)
                }
            }
        }
    }
}

@Composable
private fun BloodPressureChart(healthDataManager: HealthDataManager, dateRange: DateRange) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getBloodPressureList()
        filterByDateRange(allData, dateRange) { it.timestamp }
    }

    if (data.isEmpty()) {
        EmptyStateCard("Belum ada data tekanan darah")
        return
    }

    val systolicData = data.map { bp ->
        ChartDataPoint(
            timestamp = bp.timestamp,
            value = bp.systolic.toFloat(),
            label = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(bp.timestamp))
        )
    }

    val diastolicData = data.map { bp ->
        ChartDataPoint(
            timestamp = bp.timestamp,
            value = bp.diastolic.toFloat(),
            label = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(bp.timestamp))
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tekanan Darah Sistolik", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleLineChart(data = systolicData, lineColor = Color(0xFFE91E63))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tekanan Darah Diastolik", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleLineChart(data = diastolicData, lineColor = Color(0xFF2196F3))
        }
    }
}

@Composable
private fun BloodSugarChart(healthDataManager: HealthDataManager, dateRange: DateRange) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getBloodSugarList()
        filterByDateRange(allData, dateRange) { it.timestamp }
    }

    if (data.isEmpty()) {
        EmptyStateCard("Belum ada data gula darah")
        return
    }

    val chartData = data.map { bs ->
        ChartDataPoint(
            timestamp = bs.timestamp,
            value = bs.level.toFloat(),
            label = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(bs.timestamp))
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Gula Darah", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleLineChart(data = chartData, lineColor = Color(0xFF9C27B0))
        }
    }
}

@Composable
private fun BMIChart(healthDataManager: HealthDataManager, dateRange: DateRange) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getBodyMetricsList()
        filterByDateRange(allData, dateRange) { it.timestamp }
    }

    if (data.isEmpty()) {
        EmptyStateCard("Belum ada data berat & tinggi badan")
        return
    }

    val chartData = data.map { bm ->
        ChartDataPoint(
            timestamp = bm.timestamp,
            value = bm.bmi.toFloat(),
            label = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(bm.timestamp))
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Body Mass Index (BMI)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleLineChart(data = chartData, lineColor = Color(0xFF2196F3))
        }
    }
}

@Composable
private fun ActivityChart(healthDataManager: HealthDataManager, dateRange: DateRange) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getPhysicalActivityList()
        filterByDateRange(allData, dateRange) { it.timestamp }
    }

    if (data.isEmpty()) {
        EmptyStateCard("Belum ada data aktivitas fisik")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val dailyData = data
        .groupBy { dateFormat.format(Date(it.timestamp)) }
        .map { (date, activities) ->
            ChartDataPoint(
                timestamp = activities.first().timestamp,
                value = activities.sumOf { it.caloriesBurned ?: 0 }.toFloat(),
                label = date
            )
        }
        .sortedBy { it.timestamp }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Kalori Terbakar per Hari", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleBarChart(data = dailyData, barColor = Color(0xFF4CAF50))
        }
    }
}

@Composable
private fun FoodChart(healthDataManager: HealthDataManager, dateRange: DateRange) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getFoodIntakeList()
        filterByDateRange(allData, dateRange) { it.timestamp }
    }

    if (data.isEmpty()) {
        EmptyStateCard("Belum ada data asupan makanan")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val dailyData = data
        .groupBy { dateFormat.format(Date(it.timestamp)) }
        .map { (date, foods) ->
            ChartDataPoint(
                timestamp = foods.first().timestamp,
                value = foods.sumOf { it.calories }.toFloat(),
                label = date
            )
        }
        .sortedBy { it.timestamp }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Kalori Asupan per Hari", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            SimpleBarChart(data = dailyData, barColor = Color(0xFFFF9800))
        }
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun <T> filterByDateRange(
    data: List<T>,
    dateRange: DateRange,
    timestampExtractor: (T) -> Long
): List<T> {
    val cutoffTime = System.currentTimeMillis() - (dateRange.days * 24 * 60 * 60 * 1000L)
    return data
        .filter { timestampExtractor(it) >= cutoffTime }
        .sortedBy { timestampExtractor(it) }
}
