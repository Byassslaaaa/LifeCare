package com.example.lifecare.data

import android.content.Context
import android.content.SharedPreferences

/**
 * ThemeManager - Manages theme preferences (Dark/Light/System mode)
 */
class ThemeManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "theme_preferences",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    /**
     * Get current theme mode
     */
    fun getThemeMode(): String {
        return sharedPreferences.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
    }

    /**
     * Set theme mode
     */
    fun setThemeMode(mode: String) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    /**
     * Check if theme is set to system/auto
     */
    fun isSystemTheme(): Boolean {
        return getThemeMode() == THEME_SYSTEM
    }

    /**
     * Legacy: Check if dark mode is enabled (for backward compatibility)
     * Returns true if theme is DARK or if SYSTEM with dark mode active
     */
    fun isDarkModeEnabled(isSystemInDarkMode: Boolean = false): Boolean {
        return when (getThemeMode()) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            THEME_SYSTEM -> isSystemInDarkMode
            else -> isSystemInDarkMode
        }
    }

    /**
     * Cycle through theme modes: System -> Light -> Dark -> System
     */
    fun cycleThemeMode(): String {
        val newMode = when (getThemeMode()) {
            THEME_SYSTEM -> THEME_LIGHT
            THEME_LIGHT -> THEME_DARK
            THEME_DARK -> THEME_SYSTEM
            else -> THEME_SYSTEM
        }
        setThemeMode(newMode)
        return newMode
    }

    /**
     * Get theme display name
     */
    fun getThemeDisplayName(): String {
        return when (getThemeMode()) {
            THEME_LIGHT -> "Terang"
            THEME_DARK -> "Gelap"
            THEME_SYSTEM -> "Ikuti Sistem"
            else -> "Ikuti Sistem"
        }
    }
}
