package com.example.lifecare.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * HealthSpacing - Complete spacing system for LifeCare application
 * Based on 8dp grid system for consistent layouts
 */
object HealthSpacing {
    // Base spacing units (8dp grid)
    val xxxSmall: Dp = 2.dp     // For very tight spacing (dividers, borders)
    val xxSmall: Dp = 4.dp      // For minimal spacing
    val extraSmall: Dp = 6.dp   // Extra small spacing
    val xSmall: Dp = 8.dp       // Base unit (1x)
    val small: Dp = 12.dp       // 1.5x base
    val medium: Dp = 16.dp      // 2x base (most commonly used)
    val large: Dp = 24.dp       // 3x base
    val xLarge: Dp = 32.dp      // 4x base
    val extraLarge: Dp = 40.dp  // 5x base
    val xxLarge: Dp = 48.dp     // 6x base (section spacing)
    val xxxLarge: Dp = 64.dp    // 8x base (large gaps)

    // Component-specific spacing
    val cardPadding: Dp = 16.dp             // Internal padding for cards
    val cardSpacing: Dp = 12.dp             // Spacing between cards
    val sectionSpacing: Dp = 24.dp          // Spacing between major sections
    val screenPadding: Dp = 20.dp           // Edge-to-edge screen padding
    val buttonHeight: Dp = 50.dp            // Standard button height
    val iconSize: Dp = 24.dp                // Standard icon size
    val iconSizeSmall: Dp = 20.dp           // Small icon size
    val iconSizeMedium: Dp = 28.dp          // Medium icon size
    val iconSizeLarge: Dp = 32.dp           // Large icon size

    // Layout spacing
    val minTouchTarget: Dp = 48.dp          // Minimum touch target size (accessibility)
    val topBarHeight: Dp = 64.dp            // Top app bar height
    val bottomBarHeight: Dp = 80.dp         // Bottom navigation bar height
    val fabSize: Dp = 56.dp                 // Floating action button size

    // Corner radius
    val cornerRadiusSmall: Dp = 8.dp        // Small corners (chips, small buttons)
    val cornerRadiusMedium: Dp = 12.dp      // Standard corners (cards, buttons)
    val cornerRadiusLarge: Dp = 16.dp       // Large corners (hero cards)
    val cornerRadiusXLarge: Dp = 24.dp      // Extra large corners (bottom sheets)

    // Elevation (for shadows and layering)
    val elevationNone: Dp = 0.dp            // No elevation
    val elevationSmall: Dp = 2.dp           // Subtle elevation (cards)
    val elevationMedium: Dp = 4.dp          // Standard elevation (raised cards)
    val elevationLarge: Dp = 8.dp           // High elevation (dialogs, FAB)
    val elevationXLarge: Dp = 16.dp         // Maximum elevation (modals)
}
