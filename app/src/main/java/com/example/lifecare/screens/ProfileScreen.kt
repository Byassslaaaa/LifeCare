package com.example.lifecare.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifecare.R
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.data.ThemeManager

// warna utama sesuai desain
val LifeBlue = Color(0xFF33A1E0)
val LifeGreen = Color(0xFF98CD00)
private val LightGreyBg = Color(0xFFF5F5F5)

enum class ProfileMenu {
    NONE, EDIT_PROFILE, PIN, ACCOUNT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onChangePIN: () -> Unit = {},
    onThemeToggle: () -> Unit = {},
    onShowStatistic: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onShowAccount: () -> Unit = {}
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    var currentThemeMode by remember { mutableStateOf(themeManager.getThemeMode()) }

    var showChangePINDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showHardClearDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var activeMenu by remember { mutableStateOf(ProfileMenu.NONE) }

    val userData = healthDataManager.getUserData()
    var userFullName by remember { mutableStateOf(userData?.fullName ?: "Pengguna LifeCare") }
    var userEmail by remember { mutableStateOf(userData?.email ?: "you@example.com") }
    var userAge by remember { mutableStateOf(userData?.age ?: "") }
    var userGender by remember { mutableStateOf(userData?.gender ?: "") }

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
            // top bar: back di kiri, logo di kanan (kecil, tidak mengubah tinggi app bar)
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = LifeBlue
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "LifeCare Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(28.dp)
                            .padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // ====== AVATAR + NAMA (dengan foto profil) ======
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile photo with edit button
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    // Profile photo or default avatar
                    if (profilePhotoUri != null) {
                        AsyncImage(
                            model = profilePhotoUri,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E5E5))
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E5E5))
                                .clickable { photoPickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Avatar",
                                modifier = Modifier.size(70.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    // Camera icon button
                    FloatingActionButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier.size(40.dp),
                        containerColor = LifeGreen,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ubah Foto",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = userFullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LifeBlue
                )
                Text(
                    text = userEmail,
                    fontSize = 12.sp,
                    color = LifeGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ====== JUDUL SETTINGS ======
            Text(
                text = "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LifeBlue,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ====== MENU LIST (4 tombol putih dengan border biru) ======
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsMenuItem(
                    label = "Edit Profile",
                    icon = Icons.Default.Edit,
                    onClick = onEditProfile
                )

                SettingsMenuItem(
                    label = "My Statistic",
                    icon = Icons.Default.InsertChart,
                    onClick = onShowStatistic
                )

                SettingsMenuItem(
                    label = "PIN",
                    icon = Icons.Default.Dialpad,
                    onClick = {
                        activeMenu = ProfileMenu.PIN
                        onChangePIN()
                    }
                )

                SettingsMenuItem(
                    label = "Account",
                    icon = Icons.Default.ManageAccounts,
                    onClick = onShowAccount
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LifeCare v1.0",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // ====== DIALOG CLEAR DATA (tanpa logout) ======
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B)
                )
            },
            title = { Text("Hapus Semua Data?") },
            text = {
                Text(
                    "Tindakan ini akan menghapus semua data kesehatan Anda secara permanen dan tidak dapat dikembalikan."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        healthDataManager.clearAllData()
                        Toast.makeText(context, "Semua data berhasil dihapus", Toast.LENGTH_SHORT)
                            .show()
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

    // ====== DIALOG HARD CLEAR (hapus data + logout) ======
    if (showHardClearDialog) {
        AlertDialog(
            onDismissRequest = { showHardClearDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF6B6B),
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text("Hapus Semua Data?", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "⚠️ PERINGATAN: Tindakan ini akan:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Menghapus akun Anda")
                    Text("• Menghapus SEMUA data kesehatan")
                    Text("• Menghapus PIN keamanan")
                    Text("• Mengeluarkan Anda dari aplikasi")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data yang dihapus TIDAK DAPAT dikembalikan!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showHardClearDialog = false
                        healthDataManager.clearAllData()
                        Toast.makeText(
                            context,
                            "Semua data berhasil dihapus",
                            Toast.LENGTH_LONG
                        ).show()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFFF6B6B))
                ) {
                    Text("Hapus Semua Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHardClearDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ====== DIALOG LOGOUT ======
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = LifeBlue
                )
            },
            title = { Text("Keluar dari Akun?") },
            text = {
                Text(
                    "Anda akan keluar dari aplikasi dan harus login kembali untuk mengakses data kesehatan. Data Anda akan tetap tersimpan."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(LifeBlue)
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
}

/* ===================== MENU ITEM (BUTTON PUTIH) ===================== */

@Composable
private fun SettingsMenuItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // ICON BOX (tetap biru)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LifeBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = LifeBlue
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LifeBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/* ===================== BAGIAN LAIN (STATISTIC, ACCOUNT, DIALOG) ===================== */
/* Di bawah ini isinya sama seperti di file kamu sebelumnya, jadi tidak mengubah fungsi */

@Composable
fun StatisticSection(healthDataManager: HealthDataManager) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "My Statistic",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748)
        )
        Spacer(modifier = Modifier.height(12.dp))

        StatisticItem(
            label = "Total Data Berat & Tinggi",
            count = healthDataManager.getBodyMetricsList().size
        )
        Spacer(modifier = Modifier.height(8.dp))

        StatisticItem(
            label = "Total Data Tekanan Darah",
            count = healthDataManager.getBloodPressureList().size
        )
        Spacer(modifier = Modifier.height(8.dp))

        StatisticItem(
            label = "Total Data Gula Darah",
            count = healthDataManager.getBloodSugarList().size
        )
        Spacer(modifier = Modifier.height(8.dp))

        StatisticItem(
            label = "Total Aktivitas Fisik",
            count = healthDataManager.getPhysicalActivityList().size
        )
        Spacer(modifier = Modifier.height(8.dp))

        StatisticItem(
            label = "Total Asupan Makanan",
            count = healthDataManager.getFoodIntakeList().size
        )
    }
}

@Composable
fun StatisticItem(
    label: String,
    count: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$count data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE91E63)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat detail data",
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = "Lihat data",
                    tint = Color(0xFFE91E63),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AccountSection(
    userAge: String,
    userGender: String,
    userEmail: String,
    themeManager: ThemeManager,
    onThemeToggle: () -> Unit,
    onShowClearData: () -> Unit,
    onShowLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            "Account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3748)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                UserInfoItem(
                    "Umur",
                    if (userAge.isNotEmpty()) "$userAge tahun" else "Belum diisi"
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                UserInfoItem(
                    "Jenis Kelamin",
                    userGender.ifEmpty { "Belum diisi" }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                UserInfoItem("Email", userEmail)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingItem(
                    icon = Icons.Default.DarkMode,
                    title = "Tema Aplikasi",
                    subtitle = "Saat ini: ${themeManager.getThemeDisplayName()}",
                    onClick = onThemeToggle
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingItem(
                    icon = Icons.Default.Delete,
                    title = "Hapus Semua Data",
                    subtitle = "Hapus seluruh data kesehatan",
                    iconColor = Color(0xFFFF6B6B),
                    onClick = onShowClearData
                )
            }
        }
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
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3748)
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = LifeBlue,
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}