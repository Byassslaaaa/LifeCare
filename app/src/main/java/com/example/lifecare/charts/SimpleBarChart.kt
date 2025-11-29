package com.example.lifecare.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Simple Bar Chart - Simplified version without text rendering issues
 */
@Composable
fun SimpleBarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val padding = 20f

            // Calculate dimensions
            val availableWidth = canvasWidth - 2 * padding
            val barSpacing = 8f
            val totalSpacing = barSpacing * (data.size + 1)
            val barWidth = ((availableWidth - totalSpacing) / data.size).coerceAtLeast(10f)

            // Calculate min/max values
            val minValue = 0f
            val maxValue = data.maxOf { it.value }
            val valueRange = maxValue - minValue

            if (valueRange == 0f) return@Canvas

            // Draw bars
            data.forEachIndexed { index, point ->
                val normalizedValue = (point.value - minValue) / valueRange
                val barHeight = (canvasHeight - 2 * padding) * normalizedValue
                val x = padding + barSpacing + (barWidth + barSpacing) * index
                val y = canvasHeight - padding - barHeight

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        // Show max value as text below chart
        Text(
            "Max: ${String.format("%.0f", data.maxOf { it.value })} kal",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
