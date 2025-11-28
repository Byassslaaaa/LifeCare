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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Custom BarChart Composable
 */
@Composable
fun BarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(title = "Bar Chart"),
    barColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = Color.Gray.copy(alpha = 0.3f)
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(200.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("Tidak ada data untuk ditampilkan", color = Color.Gray)
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Chart Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val padding = 40f

            // Calculate min/max values
            val minValue = config.minValue ?: 0f
            val maxValue = config.maxValue ?: data.maxOf { it.value }
            val valueRange = maxValue - minValue

            // Draw grid if enabled
            if (config.showGrid) {
                val gridLines = 5
                for (i in 0..gridLines) {
                    val y = padding + (canvasHeight - 2 * padding) * i / gridLines
                    drawLine(
                        color = gridColor,
                        start = Offset(padding, y),
                        end = Offset(canvasWidth - padding, y),
                        strokeWidth = 1f
                    )
                }
            }

            // Draw axes
            // Y-axis
            drawLine(
                color = Color.Black,
                start = Offset(padding, padding),
                end = Offset(padding, canvasHeight - padding),
                strokeWidth = 2f
            )
            // X-axis
            drawLine(
                color = Color.Black,
                start = Offset(padding, canvasHeight - padding),
                end = Offset(canvasWidth - padding, canvasHeight - padding),
                strokeWidth = 2f
            )

            // Calculate bar width and spacing
            val availableWidth = canvasWidth - 2 * padding
            val barSpacing = 8f
            val totalSpacing = barSpacing * (data.size + 1)
            val barWidth = (availableWidth - totalSpacing) / data.size

            // Draw bars
            data.forEachIndexed { index, point ->
                val normalizedValue = if (valueRange > 0) {
                    (point.value - minValue) / valueRange
                } else 0f

                val barHeight = (canvasHeight - 2 * padding) * normalizedValue
                val x = padding + barSpacing + (barWidth + barSpacing) * index
                val y = canvasHeight - padding - barHeight

                // Draw bar with rounded corners
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )

                // Draw value on top of bar
                val valueText = when {
                    point.value >= 1000 -> "${(point.value / 1000).roundToInt()}k"
                    point.value >= 100 -> point.value.roundToInt().toString()
                    else -> "%.1f".format(point.value)
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        valueText,
                        x + barWidth / 2,
                        y - 10f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 22f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                // Draw label below bar
                if (point.label.isNotEmpty()) {
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            point.label,
                            x + barWidth / 2,
                            canvasHeight - padding + 30f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.GRAY
                                textSize = 20f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }
            }

            // Draw Y-axis labels
            val yLabels = 5
            for (i in 0..yLabels) {
                val value = minValue + (maxValue - minValue) * i / yLabels
                val y = canvasHeight - padding - (canvasHeight - 2 * padding) * i / yLabels

                val text = when {
                    value >= 1000 -> "${(value / 1000).roundToInt()}k"
                    value >= 100 -> value.roundToInt().toString()
                    else -> "%.1f".format(value)
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        text,
                        padding - 10f,
                        y + 5f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.RIGHT
                        }
                    )
                }
            }
        }

        // Y-axis label
        if (config.yAxisLabel.isNotEmpty()) {
            Text(
                text = config.yAxisLabel,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
