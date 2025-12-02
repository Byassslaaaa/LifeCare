package com.example.lifecare.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.ui.theme.HealthColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileStatisticScreen(
    healthDataManager: HealthDataManager,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Statistic",
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

            // Icon + heading
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Statistik Kesehatan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ringkasan aktivitas dan pencatatan kesehatan Anda",
                fontSize = 14.sp,
                color = HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Statistics Cards
            StatisticCard(
                categoryName = "Berat & Tinggi Badan",
                count = healthDataManager.getBodyMetricsList().size
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticCard(
                categoryName = "Tekanan Darah",
                count = healthDataManager.getBloodPressureList().size
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticCard(
                categoryName = "Gula Darah",
                count = healthDataManager.getBloodSugarList().size
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticCard(
                categoryName = "Makanan",
                count = healthDataManager.getFoodIntakeList().size
            )

            Spacer(modifier = Modifier.height(12.dp))

            StatisticCard(
                categoryName = "Aktivitas Fisik",
                count = healthDataManager.getPhysicalActivityList().size
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card tips di bawah
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tips!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = HealthColors.NeonGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Pantau statistik kesehatan Anda secara berkala untuk melihat perkembangan, " +
                                "menjaga konsistensi, dan mendeteksi perubahan pola sejak dini.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun StatisticCard(
    categoryName: String,
    count: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category name
            Text(
                text = categoryName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Count
            Text(
                text = "$count data",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HealthColors.NeonGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            // View details link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat detail data",
                    fontSize = 14.sp,
                    color = HealthColors.NeonGreen
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}