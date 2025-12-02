package com.example.lifecare

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.auth.AuthViewModel
import com.example.lifecare.auth.AuthUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.window.Dialog
import com.example.lifecare.utils.Constants
import com.example.lifecare.utils.Dimensions

@Composable
fun PINScreen(
    healthDataManager: HealthDataManager,
    authViewModel: AuthViewModel,
    onPINVerified: () -> Unit,
    forceCreateMode: Boolean = false
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var showForgotPINDialog by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val existingPIN = healthDataManager.getUserPIN()
    val isSettingPIN = forceCreateMode || existingPIN == null

    // Observe auth state for forgot PIN flow
    val authState by authViewModel.authState.collectAsState()

    // Keyboard otomatis
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logodepan),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )

        // App Name
        Text(
            text = "Life Care",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Title
        Text(
            text = "Masukkan PIN Anda",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // PIN BOX - 6 boxes rounded
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        ) {
            repeat(Constants.PIN_LENGTH) { index ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = if (index < pin.length) "●" else "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Divider line
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Invisible TextField
        TextField(
            value = pin,
            onValueChange = {
                if (it.length <= Constants.PIN_LENGTH && it.all(Char::isDigit)) {
                    pin = it
                }
            },
            modifier = Modifier
                .size(1.dp)
                .alpha(0f)
                .focusRequester(focusRequester),

            // keyboard numeric
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword
            ),

            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // Masuk Button
        Button(
            onClick = {
                if (pin.length != Constants.PIN_LENGTH) {
                    Toast.makeText(context, "PIN harus ${Constants.PIN_LENGTH} digit", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (isSettingPIN) {
                    healthDataManager.saveUserPIN(pin)
                    Toast.makeText(context, "PIN berhasil dibuat", Toast.LENGTH_SHORT).show()
                    onPINVerified()
                } else {
                    if (healthDataManager.verifyPIN(pin)) {
                        onPINVerified()
                    } else {
                        Toast.makeText(context, "PIN salah!", Toast.LENGTH_SHORT).show()
                        pin = ""
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Masuk",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Show "Forgot PIN" only when verifying (not creating)
        if (!isSettingPIN) {
            Text(
                text = "Forgot PIN",
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    showForgotPINDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }

    // Forgot PIN Dialog
    if (showForgotPINDialog) {
        ForgotPINDialog(
            authViewModel = authViewModel,
            healthDataManager = healthDataManager,
            onDismiss = { showForgotPINDialog = false },
            onPINReset = {
                Toast.makeText(context, "PIN berhasil direset. Silakan buat PIN baru.", Toast.LENGTH_LONG).show()
                showForgotPINDialog = false
                // Clear PIN and let parent handle navigation to PIN create
                healthDataManager.clearPIN()
                pin = ""
            }
        )
    }
}

/**
 * ForgotPINDialog - Dialog untuk reset PIN dengan verifikasi password Firebase
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPINDialog(
    authViewModel: AuthViewModel,
    healthDataManager: HealthDataManager,
    onDismiss: () -> Unit,
    onPINReset: () -> Unit
) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()

    // Handle auth state for reauthentication
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthUiState.Success -> {
                // Password verified successfully, clear PIN
                healthDataManager.clearPIN()
                authViewModel.resetAuthState()
                onPINReset()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    val isLoading = authState is AuthUiState.Loading

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Reset PIN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masukkan password akun Anda untuk mereset PIN",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Masukkan password Anda") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF2D3748),
                        unfocusedTextColor = Color(0xFF2D3748),
                        focusedBorderColor = Color(0xFF33A1E0),
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = Color(0xFF33A1E0),
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

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF33A1E0)
                        )
                    ) {
                        Text("Batal", fontSize = 16.sp)
                    }

                    // Verify Button
                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                Toast.makeText(context, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            // Use reauthenticate to verify password
                            authViewModel.reauthenticateWithPassword(password)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF33A1E0)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Verifikasi", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}