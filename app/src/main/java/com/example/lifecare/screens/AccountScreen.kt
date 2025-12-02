package com.example.lifecare.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.data.ThemeManager
import com.example.lifecare.ui.theme.HealthColors

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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = HealthColors.NeonGreen
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
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            // Icon and Title Section with NeonGreen circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(HealthColors.NeonGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pengaturan Akun",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kelola informasi akun, tema, dan data aplikasi anda",
                fontSize = 14.sp,
                color = HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Informasi Akun Section =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Informasi Akun",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HealthColors.NeonGreen,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Email card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Email: ${if (userEmail.isNotEmpty()) userEmail else "example@gmail.com"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Age card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Umur: ${if (userAge.isNotEmpty()) "$userAge Tahun" else "22 Tahun"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gender card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Jenis Kelamin: ${if (userGender.isNotEmpty()) userGender else "Female"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ===== Pengaturan Akun Section =====
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Pengaturan Akun",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Tema Aplikasi Button
                AccountActionButton(
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

                Spacer(modifier = Modifier.height(12.dp))

                // Hapus Semua Data Button
                AccountActionButton(
                    icon = Icons.Default.Delete,
                    title = "Hapus Semua Data",
                    subtitle = "Hapus seluruh data kesehatan",
                    onClick = { showClearDataDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hapus Data & Logout Button
                AccountActionButton(
                    icon = Icons.Default.Warning,
                    title = "Hapus Data & Logout",
                    subtitle = "Hapus semua data dan keluar dari akun",
                    onClick = { showHardClearDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Keluar dari Akun Button
                AccountActionButton(
                    icon = Icons.Default.ExitToApp,
                    title = "Keluar dari Akun",
                    subtitle = "Akun tetap tersimpan, hanya Logout",
                    onClick = { showLogoutDialog = true }
                )
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
                    tint = HealthColors.NeonGreen
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
                    colors = ButtonDefaults.buttonColors(HealthColors.NeonGreen)
                ) {
                    Text("Keluar", color = Color.White)
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

// ===== AccountActionButton Composable =====
@Composable
fun AccountActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HealthColors.NeonGreen)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon box with white background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title and subtitle
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Right arrow icon
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
