package com.example.lifecare.charts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
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
private fun BloodPressureChart(
    healthDataManager: HealthDataManager,
    dateRange: DateRange
) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getAllBloodPressure()
        filterByDateRange(allData, dateRange)
    }

    if (data.isEmpty()) {
        EmptyChartMessage("tekanan darah")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    // Systolic chart
    val systolicData = data.map { bp ->
        ChartDataPoint(
            timestamp = bp.timestamp,
            value = bp.systolic.toFloat(),
            label = dateFormat.format(Date(bp.timestamp))
        )
    }

    LineChart(
        data = systolicData,
        config = ChartConfig(
            title = "Tekanan Darah Sistolik",
            yAxisLabel = "mmHg",
            minValue = 70f,
            maxValue = 200f
        ),
        lineColor = Color(0xFFE91E63)
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Diastolic chart
    val diastolicData = data.map { bp ->
        ChartDataPoint(
            timestamp = bp.timestamp,
            value = bp.diastolic.toFloat(),
            label = dateFormat.format(Date(bp.timestamp))
        )
    }

    LineChart(
        data = diastolicData,
        config = ChartConfig(
            title = "Tekanan Darah Diastolik",
            yAxisLabel = "mmHg",
            minValue = 40f,
            maxValue = 150f
        ),
        lineColor = Color(0xFF9C27B0)
    )
}

@Composable
private fun BloodSugarChart(
    healthDataManager: HealthDataManager,
    dateRange: DateRange
) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getAllBloodSugar()
        filterByDateRange(allData, dateRange)
    }

    if (data.isEmpty()) {
        EmptyChartMessage("gula darah")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    val chartData = data.map { bs ->
        ChartDataPoint(
            timestamp = bs.timestamp,
            value = bs.level.toFloat(),
            label = dateFormat.format(Date(bs.timestamp))
        )
    }

    LineChart(
        data = chartData,
        config = ChartConfig(
            title = "Level Gula Darah",
            yAxisLabel = "mg/dL",
            minValue = 50f,
            maxValue = 300f
        ),
        lineColor = Color(0xFF9C27B0)
    )
}

@Composable
private fun BMIChart(
    healthDataManager: HealthDataManager,
    dateRange: DateRange
) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getAllBodyMetrics()
        filterByDateRange(allData, dateRange)
    }

    if (data.isEmpty()) {
        EmptyChartMessage("berat & tinggi badan")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    val chartData = data.map { bm ->
        val bmi = bm.weight / ((bm.height / 100) * (bm.height / 100))
        ChartDataPoint(
            timestamp = bm.timestamp,
            value = bmi.toFloat(),
            label = dateFormat.format(Date(bm.timestamp))
        )
    }

    LineChart(
        data = chartData,
        config = ChartConfig(
            title = "Indeks Massa Tubuh (BMI)",
            yAxisLabel = "BMI",
            minValue = 15f,
            maxValue = 35f
        ),
        lineColor = Color(0xFF2196F3)
    )
}

@Composable
private fun ActivityChart(
    healthDataManager: HealthDataManager,
    dateRange: DateRange
) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getAllPhysicalActivities()
        filterByDateRange(allData, dateRange)
    }

    if (data.isEmpty()) {
        EmptyChartMessage("aktivitas fisik")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    // Group by date and sum calories
    val caloriesByDate = data.groupBy {
        dateFormat.format(Date(it.timestamp))
    }.mapValues { entry ->
        entry.value.sumOf { it.caloriesBurned }
    }

    val chartData = caloriesByDate.entries.mapIndexed { index, entry ->
        ChartDataPoint(
            timestamp = index.toLong(),
            value = entry.value.toFloat(),
            label = entry.key
        )
    }

    BarChart(
        data = chartData,
        config = ChartConfig(
            title = "Kalori Terbakar per Hari",
            yAxisLabel = "Kalori",
            minValue = 0f
        ),
        barColor = Color(0xFF4CAF50)
    )
}

@Composable
private fun FoodChart(
    healthDataManager: HealthDataManager,
    dateRange: DateRange
) {
    val data = remember(dateRange) {
        val allData = healthDataManager.getAllFoodIntakes()
        filterByDateRange(allData, dateRange)
    }

    if (data.isEmpty()) {
        EmptyChartMessage("asupan makanan")
        return
    }

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    // Group by date and sum calories
    val caloriesByDate = data.groupBy {
        dateFormat.format(Date(it.timestamp))
    }.mapValues { entry ->
        entry.value.sumOf { it.calories }
    }

    val chartData = caloriesByDate.entries.mapIndexed { index, entry ->
        ChartDataPoint(
            timestamp = index.toLong(),
            value = entry.value.toFloat(),
            label = entry.key
        )
    }

    BarChart(
        data = chartData,
        config = ChartConfig(
            title = "Asupan Kalori per Hari",
            yAxisLabel = "Kalori",
            minValue = 0f
        ),
        barColor = Color(0xFFFF9800)
    )
}

@Composable
private fun EmptyChartMessage(category: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                "Tidak ada data $category",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Mulai catat data $category untuk melihat grafik",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// Helper function to filter data by date range
private fun <T : Any> filterByDateRange(data: List<T>, dateRange: DateRange): List<T> {
    val cutoffTime = System.currentTimeMillis() - (dateRange.days * 24 * 60 * 60 * 1000L)

    return data.filter { item ->
        val timestamp = when (item) {
            is BloodPressure -> item.timestamp
            is BloodSugar -> item.timestamp
            is BodyMetrics -> item.timestamp
            is PhysicalActivity -> item.timestamp
            is FoodIntake -> item.timestamp
            else -> 0L
        }
        timestamp >= cutoffTime
    }.sortedBy { item ->
        when (item) {
            is BloodPressure -> item.timestamp
            is BloodSugar -> item.timestamp
            is BodyMetrics -> item.timestamp
            is PhysicalActivity -> item.timestamp
            is FoodIntake -> item.timestamp
            else -> 0L
        }
    }
}
