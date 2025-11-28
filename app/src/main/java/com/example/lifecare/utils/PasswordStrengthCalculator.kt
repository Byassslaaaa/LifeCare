package com.example.lifecare.utils

import androidx.compose.ui.graphics.Color

/**
 * Password strength levels
 */
enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}

/**
 * PasswordStrengthCalculator - Calculate and display password strength
 */
object PasswordStrengthCalculator {

    /**
     * Calculate password strength based on length and character variety
     *
     * Scoring:
     * - Length: 8+ chars = 1pt, 12+ chars = 2pts
     * - Lowercase = 1pt
     * - Uppercase = 1pt
     * - Digits = 1pt
     * - Special chars = 1pt
     *
     * Total >= 5 = STRONG
     * Total >= 3 = MEDIUM
     * Total < 3 = WEAK
     */
    fun calculate(password: String): PasswordStrength {
        if (password.length < 6) return PasswordStrength.WEAK

        var strength = 0

        // Length score
        when {
            password.length >= 12 -> strength += 2
            password.length >= 8 -> strength += 1
        }

        // Character variety
        if (password.any { it.isLowerCase() }) strength += 1
        if (password.any { it.isUpperCase() }) strength += 1
        if (password.any { it.isDigit() }) strength += 1
        if (password.any { !it.isLetterOrDigit() }) strength += 1

        return when {
            strength >= 5 -> PasswordStrength.STRONG
            strength >= 3 -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }

    /**
     * Get localized label for strength
     */
    fun getLabel(strength: PasswordStrength): String = when (strength) {
        PasswordStrength.WEAK -> "Lemah"
        PasswordStrength.MEDIUM -> "Sedang"
        PasswordStrength.STRONG -> "Kuat"
    }

    /**
     * Get color for strength indicator
     */
    fun getColor(strength: PasswordStrength): Color = when (strength) {
        PasswordStrength.WEAK -> Color(0xFFFF5252)    // Red
        PasswordStrength.MEDIUM -> Color(0xFFFFA726)  // Orange
        PasswordStrength.STRONG -> Color(0xFF66BB6A)  // Green
    }
}
