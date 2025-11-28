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
import com.example.lifecare.utils.ValidationHelper
import com.example.lifecare.utils.Dimensions

/**
 * LoginScreen - Halaman Login dengan Firebase Authentication
 * Mendukung login dengan Email/Password dan Google Sign-In
 */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo App",
            modifier = Modifier.size(Dimensions.LogoSize)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            "Masuk",
            color = Color(0xFF2196F3),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Masuk untuk melanjutkan",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // EMAIL FIELD
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validateEmail()
            },
            label = { Text("Email") },
            placeholder = { Text("Masukkan email Anda") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                autoCorrect = false
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF33A1E0),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF33A1E0),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PASSWORD FIELD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                validatePassword()
            },
            label = { Text("Password") },
            placeholder = { Text("Masukkan password Anda") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF33A1E0),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF33A1E0),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
                        tint = Color(0xFF33A1E0)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        // LOGIN BUTTON
        Button(
            onClick = {
                authViewModel.loginWithEmail(email, password)
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
            Text(
                text = "  atau  ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GOOGLE SIGN-IN BUTTON
        OutlinedButton(
            onClick = {
                authViewModel.signInWithGoogle()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF757575)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD)),
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
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Google",
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Masuk dengan Google",
                        color = Color(0xFF757575),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sign Up Link
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Gray.copy(alpha = 0.6f))) {
                    append("Belum punya akun? ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF98CD00), fontWeight = FontWeight.Bold)) {
                    append("Daftar")
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.clickable(enabled = !isLoading) { onRegisterClick() }
        )
    }
}
