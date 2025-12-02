package com.example.lifecare.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifecare.R
import com.example.lifecare.data.HealthDataManager
import androidx.compose.ui.platform.LocalContext

private val EditPrimary = Color(0xFF33A1E0)

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
                        "Edit Profile",
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

            Spacer(modifier = Modifier.height(8.dp))

            // ===== AVATAR (Large) =====
            if (profilePhotoUri != null) {
                AsyncImage(
                    model = profilePhotoUri,
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0))
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto Profil",
                        tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User name (bold, theme-aware)
            Text(
                text = fullName.ifEmpty { "Nama Lengkap" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Email (NeonGreen)
            Text(
                text = userData?.email ?: "email@example.com",
                fontSize = 14.sp,
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Title text (NeonGreen)
            Text(
                "Ubah Informasi Profile Anda",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = com.example.lifecare.ui.theme.HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== INPUT FIELDS (Gray Background) =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Nama Lengkap
                TextField(
                    value = fullName,
                    onValueChange = {
                        if (it.length <= 50) {
                            fullName = it
                            fullNameError = null
                        }
                    },
                    placeholder = { Text("Nama Lengkap", color = MaterialTheme.colorScheme.onSurfaceVariant) },
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

                // Umur
                TextField(
                    value = age,
                    onValueChange = {
                        if (it.isEmpty() || (it.all { ch -> ch.isDigit() } && it.length <= 3)) {
                            age = it
                            ageError = null
                        }
                    },
                    placeholder = { Text("Umur", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                // Gender (dropdown)
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    TextField(
                        value = gender,
                        onValueChange = {},
                        placeholder = { Text("Jenis Kelamin", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(50.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
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

            Spacer(modifier = Modifier.height(24.dp))

            // ===== BUTTON SIMPAN (NeonGreen) =====
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

            // Info note (theme-aware)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Catatan :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.example.lifecare.ui.theme.HealthColors.NeonGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Pastikan data profil yang Anda masukkan sudah benar. Perubahan ini akan digunakan untuk personalisasi dan analisis kesehatan Anda.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}