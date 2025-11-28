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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material.icons.filled.Email
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.lifecare.auth.GoogleSignInHelper
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    healthDataManager: com.example.lifecare.data.HealthDataManager,
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleSignInHelper = remember { GoogleSignInHelper(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isGoogleSignInLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo App",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Sign Up",
            color = Color(0xFF2196F3),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Masukkan Email") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF33A1E0),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF33A1E0)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Masukkan Password") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF33A1E0),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF33A1E0)
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF33A1E0)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                // Validation
                when {
                    email.isEmpty() && password.isEmpty() -> {
                        Toast.makeText(context, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    email.isEmpty() -> {
                        Toast.makeText(context, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    password.isEmpty() -> {
                        Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // Verifikasi login dengan data yang tersimpan
                if (healthDataManager.verifyLogin(email, password)) {
                    healthDataManager.setLoggedIn(true) // Simpan status login ke storage
                    Toast.makeText(context, "Login berhasil! Selamat datang kembali", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    // Check if user exists
                    if (!healthDataManager.isUserRegistered()) {
                        Toast.makeText(context, "Akun belum terdaftar. Silakan daftar terlebih dahulu", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Email atau password salah. Periksa kembali dan coba lagi", Toast.LENGTH_LONG).show()
                    }
                }

            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider with "Atau"
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

        // Google Sign-In Button
        OutlinedButton(
            onClick = {
                if (isGoogleSignInLoading) return@OutlinedButton

                isGoogleSignInLoading = true
                scope.launch {
                    try {
                        val result = googleSignInHelper.signIn()

                        result.onSuccess { googleResult ->
                            // Simpan data user dari Google
                            val googleEmail = googleResult.email ?: ""
                            val displayName = googleResult.displayName ?: ""

                            // Validate Google email
                            if (googleEmail.isEmpty()) {
                                Toast.makeText(context, "Tidak dapat mengambil email dari akun Google", Toast.LENGTH_LONG).show()
                                return@onSuccess
                            }

                            // Cek apakah user sudah terdaftar
                            val existingUser = healthDataManager.getUserData()

                            if (existingUser == null) {
                                // User baru dari Google - arahkan ke register
                                Toast.makeText(
                                    context,
                                    "Akun Google belum terdaftar.\nSilakan gunakan menu Register untuk mendaftar terlebih dahulu.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onRegisterClick() // Navigate to register screen
                            } else if (existingUser.email == googleEmail) {
                                // User sudah terdaftar dengan email Google yang sama - LOGIN SUCCESS
                                healthDataManager.setLoggedIn(true) // Simpan status login ke storage
                                Toast.makeText(context, "✅ Login dengan Google berhasil!\nSelamat datang kembali", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } else {
                                // Email Google berbeda dengan user yang sudah terdaftar
                                Toast.makeText(
                                    context,
                                    "⚠️ Email Google (${googleEmail}) tidak cocok dengan akun terdaftar (${existingUser.email})\n\n" +
                                    "Gunakan akun Google yang sama atau hapus data aplikasi di Profile.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        result.onFailure { error ->
                            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    } finally {
                        isGoogleSignInLoading = false
                    }
                }
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
            enabled = !isGoogleSignInLoading
        ) {
            if (isGoogleSignInLoading) {
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
                        "Sign in with Google",
                        color = Color(0xFF757575),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Gray.copy(alpha = 0.6f))) {
                    append("Don't have an account? ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF98CD00), fontWeight = FontWeight.Bold)) {
                    append("Sign up")
                }
            },
            fontSize = 14.sp,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}