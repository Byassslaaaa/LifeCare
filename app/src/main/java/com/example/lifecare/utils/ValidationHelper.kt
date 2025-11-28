package com.example.lifecare.utils

import android.util.Patterns

/**
 * ValidationHelper - Centralized validation logic
 * Used by LoginScreen and RegisterScreen for consistent validation
 */
object ValidationHelper {

    /**
     * Validate email format
     * @return Error message or null if valid
     */
    fun validateEmail(email: String): String? = when {
        email.isBlank() -> null // Don't show error for empty field
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            "Format email tidak valid"
        else -> null
    }

    /**
     * Validate password length
     * @return Error message or null if valid
     */
    fun validatePassword(password: String): String? = when {
        password.isBlank() -> null
        password.length < 6 -> "Password minimal 6 karakter"
        else -> null
    }

    /**
     * Validate full name length
     * @return Error message or null if valid
     */
    fun validateFullName(fullName: String): String? = when {
        fullName.isBlank() -> null
        fullName.length < 3 -> "Nama minimal 3 karakter"
        else -> null
    }

    /**
     * Validate password confirmation match
     * @return Error message or null if valid
     */
    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? = when {
        confirmPassword.isBlank() -> null
        password != confirmPassword -> "Password tidak cocok"
        else -> null
    }

    /**
     * Validate age range
     * @return Error message or null if valid
     */
    fun validateAge(age: String): String? = when {
        age.isBlank() -> null
        age.toIntOrNull() == null -> "Umur harus berupa angka"
        age.toInt() < 13 -> "Umur minimal 13 tahun"
        age.toInt() > 150 -> "Umur tidak valid"
        else -> null
    }
}
