package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onChangePIN: () -> Unit = {}
) {
    val context = LocalContext.current
    var showChangePINDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val userData = healthDataManager.getUserData()
    var userFullName by remember { mutableStateOf(userData?.fullName ?: "Pengguna LifeCare") }
    var userEmail by remember { mutableStateOf(userData?.email ?: "") }
    var userAge by remember { mutableStateOf(userData?.age ?: "") }
    var userGender by remember { mutableStateOf(userData?.gender ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil & Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5DCCB4),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5DCCB4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        userFullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3748)
                    )
                    Text(
                        userEmail.ifEmpty { "Kelola profil dan pengaturan Anda" },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Edit Profile Button
                    TextButton(
                        onClick = { showEditProfileDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF5DCCB4)
                        )
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Profil", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info Section
            if (userData != null) {
                Text(
                    "Informasi Pribadi",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        UserInfoItem("Umur", if (userAge.isNotEmpty()) "$userAge tahun" else "Belum diisi")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        UserInfoItem("Jenis Kelamin", userGender.ifEmpty { "Belum diisi" })
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        UserInfoItem("Email", userEmail)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Section
            Text(
                "Statistik Data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatisticItem("Total Data Berat & Tinggi", healthDataManager.getBodyMetricsList().size)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    StatisticItem("Total Data Tekanan Darah", healthDataManager.getBloodPressureList().size)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    StatisticItem("Total Data Gula Darah", healthDataManager.getBloodSugarList().size)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    StatisticItem("Total Aktivitas Fisik", healthDataManager.getPhysicalActivityList().size)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    StatisticItem("Total Asupan Makanan", healthDataManager.getFoodIntakeList().size)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            Text(
                "Pengaturan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "Ubah PIN",
                        subtitle = "Ganti PIN keamanan data",
                        onClick = onChangePIN
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    SettingItem(
                        icon = Icons.Default.Delete,
                        title = "Hapus Semua Data",
                        subtitle = "Hapus seluruh data kesehatan",
                        iconColor = Color(0xFFFF6B6B),
                        onClick = { showClearDataDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            var showLogoutDialog by remember { mutableStateOf(false) }

            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari Akun", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFFF6B6B)) },
                    title = { Text("Keluar dari Akun?") },
                    text = { Text("Anda akan keluar dari aplikasi dan harus login kembali untuk mengakses data kesehatan. Data Anda akan tetap tersimpan.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFF6B6B))
                        ) {
                            Text("Keluar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clear All Data Button
            var showClearDataDialog by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showClearDataDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF6B6B)
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF6B6B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus Semua Data & Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // Clear Data Confirmation Dialog
            if (showClearDataDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDataDialog = false },
                    icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF6B6B), modifier = Modifier.size(48.dp)) },
                    title = { Text("Hapus Semua Data?", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("⚠️ PERINGATAN: Tindakan ini akan:", fontWeight = FontWeight.Bold, color = Color(0xFFFF6B6B))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("• Menghapus akun Anda")
                            Text("• Menghapus SEMUA data kesehatan")
                            Text("• Menghapus PIN keamanan")
                            Text("• Mengeluarkan Anda dari aplikasi")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Data yang dihapus TIDAK DAPAT dikembalikan!", fontWeight = FontWeight.Bold, color = Color(0xFFFF6B6B))
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showClearDataDialog = false
                                healthDataManager.clearAllData()
                                Toast.makeText(context, "Semua data berhasil dihapus", Toast.LENGTH_LONG).show()
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFF6B6B))
                        ) {
                            Text("Hapus Semua Data")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDataDialog = false }) {
                            Text("Batal", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Info
            Text(
                "LifeCare v1.0",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentFullName = userFullName,
            currentAge = userAge,
            currentGender = userGender,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName, newAge, newGender ->
                if (userData != null) {
                    healthDataManager.saveUserData(
                        fullName = newName,
                        email = userData.email,
                        password = userData.password,
                        age = newAge,
                        gender = newGender
                    )
                    userFullName = newName
                    userAge = newAge
                    userGender = newGender
                    Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                showEditProfileDialog = false
            }
        )
    }

    // Change PIN Dialog
    if (showChangePINDialog) {
        ChangePINDialog(
            healthDataManager = healthDataManager,
            onDismiss = { showChangePINDialog = false }
        )
    }

    // Clear Data Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF6B6B)) },
            title = { Text("Hapus Semua Data?") },
            text = { Text("Tindakan ini akan menghapus semua data kesehatan Anda secara permanen dan tidak dapat dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        healthDataManager.clearAllData()
                        Toast.makeText(context, "Semua data berhasil dihapus", Toast.LENGTH_SHORT).show()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF6B6B))
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun UserInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
    }
}

@Composable
fun StatisticItem(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color(0xFF2D3748))
        Text("$count data", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5DCCB4))
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = Color(0xFF5DCCB4),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D3748))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    currentFullName: String,
    currentAge: String,
    currentGender: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf(currentFullName) }
    var age by remember { mutableStateOf(currentAge) }
    var gender by remember { mutableStateOf(currentGender) }
    var expanded by remember { mutableStateOf(false) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val genderOptions = listOf("Laki-laki", "Perempuan")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profil") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        if (it.length <= 50) {
                            fullName = it
                            fullNameError = null
                        }
                    },
                    label = { Text("Nama Lengkap") },
                    placeholder = { Text("Masukkan nama lengkap") },
                    singleLine = true,
                    isError = fullNameError != null,
                    supportingText = {
                        if (fullNameError != null) {
                            Text(fullNameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { char -> char.isDigit() } && it.length <= 3)) {
                            age = it
                            ageError = null
                        }
                    },
                    label = { Text("Umur") },
                    placeholder = { Text("Masukkan umur") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = ageError != null,
                    supportingText = {
                        if (ageError != null) {
                            Text(ageError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        label = { Text("Jenis Kelamin") },
                        readOnly = true,
                        isError = genderError != null,
                        supportingText = {
                            if (genderError != null) {
                                Text(genderError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
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
                                    genderError = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var hasError = false

                    // Validate full name
                    if (fullName.isEmpty()) {
                        fullNameError = "Nama lengkap harus diisi"
                        hasError = true
                    } else if (fullName.length < 3) {
                        fullNameError = "Nama terlalu pendek (minimal 3 karakter)"
                        hasError = true
                    }

                    // Validate age
                    val ageValue = age.toIntOrNull()
                    if (age.isEmpty()) {
                        ageError = "Umur harus diisi"
                        hasError = true
                    } else if (ageValue == null) {
                        ageError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (ageValue < 1 || ageValue > 150) {
                        ageError = "Umur harus antara 1-150"
                        hasError = true
                    }

                    // Validate gender
                    if (gender.isEmpty()) {
                        genderError = "Pilih jenis kelamin"
                        hasError = true
                    }

                    if (!hasError) {
                        onSave(fullName, age, gender)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ChangePINDialog(
    healthDataManager: HealthDataManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var currentPIN by remember { mutableStateOf("") }
    var newPIN by remember { mutableStateOf("") }
    var confirmPIN by remember { mutableStateOf("") }
    var showCurrentPIN by remember { mutableStateOf(false) }
    var showNewPIN by remember { mutableStateOf(false) }
    var showConfirmPIN by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah PIN") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentPIN,
                    onValueChange = { if (it.length <= 6) currentPIN = it },
                    label = { Text("PIN Saat Ini") },
                    visualTransformation = if (showCurrentPIN) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPIN = !showCurrentPIN }) {
                            Icon(
                                if (showCurrentPIN) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPIN,
                    onValueChange = { if (it.length <= 6) newPIN = it },
                    label = { Text("PIN Baru") },
                    visualTransformation = if (showNewPIN) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { showNewPIN = !showNewPIN }) {
                            Icon(
                                if (showNewPIN) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPIN,
                    onValueChange = { if (it.length <= 6) confirmPIN = it },
                    label = { Text("Konfirmasi PIN Baru") },
                    visualTransformation = if (showConfirmPIN) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPIN = !showConfirmPIN }) {
                            Icon(
                                if (showConfirmPIN) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        currentPIN.length != 6 || newPIN.length != 6 || confirmPIN.length != 6 -> {
                            Toast.makeText(context, "PIN harus 6 digit", Toast.LENGTH_SHORT).show()
                        }
                        !healthDataManager.verifyPIN(currentPIN) -> {
                            Toast.makeText(context, "PIN saat ini salah", Toast.LENGTH_SHORT).show()
                        }
                        newPIN != confirmPIN -> {
                            Toast.makeText(context, "PIN baru tidak cocok", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            healthDataManager.saveUserPIN(newPIN)
                            Toast.makeText(context, "PIN berhasil diubah", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
