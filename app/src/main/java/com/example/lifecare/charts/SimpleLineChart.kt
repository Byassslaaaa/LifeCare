package com.example.lifecare.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Simple Line Chart - Simplified version without text rendering issues
 */
@Composable
fun SimpleLineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        // Show empty state instead of nothing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Belum ada data untuk ditampilkan",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        return
    }

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)  // Increased height for better visibility
                .padding(16.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val padding = 20f

            // Calculate min/max values
            val minValue = data.minOf { it.value }
            val maxValue = data.maxOf { it.value }
            val valueRange = maxValue - minValue

            if (valueRange == 0f || data.size < 2) return@Canvas

            // Map data points to canvas coordinates
            val points = data.mapIndexed { index, point ->
                val x = padding + (canvasWidth - 2 * padding) * index / (data.size - 1)
                val normalizedValue = (point.value - minValue) / valueRange
                val y = canvasHeight - padding - (canvasHeight - 2 * padding) * normalizedValue
                Offset(x, y)
            }

            // Draw background grid (optional, for better visibility)
            val gridColor = Color.LightGray.copy(alpha = 0.3f)

            // Horizontal grid lines
            for (i in 0..4) {
                val y = padding + (canvasHeight - 2 * padding) * i / 4
                drawLine(
                    color = gridColor,
                    start = Offset(padding, y),
                    end = Offset(canvasWidth - padding, y),
                    strokeWidth = 1f
                )
            }

            // Draw line
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(path, lineColor, style = Stroke(width = 4f))

            // Draw points with white border for better visibility
            points.forEach { point ->
                // White border
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = point
                )
                // Colored center
                drawCircle(
                    color = lineColor,
                    radius = 5f,
                    center = point
                )
            }
        }

        // Show min/max values as text below chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Min: ${String.format("%.1f", data.minOf { it.value })}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Max: ${String.format("%.1f", data.maxOf { it.value })}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
