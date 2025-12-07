package com.example.lifecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.lifecare.auth.AuthViewModel
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.data.PINSessionManager
import com.example.lifecare.data.ThemeManager
import com.example.lifecare.ui.theme.LifeCareTheme

/**
 * MainActivity - Entry point aplikasi dengan Firebase Authentication
 *
 * IMPROVED FLOW (No Activity Restart!):
 * 1. Check Firebase currentUser
 * 2. Check PIN session (valid 30 menit)
 * 3. Navigate dengan state management (smooth transitions)
 *
 * Authentication States:
 * - NOT_LOGGED_IN → LoginScreen / RegisterScreen
 * - LOGGED_IN_PIN_NOT_SET → PINScreen (Create Mode)
 * - LOGGED_IN_PIN_EXPIRED → PINScreen (Verify Mode)
 * - LOGGED_IN_PIN_VALID → HomeScreen
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize Managers
            val themeManager = remember { ThemeManager(this) }
            val healthDataManager = remember { HealthDataManager(this) }
            val authViewModel = remember { AuthViewModel(this) }
            val pinSessionManager = remember { PINSessionManager(this) }

            // THEME STATE - reactive dark mode state with real-time switching
            val isSystemInDarkMode = isSystemInDarkTheme()
            // Use mutableStateOf with key to force recomposition when SharedPreferences changes
            var themeMode by remember { mutableStateOf(themeManager.getThemeMode()) }

            // Key for force recomposition
            var themeUpdateTrigger by remember { mutableIntStateOf(0) }

            // Calculate isDarkMode based on current themeMode (real-time)
            // FIX: Correct logic - light means NOT dark, dark means dark
            val isDarkMode = remember(themeMode, themeUpdateTrigger) {
                when (themeManager.getThemeMode()) {  // Always read from SharedPreferences
                    "light" -> false  // Light theme = not dark
                    "dark" -> true    // Dark theme = dark
                    else -> isSystemInDarkMode // System default
                }
            }

            LifeCareTheme(darkTheme = isDarkMode) {

                // ============ AUTHENTICATION STATE ============
                val isUserLoggedIn = authViewModel.isUserLoggedIn()
                val isPINSet = healthDataManager.isPINSet()
                val isPINSessionValid = pinSessionManager.isSessionValid()

                // ============ NAVIGATION STATE ============
                // Determine initial screen based on auth + PIN state
                val initialScreen = remember(isUserLoggedIn, isPINSet, isPINSessionValid) {
                    when {
                        !isUserLoggedIn -> AppScreen.LOGIN
                        !isPINSet -> AppScreen.PIN_CREATE
                        !isPINSessionValid -> AppScreen.PIN_VERIFY
                        else -> AppScreen.HOME
                    }
                }

                var currentScreen by remember { mutableStateOf(initialScreen) }

                // ============ BACK BUTTON HANDLER ============
                BackHandler(enabled = true) {
                    when (currentScreen) {
                        AppScreen.LOGIN, AppScreen.REGISTER -> {
                            // Exit app from login/register screen
                            finish()
                        }
                        AppScreen.PIN_CREATE, AppScreen.PIN_VERIFY -> {
                            // Cannot go back from PIN screens (security)
                            // Just exit app
                            finish()
                        }
                        AppScreen.HOME -> {
                            // Exit app from home screen
                            finish()
                        }
                    }
                }

                // ============ SCREEN NAVIGATION ============
                when (currentScreen) {
                    // === HOME SCREEN ===
                    AppScreen.HOME -> {
                        HomeScreen(
                            onLogoutClick = {
                                // Logout flow
                                authViewModel.logout()
                                pinSessionManager.clearSession()
                                currentScreen = AppScreen.LOGIN
                            },
                            onThemeToggle = {
                                // FIX: Force update theme from SharedPreferences
                                themeMode = themeManager.getThemeMode()
                                themeUpdateTrigger++  // Trigger recomposition
                            }
                        )
                    }

                    // === PIN CREATE (Setelah register pertama kali) ===
                    AppScreen.PIN_CREATE -> {
                        PINScreen(
                            healthDataManager = healthDataManager,
                            authViewModel = authViewModel,
                            onPINVerified = {
                                // PIN created, mark session as valid
                                pinSessionManager.markPINVerified()
                                currentScreen = AppScreen.HOME
                            },
                            forceCreateMode = true
                        )
                    }

                    // === PIN VERIFY (Session expired atau baru buka app) ===
                    AppScreen.PIN_VERIFY -> {
                        // Check if PIN still exists (bisa saja di-clear via forgot PIN)
                        val isPINStillSet = healthDataManager.isPINSet()

                        if (!isPINStillSet) {
                            // PIN was cleared, navigate to create mode
                            currentScreen = AppScreen.PIN_CREATE
                        } else {
                            PINScreen(
                                healthDataManager = healthDataManager,
                                authViewModel = authViewModel,
                                onPINVerified = {
                                    // PIN verified, renew session
                                    pinSessionManager.markPINVerified()
                                    currentScreen = AppScreen.HOME
                                },
                                forceCreateMode = false
                            )
                        }
                    }

                    // === REGISTER SCREEN ===
                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            authViewModel = authViewModel,
                            healthDataManager = healthDataManager,
                            onLoginClick = {
                                currentScreen = AppScreen.LOGIN
                            },
                            onRegisterSuccess = {
                                // After register, need to create PIN
                                currentScreen = AppScreen.PIN_CREATE
                            }
                        )
                    }

                    // === LOGIN SCREEN ===
                    AppScreen.LOGIN -> {
                        LoginScreen(
                            authViewModel = authViewModel,
                            healthDataManager = healthDataManager,
                            onRegisterClick = {
                                currentScreen = AppScreen.REGISTER
                            },
                            onLoginSuccess = {
                                // After login, check PIN status
                                currentScreen = if (healthDataManager.isPINSet()) {
                                    // PIN already set, need to verify
                                    AppScreen.PIN_VERIFY
                                } else {
                                    // First time login, need to create PIN
                                    AppScreen.PIN_CREATE
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * AppScreen - Sealed class untuk type-safe navigation
 * Lebih baik dari String-based navigation
 *
 * Note: CHARTS & REMINDERS navigation handled internally by HomeScreen
 */
sealed class AppScreen {
    object LOGIN : AppScreen()
    object REGISTER : AppScreen()
    object PIN_CREATE : AppScreen()
    object PIN_VERIFY : AppScreen()
    object HOME : AppScreen()
}
