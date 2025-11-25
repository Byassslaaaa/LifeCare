package com.example.lifecare

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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.text.input.KeyboardType
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
import android.widget.Toast
import android.util.Patterns
import androidx.compose.material.icons.filled.Email
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    healthDataManager: com.example.lifecare.data.HealthDataManager,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val googleSignInHelper = remember { com.example.lifecare.auth.GoogleSignInHelper(context) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verifPassword by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isGoogleSignInLoading by remember { mutableStateOf(false) }
    var gender by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var verifPasswordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Buat Akun Baru",
            color = Color(0xFF2D3748),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Daftar untuk mulai tracking kesehatan",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Nama Lengkap") },
            placeholder = { Text("Masukkan nama lengkap") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF5DCCB4),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF5DCCB4)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("contoh@email.com") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF5DCCB4),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF5DCCB4)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Minimal 6 karakter") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF5DCCB4),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF5DCCB4)
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF5DCCB4)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = verifPassword,
            onValueChange = { verifPassword = it },
            label = { Text("Konfirmasi Password") },
            placeholder = { Text("Masukkan password lagi") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF5DCCB4),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF5DCCB4)
            ),
            visualTransformation = if (verifPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { verifPasswordVisible = !verifPasswordVisible }) {
                    Icon(
                        imageVector = if (verifPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF5DCCB4)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) age = it },
            label = { Text("Umur") },
            placeholder = { Text("Masukkan umur") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF5DCCB4),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF5DCCB4)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Jenis Kelamin") },
                placeholder = { Text("Pilih jenis kelamin") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF2D3748),
                    unfocusedTextColor = Color(0xFF2D3748),
                    focusedBorderColor = Color(0xFF5DCCB4),
                    unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color(0xFF5DCCB4)
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            gender = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validasi input kosong - dengan pesan spesifik
                when {
                    fullName.isEmpty() -> {
                        Toast.makeText(context, "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
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
                    verifPassword.isEmpty() -> {
                        Toast.makeText(context, "Konfirmasi password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    age.isEmpty() -> {
                        Toast.makeText(context, "Umur tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    gender.isEmpty() -> {
                        Toast.makeText(context, "Jenis kelamin harus dipilih", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // Validasi format email
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(context, "Format email tidak valid. Contoh: nama@email.com", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Validasi panjang password
                if (password.length < 6) {
                    Toast.makeText(context, "Password terlalu pendek. Minimal 6 karakter", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Validasi kecocokan password
                if (password != verifPassword) {
                    Toast.makeText(context, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_LONG).show()
                    return@Button
                }

                // Validasi umur
                val ageValue = age.toIntOrNull()
                when {
                    ageValue == null -> {
                        Toast.makeText(context, "Umur harus berupa angka", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    ageValue < 13 -> {
                        Toast.makeText(context, "Umur minimal 13 tahun untuk menggunakan aplikasi", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    ageValue > 150 -> {
                        Toast.makeText(context, "Umur tidak valid", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }

                // Simpan data user
                healthDataManager.saveUserData(fullName, email, password, age, gender)
                healthDataManager.setLoggedIn(true) // Simpan status login ke storage setelah register
                Toast.makeText(context, "✅ Registrasi Berhasil! Silakan buat PIN 6 digit untuk keamanan data Anda", Toast.LENGTH_LONG).show()
                onRegisterSuccess()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF5DCCB4)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                "Buat Akun",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
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

        // Google Sign-Up Button
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
                                // User baru dari Google, simpan data
                                healthDataManager.saveUserData(
                                    fullName = displayName.ifEmpty { "User Google" },
                                    email = googleEmail,
                                    password = "", // Password kosong untuk Google Sign-In
                                    age = "",
                                    gender = ""
                                )
                                healthDataManager.setLoggedIn(true) // Simpan status login ke storage
                                Toast.makeText(context, "✅ Registrasi dengan Google berhasil!\nSilakan buat PIN 6 digit untuk keamanan", Toast.LENGTH_LONG).show()
                                onRegisterSuccess() // This will trigger PIN creation
                            } else if (existingUser.email == googleEmail) {
                                // User sudah terdaftar dengan email Google yang sama - arahkan ke login
                                Toast.makeText(context, "Akun sudah terdaftar dengan email ini.\nSilakan gunakan menu Login.", Toast.LENGTH_LONG).show()
                                onLoginClick() // Navigate to login screen
                            } else {
                                // Email Google berbeda dengan user yang sudah terdaftar
                                Toast.makeText(
                                    context,
                                    "⚠️ Sudah ada akun terdaftar dengan email berbeda: ${existingUser.email}\n\n" +
                                    "Untuk menggunakan akun Google ini, hapus data aplikasi terlebih dahulu di Profile > Hapus Semua Data.",
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
            shape = RoundedCornerShape(12.dp),
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
                        "Sign up with Google",
                        color = Color(0xFF757575),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.clickable { onLoginClick() },
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sudah punya akun? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = "Masuk",
                color = Color(0xFF5DCCB4),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}