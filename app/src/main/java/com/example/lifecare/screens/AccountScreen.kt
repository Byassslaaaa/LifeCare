package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.data.ThemeManager

private val AccountPrimary = Color(0xFF5DCCB4)
private val AccountBackground = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onThemeToggle: () -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }

    val userData = healthDataManager.getUserData()
    val userEmail = userData?.email ?: "you@example.com"
    val userAge = userData?.age ?: ""
    val userGender = userData?.gender ?: ""

    var showClearDataDialog by remember { mutableStateOf(false) }
    var showHardClearDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AccountPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AccountBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Icon + heading (mirip style PIN)
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = AccountPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Pengaturan Akun",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Kelola informasi akun, tema, dan data aplikasi Anda.",
                fontSize = 13.sp,
                color = Color(0xFF6C757D)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Card informasi akun =====
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Informasi Akun",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    UserInfoItem(
                        label = "Email",
                        value = userEmail
                    )
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    UserInfoItem(
                        label = "Umur",
                        value = if (userAge.isNotEmpty()) "$userAge tahun" else "Belum diisi"
                    )
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    UserInfoItem(
                        label = "Jenis Kelamin",
                        value = if (userGender.isNotEmpty()) userGender else "Belum diisi"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== Card pengaturan tema & data =====
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Pengaturan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D3748)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Ubah tema
                    SettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "Tema Aplikasi",
                        subtitle = "Saat ini: ${themeManager.getThemeDisplayName()}",
                        onClick = {
                            onThemeToggle()
                            Toast.makeText(
                                context,
                                "Tema diubah ke: ${themeManager.getThemeDisplayName()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Hapus semua data (tanpa logout)
                    SettingItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Hapus Semua Data",
                        subtitle = "Hapus seluruh data kesehatan",
                        iconColor = Color(0xFFFF6B6B),
                        onClick = { showClearDataDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Hapus semua data & logout
                    SettingItem(
                        icon = Icons.Default.Warning,
                        title = "Hapus Data & Logout",
                        subtitle = "Hapus semua data dan keluar dari akun",
                        iconColor = Color(0xFFFF6B6B),
                        onClick = { showHardClearDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Keluar dari akun saja
                    SettingItem(
                        icon = Icons.Default.ExitToApp,
                        title = "Keluar dari Akun",
                        subtitle = "Akun tetap tersimpan, hanya logout",
                        iconColor = LifeBlue,
                        onClick = { showLogoutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ===== Dialog hapus semua data =====
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
                        Toast.makeText(
                            context,
                            "Semua data berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
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

    // ===== Dialog hapus data & logout =====
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
                    Text("Hapus Data & Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHardClearDialog = false }) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // ===== Dialog logout saja =====
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
