package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePINScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onPINChanged: () -> Unit
) {
    val context = LocalContext.current

    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ganti PIN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5DCCB4),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Icon
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFF5DCCB4)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Ubah PIN Keamanan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Masukkan PIN lama dan PIN baru Anda",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Old PIN Field
            OutlinedTextField(
                value = oldPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        oldPin = it
                    }
                },
                label = { Text("PIN Lama") },
                placeholder = { Text("Masukkan PIN lama (6 digit)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5DCCB4),
                    focusedLabelColor = Color(0xFF5DCCB4),
                    cursorColor = Color(0xFF5DCCB4)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New PIN Field
            OutlinedTextField(
                value = newPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        newPin = it
                    }
                },
                label = { Text("PIN Baru") },
                placeholder = { Text("Masukkan PIN baru (6 digit)") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5DCCB4),
                    focusedLabelColor = Color(0xFF5DCCB4),
                    cursorColor = Color(0xFF5DCCB4)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm PIN Field
            OutlinedTextField(
                value = confirmPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        confirmPin = it
                    }
                },
                label = { Text("Konfirmasi PIN Baru") },
                placeholder = { Text("Masukkan ulang PIN baru") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5DCCB4),
                    focusedLabelColor = Color(0xFF5DCCB4),
                    cursorColor = Color(0xFF5DCCB4)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    // Validasi
                    if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                        Toast.makeText(context, "Harap isi semua field", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (oldPin.length != 6) {
                        Toast.makeText(context, "PIN lama harus 6 digit", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (newPin.length != 6) {
                        Toast.makeText(context, "PIN baru harus 6 digit", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (newPin != confirmPin) {
                        Toast.makeText(context, "PIN baru tidak cocok", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (oldPin == newPin) {
                        Toast.makeText(context, "PIN baru harus berbeda dari PIN lama", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    // Change PIN
                    val success = healthDataManager.changePIN(oldPin, newPin)

                    isLoading = false

                    if (success) {
                        Toast.makeText(context, "PIN berhasil diubah!", Toast.LENGTH_LONG).show()
                        onPINChanged()
                    } else {
                        Toast.makeText(context, "PIN lama salah", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5DCCB4)
                ),
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
                        "Ubah PIN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Tips Keamanan PIN:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Gunakan kombinasi angka yang sulit ditebak\n" +
                        "• Jangan gunakan tanggal lahir atau angka berurutan\n" +
                        "• Ubah PIN secara berkala untuk keamanan\n" +
                        "• Jangan bagikan PIN Anda kepada siapapun",
                        fontSize = 12.sp,
                        color = Color(0xFF2196F3),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
