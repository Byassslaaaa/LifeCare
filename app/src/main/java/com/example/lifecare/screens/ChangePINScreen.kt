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
                        "Ubah PIN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = com.example.lifecare.ui.theme.HealthColors.NeonGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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

            // Icon with 123
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "123",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

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
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Old PIN Field
            TextField(
                value = oldPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        oldPin = it
                    }
                },
                placeholder = { Text("PIN Lama", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New PIN Field
            TextField(
                value = newPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        newPin = it
                    }
                },
                placeholder = { Text("PIN Baru", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm PIN Field
            TextField(
                value = confirmPin,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        confirmPin = it
                    }
                },
                placeholder = { Text("Konfirmasi PIN Baru", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
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
                    .height(56.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = com.example.lifecare.ui.theme.HealthColors.NeonGreen
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
                        "Simpan Perubahan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Catatan :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.example.lifecare.ui.theme.HealthColors.NeonGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Gunakan kombinasi angka yang sulit ditebak\n" +
                        "• Jangan gunakan tanggal lahir atau angka berurutan\n" +
                        "• Ubah PIN secara berkala untuk keamanan\n" +
                        "• Jangan bagikan PIN Anda kepada siapapun",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
