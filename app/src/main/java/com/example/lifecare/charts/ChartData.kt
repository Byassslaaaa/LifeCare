package com.example.lifecare.charts

import java.time.LocalDateTime

/**
 * Data class untuk chart points
 */
data class ChartDataPoint(
    val timestamp: Long,
    val value: Float,
    val label: String = "",
    val category: String? = null
)

/**
 * Chart configuration
 */
data class ChartConfig(
    val title: String,
    val xAxisLabel: String = "",
    val yAxisLabel: String = "",
    val showGrid: Boolean = true,
    val showPoints: Boolean = true,
    val minValue: Float? = null,
    val maxValue: Float? = null
)

/**
 * Chart type
 */
enum class ChartType {
    LINE, BAR
}

/**
 * Date range for filtering
 */
enum class DateRange(val days: Int, val label: String) {
    WEEK(7, "7 Hari"),
    MONTH(30, "30 Hari"),
    THREE_MONTHS(90, "90 Hari")
}

/**
 * Health category for charts
 */
enum class HealthChartCategory(val label: String) {
    BLOOD_PRESSURE("Tekanan Darah"),
    BLOOD_SUGAR("Gula Darah"),
    BMI("Berat & Tinggi (BMI)"),
    ACTIVITY("Aktivitas Fisik"),
    FOOD("Asupan Makanan")
}
