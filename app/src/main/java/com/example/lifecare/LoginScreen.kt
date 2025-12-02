package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.auth.AuthViewModel
import com.example.lifecare.auth.AuthUiState
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.utils.ValidationHelper
import com.example.lifecare.utils.Dimensions
import com.google.firebase.auth.FirebaseAuth

/**
 * LoginScreen - Halaman Login dengan Firebase Authentication
 * Mendukung login dengan Email/Password dan Google Sign-In
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    healthDataManager: HealthDataManager,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Real-time validation states
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Real-time validation functions - using ValidationHelper
    fun validateEmail() {
        emailError = ValidationHelper.validateEmail(email)
    }

    fun validatePassword() {
        passwordError = ValidationHelper.validatePassword(password)
    }

    // Observe auth state
    val authState by authViewModel.authState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                // FIX: Save user data to local storage (especially for Google Sign-In)
                val firebaseUser = state.user
                if (firebaseUser != null) {
                    // Check if user data already exists in local storage
                    val existingUser = healthDataManager.getUserData()

                    if (existingUser == null) {
                        // New user (Google Sign-In first time) - save to local
                        healthDataManager.saveUserData(
                            fullName = firebaseUser.displayName ?: "Pengguna LifeCare",
                            email = firebaseUser.email ?: "",
                            password = "", // No password for Google/existing users
                            age = "",
                            gender = ""
                        )
                    }
                    healthDataManager.setLoggedIn(true)
                }

                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
                onLoginSuccess()
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
                painter = painterResource(id = R.drawable.logodepan),
                contentDescription = "Logo App",
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
                "Masuk",
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        Spacer(modifier = Modifier.height(32.dp))

        // EMAIL FIELD
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

        // PASSWORD FIELD
        TextField(
            value = password,
            onValueChange = {
                password = it
                validatePassword()
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

        Spacer(modifier = Modifier.height(32.dp))

        // LOGIN BUTTON
        Button(
            onClick = {
                authViewModel.loginWithEmail(email, password)
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
                Text("Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

        // GOOGLE SIGN-IN BUTTON
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
                        painter = painterResource(id = R.drawable.logogoogle),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Masuk dengan Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign Up Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum punya akun? ",
                color = textColor,
                fontSize = 14.sp
            )
            Text(
                text = "Daftar",
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = !isLoading) { onRegisterClick() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
