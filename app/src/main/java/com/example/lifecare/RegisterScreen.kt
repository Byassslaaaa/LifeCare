package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.text.font.FontWeight
import com.example.lifecare.auth.AuthViewModel
import com.example.lifecare.auth.AuthUiState
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.utils.ValidationHelper

/**
 * RegisterScreen - Halaman Register dengan Firebase Authentication
 * Mendukung register dengan Email/Password dan Google Sign-In
 */
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    healthDataManager: HealthDataManager,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Real-time validation states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Real-time validation functions - using ValidationHelper
    fun validateFullName() {
        fullNameError = ValidationHelper.validateFullName(fullName)
    }

    fun validateEmail() {
        emailError = ValidationHelper.validateEmail(email)
    }

    fun validatePassword() {
        passwordError = ValidationHelper.validatePassword(password)
    }

    fun validateConfirmPassword() {
        confirmPasswordError = ValidationHelper.validateConfirmPassword(password, confirmPassword)
    }

    // Observe auth state
    val authState by authViewModel.authState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                // FIX: Save user data to local storage for profile display
                healthDataManager.saveUserData(
                    fullName = fullName,
                    email = email,
                    password = password, // Will be hashed by HealthDataManager
                    age = "",
                    gender = ""
                )
                healthDataManager.setLoggedIn(true)

                Toast.makeText(context, state.message + "\n\nSilakan buat PIN 6 digit untuk keamanan data Anda", Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
                onRegisterSuccess()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    val isLoading = authState is AuthUiState.Loading

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logoDepan),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                "Life Care",
                color = textColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                "Daftar",
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // FULL NAME
            TextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    validateFullName()
                },
                placeholder = { Text("Nama Lengkap", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    disabledContainerColor = surfaceColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // EMAIL
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    validateEmail()
                },
                placeholder = { Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    autoCorrect = false
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    disabledContainerColor = surfaceColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PASSWORD
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    validatePassword()
                    validateConfirmPassword() // Re-validate confirm password when password changes
                },
                placeholder = { Text("Password", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    disabledContainerColor = surfaceColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONFIRM PASSWORD
            TextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validateConfirmPassword()
                },
                placeholder = { Text("Verifikasi Password", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = surfaceColor,
                    unfocusedContainerColor = surfaceColor,
                    disabledContainerColor = surfaceColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // REGISTER BUTTON
            Button(
                onClick = {
                    authViewModel.registerWithEmail(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        fullName = fullName,
                        age = "",
                        gender = ""
                    )
                },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Buat Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text(
                    text = "  atau  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GOOGLE SIGN-UP BUTTON
            Button(
                onClick = {
                    authViewModel.signInWithGoogle()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = surfaceColor,
                    contentColor = textColor
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF4285F4),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logoGoogle),
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Daftar dengan Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign In Link
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    color = textColor,
                    fontSize = 14.sp
                )
                Text(
                    text = "Masuk",
                    color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = !isLoading) { onLoginClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
