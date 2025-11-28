package com.example.lifecare.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AuthViewModel - ViewModel untuk mengelola state autentikasi
 * Bertindak sebagai bridge antara UI dan AuthRepository
 */
class AuthViewModel(context: Context) : ViewModel() {

    private val authRepository = AuthRepository(context)

    // State untuk UI
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    // Current user
    val currentUser: FirebaseUser?
        get() = authRepository.currentUser

    /**
     * Check if user is logged in (untuk auto-login)
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    /**
     * Register dengan email & password
     */
    fun registerWithEmail(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        age: String,
        gender: String
    ) {
        // Validation
        val validationError = validateRegistrationInput(
            email, password, confirmPassword, fullName, age, gender
        )

        if (validationError != null) {
            _authState.value = AuthUiState.Error(validationError)
            return
        }

        // Start registration
        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            when (val result = authRepository.registerWithEmail(email, password, fullName, age, gender)) {
                is AuthResult.Success -> {
                    _authState.value = AuthUiState.Success(result.user, result.message)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Login dengan email & password
     */
    fun loginWithEmail(email: String, password: String) {
        // Validation
        val validationError = validateLoginInput(email, password)

        if (validationError != null) {
            _authState.value = AuthUiState.Error(validationError)
            return
        }

        // Start login
        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            when (val result = authRepository.loginWithEmail(email, password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthUiState.Success(result.user, result.message)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Sign in dengan Google
     */
    fun signInWithGoogle() {
        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            when (val result = authRepository.signInWithGoogle()) {
                is AuthResult.Success -> {
                    _authState.value = AuthUiState.Success(result.user, result.message)
                }
                is AuthResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Reauthenticate dengan password (untuk forgot PIN flow)
     */
    fun reauthenticateWithPassword(password: String) {
        if (password.isBlank()) {
            _authState.value = AuthUiState.Error("Password tidak boleh kosong")
            return
        }

        _authState.value = AuthUiState.Loading

        viewModelScope.launch {
            when (val result = authRepository.reauthenticateWithPassword(password)) {
                is AuthResult.Success -> {
                    _authState.value = AuthUiState.Success(result.user, "Password terverifikasi")
                }
                is AuthResult.Error -> {
                    _authState.value = AuthUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Logout
     */
    fun logout() {
        authRepository.logout()
        _authState.value = AuthUiState.Initial
    }

    /**
     * Reset auth state (untuk clear error messages)
     */
    fun resetAuthState() {
        _authState.value = AuthUiState.Initial
    }

    // ============ VALIDATION FUNCTIONS ============

    private fun validateRegistrationInput(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        age: String,
        gender: String
    ): String? {
        return when {
            fullName.isBlank() -> "Nama lengkap tidak boleh kosong"
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Format email tidak valid. Contoh: nama@email.com"
            password.isBlank() -> "Password tidak boleh kosong"
            password.length < 6 -> "Password minimal 6 karakter"
            confirmPassword.isBlank() -> "Konfirmasi password tidak boleh kosong"
            password != confirmPassword -> "Password dan konfirmasi password tidak cocok"
            age.isBlank() -> "Umur tidak boleh kosong"
            age.toIntOrNull() == null -> "Umur harus berupa angka"
            age.toInt() < 13 -> "Umur minimal 13 tahun untuk menggunakan aplikasi"
            age.toInt() > 150 -> "Umur tidak valid"
            gender.isBlank() -> "Jenis kelamin harus dipilih"
            else -> null
        }
    }

    private fun validateLoginInput(email: String, password: String): String? {
        return when {
            email.isBlank() && password.isBlank() ->
                "Email dan password tidak boleh kosong"
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Format email tidak valid"
            password.isBlank() -> "Password tidak boleh kosong"
            else -> null
        }
    }
}

/**
 * AuthUiState - State untuk UI
 */
sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser, val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
