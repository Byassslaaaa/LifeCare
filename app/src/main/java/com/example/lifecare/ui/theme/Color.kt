package com.example.lifecare.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * HealthColors - Complete color system for LifeCare application
 * Based on professional health app design principles with softer, more accessible colors
 */
object HealthColors {
    // Primary Brand Colors
    val Primary = Color(0xFF4DB8A8)              // Soft Teal - Main brand color
    val PrimaryVariant = Color(0xFF3A9D8F)       // Darker teal for contrast
    val Secondary = Color(0xFF60A5FA)            // Soft Blue
    val SecondaryVariant = Color(0xFF4A90E2)     // Darker blue

    // Category Colors (Softer palette for better UX)
    val BloodPressure = Color(0xFFFF6B9D)        // Soft Pink
    val BloodPressureLight = Color(0xFFFFF0F5)   // Very light pink background
    val BloodSugar = Color(0xFFB794F6)           // Soft Purple
    val BloodSugarLight = Color(0xFFF3EFFF)      // Very light purple background
    val BodyMetrics = Color(0xFF60A5FA)          // Soft Blue
    val BodyMetricsLight = Color(0xFFE3F2FD)     // Very light blue background
    val Activity = Color(0xFF4ADE80)             // Fresh Green
    val ActivityLight = Color(0xFFE8F5E9)        // Very light green background
    val Food = Color(0xFFFBBF24)                 // Warm Yellow
    val FoodLight = Color(0xFFFDE7)            // Very light yellow background

    // Wireframe Design Colors - Exact colors from reference
    val NeonGreen = Color(0xFFA7E047)            // Green neon FAB - Light mode
    val NeonGreenDark = Color(0xFF7FBF2F)        // Green neon FAB - Dark mode (more neon)

    // Light Mode Colors
    val BackgroundLight = Color(0xFFFFFFFF)      // Pure white background
    val CardLight = Color(0xFFF8F8F8)            // Card background light #F8F8F8
    val CardLightAlt = Color(0xFFFAFAFA)         // Alternative card background
    val BorderLight = Color(0xFFE5E5E5)          // Border light
    val TextPrimaryLight = Color(0xFF000000)     // Black text
    val TextSecondaryLight = Color(0xFF4A4A4A)   // Gray text
    val IconLight = Color(0xFF000000)            // Black icons

    // Dark Mode Colors
    val BackgroundDark = Color(0xFF000000)       // Pure black background
    val CardDark = Color(0xFF1A1A1A)             // Card background dark #1A1A1A-#1C1C1C
    val CardDarkAlt = Color(0xFF1C1C1C)          // Alternative card background
    val BorderDark = Color(0xFF2A2A2A)           // Border dark
    val TextPrimaryDark = Color(0xFFFFFFFF)      // White text
    val TextSecondaryDark = Color(0xFFC7C7C7)    // Gray text
    val IconDark = Color(0xFFFFFFFF)             // White icons
    val DividerLight = Color(0xFFCFCFCF)         // Divider - Light mode
    val BottomBarDark = Color(0xFF151515)        // Bottom bar - Dark mode

    // Neutral Colors
    val Background = Color(0xFFF8FAFB)           // Very light gray
    val Surface = Color(0xFFFFFFFF)              // White
    val SurfaceVariant = Color(0xFFF1F3F5)       // Light gray

    // Text Colors
    val TextPrimary = Color(0xFF2D3748)          // Dark gray (excellent readability)
    val TextSecondary = Color(0xFF6C757D)        // Medium gray
    val TextTertiary = Color(0xFF9CA3AF)         // Light gray
    val TextOnPrimary = Color(0xFFFFFFFF)        // White text on primary colors

    // Status Colors
    val Success = Color(0xFF10B981)              // Green
    val SuccessLight = Color(0xFFD1FAE5)         // Light green background
    val Warning = Color(0xFFF59E0B)              // Orange
    val WarningLight = Color(0xFFFEF3C7)         // Light orange background
    val Error = Color(0xFFEF4444)                // Red
    val ErrorLight = Color(0xFFFEE2E2)           // Light red background
    val Info = Color(0xFF3B82F6)                 // Blue
    val InfoLight = Color(0xFFDBEAFE)            // Light blue background

    // UI Element Colors
    val Divider = Color(0xFFE5E7EB)              // Light gray divider
    val Border = Color(0xFFD1D5DB)               // Medium gray border
    val Shadow = Color(0x1A000000)               // 10% black for shadows
    val Overlay = Color(0x80000000)              // 50% black for overlays

    // Gradient Colors (for hero sections and highlights)
    val GradientStart = Color(0xFF4DB8A8)        // Primary teal
    val GradientEnd = Color(0xFF60A5FA)          // Secondary blue

    // Dark Mode Colors
    val DarkBackground = Color(0xFF000000)       // Pure Black
    val DarkSurface = Color(0xFF121212)          // Very dark gray
    val DarkSurfaceVariant = Color(0xFF1E1E1E)   // Dark gray
    val DarkTextPrimary = Color(0xFFE5E5E5)      // Light gray text
    val DarkTextSecondary = Color(0xFFB0B0B0)    // Medium gray text
    val DarkDivider = Color(0xFF2C2C2E)          // Dark divider
    val DarkBorder = Color(0xFF3A3A3C)           // Dark border
}

/**
 * Legacy color definitions for compatibility
 * These map to the old color names used throughout the app
 */
val Purple80 = HealthColors.Primary
val PurpleGrey80 = HealthColors.SurfaceVariant
val Pink80 = HealthColors.BloodPressure

val Purple40 = HealthColors.PrimaryVariant
val PurpleGrey40 = HealthColors.TextSecondary
val Pink40 = HealthColors.BloodPressure
