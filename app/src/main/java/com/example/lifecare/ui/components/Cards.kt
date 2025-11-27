package com.example.lifecare.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lifecare.ui.theme.HealthColors
import com.example.lifecare.ui.theme.HealthSpacing

/**
 * HealthCard - Standard elevated card for general content
 * Use this for most card needs in the app
 */
@Composable
fun HealthCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = HealthColors.Surface,
    elevation: androidx.compose.ui.unit.Dp = HealthSpacing.elevationSmall,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(HealthSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(HealthSpacing.small),
            content = content
        )
    }
}

/**
 * FeaturedCard - Highlighted card for important content
 * Use for hero sections, today's summary, or primary actions
 */
@Composable
fun FeaturedCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = HealthColors.Primary,
    contentColor: Color = HealthColors.TextOnPrimary,
    elevation: androidx.compose.ui.unit.Dp = HealthSpacing.elevationMedium,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusLarge),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(HealthSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(HealthSpacing.small),
            content = content
        )
    }
}

/**
 * OutlineCard - Subtle card with border, no elevation
 * Use for secondary content or less important information
 */
@Composable
fun OutlineCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = HealthColors.Surface,
    borderColor: Color = HealthColors.Border,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium),
        border = BorderStroke(1.dp, borderColor),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(HealthSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(HealthSpacing.small),
            content = content
        )
    }
}

/**
 * HealthDataCard - Type-specific colored card for health metrics
 * Automatically applies the correct color based on data type
 */
@Composable
fun HealthDataCard(
    modifier: Modifier = Modifier,
    dataType: HealthDataType,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val (backgroundColor, accentColor) = when (dataType) {
        HealthDataType.BLOOD_PRESSURE -> Pair(HealthColors.BloodPressureLight, HealthColors.BloodPressure)
        HealthDataType.BLOOD_SUGAR -> Pair(HealthColors.BloodSugarLight, HealthColors.BloodSugar)
        HealthDataType.BODY_METRICS -> Pair(HealthColors.BodyMetricsLight, HealthColors.BodyMetrics)
        HealthDataType.ACTIVITY -> Pair(HealthColors.ActivityLight, HealthColors.Activity)
        HealthDataType.FOOD -> Pair(HealthColors.FoodLight, HealthColors.Food)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = HealthSpacing.elevationSmall),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusMedium),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(HealthSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(HealthSpacing.small),
            content = content
        )
    }
}

/**
 * CompactCard - Smaller card for list items or compact displays
 * Use in lists or when you need to show multiple items in limited space
 */
@Composable
fun CompactCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = HealthColors.Surface,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = HealthSpacing.elevationSmall),
        shape = RoundedCornerShape(HealthSpacing.cornerRadiusSmall),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(HealthSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(HealthSpacing.small),
            content = content
        )
    }
}

/**
 * Enum for health data types used by HealthDataCard
 */
enum class HealthDataType {
    BLOOD_PRESSURE,
    BLOOD_SUGAR,
    BODY_METRICS,
    ACTIVITY,
    FOOD
}
