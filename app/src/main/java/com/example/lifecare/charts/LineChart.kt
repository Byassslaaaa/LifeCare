package com.example.lifecare.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Custom LineChart Composable
 * Pure Compose implementation without external libraries
 */
@Composable
fun LineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(title = "Chart"),
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = Color.Gray.copy(alpha = 0.3f),
    pointColor: Color = MaterialTheme.colorScheme.primary
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

    val textMeasurer = rememberTextMeasurer()

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
            val minValue = config.minValue ?: data.minOf { it.value }
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
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
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

            // Calculate points for line
            val points = data.mapIndexed { index, point ->
                val x = padding + (canvasWidth - 2 * padding) * index / (data.size - 1).coerceAtLeast(1)
                val normalizedValue = if (valueRange > 0) {
                    (point.value - minValue) / valueRange
                } else 0.5f
                val y = canvasHeight - padding - (canvasHeight - 2 * padding) * normalizedValue
                Offset(x, y)
            }

            // Draw line path
            if (points.size > 1) {
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 3f)
                )
            }

            // Draw points if enabled
            if (config.showPoints) {
                points.forEach { point ->
                    drawCircle(
                        color = pointColor,
                        radius = 5f,
                        center = point
                    )
                    // White center
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = point
                    )
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

            // Draw X-axis labels (show max 5 labels)
            val labelStep = (data.size / 5).coerceAtLeast(1)
            data.forEachIndexed { index, point ->
                if (index % labelStep == 0 || index == data.size - 1) {
                    val x = padding + (canvasWidth - 2 * padding) * index / (data.size - 1).coerceAtLeast(1)

                    if (point.label.isNotEmpty()) {
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                point.label,
                                x,
                                canvasHeight - padding + 30f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 22f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }
            }
        }

        // Legend (if needed)
        if (config.yAxisLabel.isNotEmpty()) {
            Text(
                text = config.yAxisLabel,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
