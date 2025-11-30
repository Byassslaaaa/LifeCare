package com.example.lifecare.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifecare.data.HealthDataManager
import androidx.compose.ui.platform.LocalContext

private val EditPrimary = Color(0xFF5DCCB4)
private val EditBackground = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onProfileSaved: () -> Unit
) {
    val context = LocalContext.current

    // ambil data user dari HealthDataManager
    val userData = healthDataManager.getUserData()

    var fullName by remember { mutableStateOf(userData?.fullName ?: "") }
    var age by remember { mutableStateOf(userData?.age ?: "") }
    var gender by remember { mutableStateOf(userData?.gender ?: "") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }

    val genderOptions = listOf("Laki-laki", "Perempuan")
    var genderExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Profile photo state
    var profilePhotoUri by remember {
        mutableStateOf<Uri?>(
            healthDataManager.getProfilePhotoUri()?.let { Uri.parse(it) }
        )
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profilePhotoUri = it
            healthDataManager.saveProfilePhotoUri(it.toString())
            Toast.makeText(context, "Foto profil berhasil diubah", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profil",
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
                    containerColor = EditPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(EditBackground)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // ===== AVATAR + CHANGE PHOTO =====
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // Profile photo or default avatar
                if (profilePhotoUri != null) {
                    AsyncImage(
                        model = profilePhotoUri,
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5E5))
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E5E5))
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto Profil",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // badge kamera kecil di pojok bawah
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EditPrimary)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Ubah Foto",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Ubah informasi profil Anda",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== CARD FORM =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Nama Lengkap
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
                            fullNameError?.let { err ->
                                Text(
                                    err,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditPrimary,
                            focusedLabelColor = EditPrimary,
                            cursorColor = EditPrimary
                        )
                    )

                    // Umur
                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            if (it.isEmpty() || (it.all { ch -> ch.isDigit() } && it.length <= 3)) {
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
                            ageError?.let { err ->
                                Text(
                                    err,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditPrimary,
                            focusedLabelColor = EditPrimary,
                            cursorColor = EditPrimary
                        )
                    )

                    // Gender (dropdown)
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            label = { Text("Jenis Kelamin") },
                            placeholder = { Text("Pilih jenis kelamin") },
                            readOnly = true,
                            isError = genderError != null,
                            supportingText = {
                                genderError?.let { err ->
                                    Text(
                                        err,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EditPrimary,
                                focusedLabelColor = EditPrimary,
                                cursorColor = EditPrimary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        gender = option
                                        genderError = null
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== BUTTON SIMPAN =====
            Button(
                onClick = {
                    var hasError = false
                    val ageValue = age.toIntOrNull()

                    if (fullName.isBlank()) {
                        fullNameError = "Nama lengkap harus diisi"
                        hasError = true
                    } else if (fullName.length < 3) {
                        fullNameError = "Nama terlalu pendek (minimal 3 karakter)"
                        hasError = true
                    }

                    if (age.isBlank()) {
                        ageError = "Umur harus diisi"
                        hasError = true
                    } else if (ageValue == null) {
                        ageError = "Masukkan angka yang valid"
                        hasError = true
                    } else if (ageValue < 1 || ageValue > 150) {
                        ageError = "Umur harus antara 1–150"
                        hasError = true
                    }

                    if (gender.isBlank()) {
                        genderError = "Pilih jenis kelamin"
                        hasError = true
                    }

                    if (hasError || userData == null) return@Button

                    isLoading = true

                    // simpan ke HealthDataManager (tanpa mengubah email & password)
                    healthDataManager.saveUserData(
                        fullName = fullName.trim(),
                        email = userData.email,
                        password = userData.password,
                        age = age.trim(),
                        gender = gender.trim()
                    )

                    isLoading = false

                    Toast.makeText(
                        context,
                        "Profil berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()

                    onProfileSaved()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditPrimary
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

            // Info kecil
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Catatan:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Pastikan data profil yang Anda masukkan sudah benar. " +
                                "Perubahan ini akan digunakan untuk personalisasi dan analisis kesehatan Anda.",
                        fontSize = 12.sp,
                        color = Color(0xFF2196F3),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}