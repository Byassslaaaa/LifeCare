package com.example.lifecare.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * App-wide constants
 */
object Constants {

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 3
    const val MIN_AGE = 13
    const val MAX_AGE = 150
    const val PIN_LENGTH = 6

    // Session
    const val SESSION_DURATION_MINUTES = 30
    const val SESSION_DURATION_MS = 30 * 60 * 1000L

    // SharedPreferences Keys
    const val PREFS_NAME = "LifeCarePrefs"
    const val KEY_USER_PIN = "user_pin"
    const val KEY_PIN_LAST_VERIFIED = "pin_last_verified"

    // Firebase
    const val FIREBASE_USERS_COLLECTION = "users"
}

/**
 * UI Colors for Authentication screens
 */
object AuthColors {
    val Primary = Color(0xFF33A1E0)
    val Secondary = Color(0xFF98CD00)
    val LoginPrimary = Color(0xFF2196F3)
    val TextPrimary = Color(0xFF2D3748)
    val TextSecondary = Color.Gray
    val ErrorRed = Color.Red
    val GoogleBlue = Color(0xFF4285F4)
    val GoogleGray = Color(0xFF757575)
    val BackgroundWhite = Color.White
    val BorderGray = Color.LightGray

    // Password Strength
    val StrengthWeak = Color(0xFFFF5252)
    val StrengthMedium = Color(0xFFFFA726)
    val StrengthStrong = Color(0xFF66BB6A)
}

/**
 * UI Dimensions
 */
object Dimensions {
    val ButtonHeight = 50.dp
    val ButtonHeightSmall = 48.dp
    val LogoSize = 180.dp
    val LogoSizeLarge = 240.dp
    val IconSize = 24.dp
    val IconSizeSmall = 20.dp

    val CornerRadius = 50.dp
    val CornerRadiusSmall = 12.dp
    val CornerRadiusTiny = 8.dp

    val SpacingTiny = 4.dp
    val SpacingSmall = 8.dp
    val SpacingMedium = 12.dp
    val SpacingLarge = 16.dp
    val SpacingXLarge = 20.dp
    val SpacingXXLarge = 24.dp
    val SpacingHuge = 32.dp
}
