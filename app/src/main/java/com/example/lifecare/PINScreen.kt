package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import androidx.compose.ui.text.style.TextAlign

@Composable
fun PINScreen(
    healthDataManager: HealthDataManager,
    onPINVerified: () -> Unit,
    forceCreateMode: Boolean = false
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var pinVisible by remember { mutableStateOf(false) }
    val existingPIN = healthDataManager.getUserPIN()
    val isSettingPIN = forceCreateMode || existingPIN == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.VisibilityOff,
            contentDescription = "Security",
            modifier = Modifier.size(80.dp),
            tint = Color(0xFF33A1E0)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isSettingPIN) "Buat PIN" else "Masukkan PIN",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF33A1E0)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSettingPIN)
                "Buat PIN 6 digit untuk melindungi data kesehatan Anda"
            else
                "Masukkan PIN untuk mengakses data kesehatan",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) pin = it
            },
            label = { Text("PIN 6 Digit") },
            placeholder = { Text("Masukkan PIN") },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (pinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { pinVisible = !pinVisible }) {
                    Icon(
                        imageVector = if (pinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color(0xFF33A1E0)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF2D3748),
                unfocusedTextColor = Color(0xFF2D3748),
                focusedBorderColor = Color(0xFF33A1E0),
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color(0xFF33A1E0)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (pin.length != 6) {
                    Toast.makeText(context, "PIN harus 6 digit", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (isSettingPIN) {
                    // Save new PIN
                    healthDataManager.saveUserPIN(pin)
                    Toast.makeText(context, "PIN berhasil dibuat", Toast.LENGTH_SHORT).show()
                    onPINVerified()
                } else {
                    // Verify existing PIN
                    if (healthDataManager.verifyPIN(pin)) {
                        onPINVerified()
                    } else {
                        Toast.makeText(context, "PIN salah!", Toast.LENGTH_SHORT).show()
                        pin = ""
                    }
                }
            },
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF33A1E0)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                if (isSettingPIN) "Buat PIN" else "Verifikasi",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
