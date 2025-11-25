package com.example.lifecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.theme.LifeCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LifeCareTheme {
                // Initialize HealthDataManager
                val healthDataManager = remember { HealthDataManager(this) }

                // LOGIN STATE - Cek dari storage (persistent)
                var isLoggedIn by remember { mutableStateOf(healthDataManager.isLoggedIn()) }

                // PIN VERIFIED STATE (untuk sesi saat ini)
                var isPinVerified by remember { mutableStateOf(false) }

                // REGISTER SCREEN STATE
                var showRegister by remember { mutableStateOf(false) }

                // JUST REGISTERED STATE (untuk langsung setup PIN setelah register)
                var justRegistered by remember { mutableStateOf(false) }

                when {
                    // === SETELAH REGISTER, SETUP PIN TERLEBIH DAHULU ===
                    justRegistered -> {
                        PINScreen(
                            healthDataManager = healthDataManager,
                            onPINVerified = {
                                justRegistered = false
                                isLoggedIn = true
                                isPinVerified = true
                            },
                            forceCreateMode = true
                        )
                    }

                    // === USER SUDAH LOGIN DAN PIN VERIFIED ===
                    isLoggedIn && isPinVerified -> {
                        HomeScreen(
                            onLogoutClick = {
                                healthDataManager.setLoggedIn(false) // Clear login state dari storage
                                isLoggedIn = false
                                isPinVerified = false
                            }
                        )
                    }

                    // === USER SUDAH LOGIN TAPI BELUM VERIFY PIN ===
                    isLoggedIn && !isPinVerified -> {
                        PINScreen(
                            healthDataManager = healthDataManager,
                            onPINVerified = {
                                isPinVerified = true
                            }
                        )
                    }

                    // === REGISTER SCREEN ===
                    showRegister -> {
                        RegisterScreen(
                            healthDataManager = healthDataManager,
                            onLoginClick = {
                                showRegister = false
                            },
                            onRegisterSuccess = {
                                showRegister = false
                                justRegistered = true
                            }
                        )
                    }

                    // === LOGIN SCREEN ===
                    else -> {
                        LoginScreen(
                            healthDataManager = healthDataManager,
                            onRegisterClick = {
                                showRegister = true
                            },
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}