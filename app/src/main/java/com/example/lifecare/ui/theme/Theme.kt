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
 * Dark color scheme using HealthColors with pure black background
 * Optimized for AMOLED screens with true black (#000000)
 * Category colors remain vibrant for better contrast
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors - keep vibrant for contrast
    primary = HealthColors.Primary,
    onPrimary = HealthColors.TextOnPrimary,
    primaryContainer = HealthColors.PrimaryVariant,
    onPrimaryContainer = HealthColors.DarkTextPrimary,

    // Secondary colors
    secondary = HealthColors.Secondary,
    onSecondary = HealthColors.TextOnPrimary,
    secondaryContainer = HealthColors.SecondaryVariant,
    onSecondaryContainer = HealthColors.DarkTextPrimary,

    // Tertiary colors
    tertiary = HealthColors.BloodPressure,
    onTertiary = HealthColors.TextOnPrimary,

    // Background & Surface - Pure black theme with wireframe colors
    background = HealthColors.DarkBackground,        // Pure Black #000000
    onBackground = HealthColors.DarkTextPrimary,     // Light gray text
    surface = HealthColors.CardDark,                 // Card dark #1A1A1A
    onSurface = HealthColors.DarkTextPrimary,        // Light gray text
    surfaceVariant = HealthColors.DarkSurfaceVariant,// Dark gray #1E1E1E
    onSurfaceVariant = HealthColors.DarkTextSecondary,

    // Outline & Border
    outline = HealthColors.DarkBorder,
    outlineVariant = HealthColors.DarkDivider,

    // Error
    error = HealthColors.Error,
    onError = Color.White,
    errorContainer = Color(0xFF5C1919),
    onErrorContainer = Color(0xFFFFDADA)
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

    // Background & Surface with wireframe colors
    background = HealthColors.Surface,               // White #FFFFFF
    onBackground = HealthColors.TextPrimary,
    surface = HealthColors.CardLight,                // Card light #EDEDED
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
