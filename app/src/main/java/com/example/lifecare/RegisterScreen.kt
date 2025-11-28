package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.lifecare.utils.ValidationHelper
import com.example.lifecare.utils.PasswordStrengthCalculator
import com.example.lifecare.utils.PasswordStrength
import com.example.lifecare.utils.Dimensions

/**
 * RegisterScreen - Halaman Register dengan Firebase Authentication
 * Mendukung register dengan Email/Password dan Google Sign-In
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")

    // Real-time validation states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    // Password strength - using utility
    val passwordStrength = remember(password) {
        derivedStateOf {
            PasswordStrengthCalculator.calculate(password)
        }
    }.value

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

    fun validateAge() {
        ageError = ValidationHelper.validateAge(age)
    }

    // Observe auth state
    val authState by authViewModel.authState.collectAsState()

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffffff))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(Dimensions.LogoSize)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            "Daftar",
            color = Color(0xFF98CD00),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Daftar untuk mulai tracking kesehatan",
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // FULL NAME
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                validateFullName()
            },
            label = { Text("Nama Lengkap") },
            placeholder = { Text("Masukkan nama lengkap") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = fullNameError != null,
            supportingText = fullNameError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF98CD00),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF98CD00),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                validateEmail()
            },
            label = { Text("Email") },
            placeholder = { Text("contoh@email.com") },
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
                focusedBorderColor = Color(0xFF98CD00),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF98CD00),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PASSWORD
        Column {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    validatePassword()
                    validateConfirmPassword() // Re-validate confirm password when password changes
                },
                label = { Text("Password") },
                placeholder = { Text("Minimal 6 karakter") },
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
                    focusedBorderColor = Color(0xFF98CD00),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color(0xFF98CD00),
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
                            tint = Color(0xFF98CD00)
                        )
                    }
                }
            )

            // Password Strength Indicator - using utility
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Dimensions.SpacingTiny))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kekuatan: ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = PasswordStrengthCalculator.getLabel(passwordStrength),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PasswordStrengthCalculator.getColor(passwordStrength)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CONFIRM PASSWORD
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                validateConfirmPassword()
            },
            label = { Text("Konfirmasi Password") },
            placeholder = { Text("Masukkan password lagi") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = confirmPasswordError != null,
            supportingText = confirmPasswordError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF98CD00),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF98CD00),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            ),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password",
                        tint = Color(0xFF98CD00)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // AGE
        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    age = it
                    validateAge()
                }
            },
            label = { Text("Umur") },
            placeholder = { Text("Masukkan umur") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            isError = ageError != null,
            supportingText = ageError?.let { { Text(it, color = Color.Red, fontSize = 12.sp) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF98CD00),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF98CD00),
                errorBorderColor = Color.Red,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // GENDER DROPDOWN
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { if (!isLoading) genderExpanded = !genderExpanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Jenis Kelamin") },
                placeholder = { Text("Pilih jenis kelamin") },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                enabled = !isLoading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF2D3748),
                    unfocusedTextColor = Color(0xFF2D3748),
                    focusedBorderColor = Color(0xFF98CD00),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color(0xFF98CD00),
                    disabledBorderColor = Color.LightGray,
                    disabledTextColor = Color.Gray
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded)
                }
            )

            DropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            genderExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // REGISTER BUTTON
        Button(
            onClick = {
                authViewModel.registerWithEmail(
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    fullName = fullName,
                    age = age,
                    gender = gender
                )
            },
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF98CD00)),
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
                Text(
                    "Buat Akun",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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

        // GOOGLE SIGN-UP BUTTON
        OutlinedButton(
            onClick = {
                authViewModel.signInWithGoogle()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50.dp),
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
                        "Daftar dengan Google",
                        color = Color(0xFF757575),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Link
        Row(
            modifier = Modifier.clickable(enabled = !isLoading) { onLoginClick() },
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sudah punya akun? ",
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                text = "Masuk",
                color = Color(0xFF33A1E0),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
