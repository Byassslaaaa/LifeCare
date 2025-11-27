package com.example.lifecare.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Dark color scheme using HealthColors
 * Currently using the same colors as light mode with some adjustments
 * Can be customized for true dark mode in the future
 */
private val DarkColorScheme = darkColorScheme(
    primary = HealthColors.Primary,
    secondary = HealthColors.Secondary,
    tertiary = HealthColors.BloodPressure,
    background = Color(0xFF1C1C1E),
    surface = Color(0xFF2C2C2E),
    onPrimary = HealthColors.TextOnPrimary,
    onSecondary = HealthColors.TextOnPrimary,
    onBackground = Color(0xFFE5E5EA),
    onSurface = Color(0xFFE5E5EA)
)

/**
 * Light color scheme using HealthColors
 * This is the primary theme for the LifeCare application
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = HealthColors.Primary,
    onPrimary = HealthColors.TextOnPrimary,
    primaryContainer = HealthColors.PrimaryVariant,
    onPrimaryContainer = HealthColors.TextOnPrimary,

    // Secondary colors
    secondary = HealthColors.Secondary,
    onSecondary = HealthColors.TextOnPrimary,
    secondaryContainer = HealthColors.SecondaryVariant,
    onSecondaryContainer = HealthColors.TextOnPrimary,

    // Tertiary colors
    tertiary = HealthColors.BloodPressure,
    onTertiary = HealthColors.TextOnPrimary,

    // Background & Surface
    background = HealthColors.Background,
    onBackground = HealthColors.TextPrimary,
    surface = HealthColors.Surface,
    onSurface = HealthColors.TextPrimary,
    surfaceVariant = HealthColors.SurfaceVariant,
    onSurfaceVariant = HealthColors.TextSecondary,

    // Outline & Border
    outline = HealthColors.Border,
    outlineVariant = HealthColors.Divider,

    // Error
    error = HealthColors.Error,
    onError = Color.White,
    errorContainer = HealthColors.ErrorLight,
    onErrorContainer = HealthColors.Error
)

/**
 * LifeCare Theme
 *
 * Main theme composable for the LifeCare application
 * Uses the custom HealthColors design system
 *
 * @param darkTheme Whether to use dark theme (currently defaults to system setting)
 * @param dynamicColor Whether to use Android 12+ dynamic colors (disabled by default for consistent branding)
 * @param content The content to be themed
 */
@Composable
fun LifeCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled dynamic color for consistent LifeCare branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
