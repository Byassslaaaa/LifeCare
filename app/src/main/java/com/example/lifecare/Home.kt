package com.example.lifecare

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifecare.charts.HealthChartsScreen
import com.example.lifecare.data.HealthDataManager
import com.example.lifecare.reminder.ReminderSettingsScreen
import com.example.lifecare.repository.RunRepository
import com.example.lifecare.screens.*
import com.example.lifecare.ui.screens.*
import com.example.lifecare.utils.PermissionHelper
import com.example.lifecare.viewmodel.RunTrackingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogoutClick: () -> Unit,
    onThemeToggle: () -> Unit = {}
) {
    val context = LocalContext.current
    val healthDataManager = remember { HealthDataManager(context) }

    // GPS Tracking ViewModel
    val runRepository = remember { RunRepository(healthDataManager) }
    val runTrackingViewModel = remember { RunTrackingViewModel(context.applicationContext as android.app.Application, runRepository) }

    var currentScreen by remember { mutableStateOf<String?>(null) }
    var selectedBottomNav by remember { mutableStateOf("home") }

    // ============ BACK BUTTON HANDLER ============
    BackHandler(enabled = currentScreen != null) {
        when (currentScreen) {
            // Sub-screens dari physical activity yang perlu kembali ke physical activity
            "run_tracking_permission", "run_tracking_setup" -> {
                currentScreen = "physical_activity"
            }
            "live_run", "run_summary" -> {
                // Dari live run/summary, kembali ke physical activity
                currentScreen = "physical_activity"
            }
            // Edit profile & change PIN kembali ke profile
            "edit_profile", "profile_statistic", "change_pin", "account" -> {
                currentScreen = "profile"
            }
            // Semua screen lain kembali ke home
            else -> {
                currentScreen = null
                selectedBottomNav = "home"
            }
        }
    }

    // Persistent Scaffold with bottom navigation and FAB
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedBottomNav,
                onItemSelected = { item ->
                    selectedBottomNav = item
                    when (item) {
                        "home" -> currentScreen = null
                        "activity" -> currentScreen = "physical_activity"
                        "calendar" -> currentScreen = "health_records"
                        "profile" -> currentScreen = "profile"
                    }
                }
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.offset(y = 32.dp) // Turunkan FAB ke dalam notch
            ) {
                FloatingActionButton(
                    onClick = {
                        // Show bottom sheet atau dialog untuk quick add data
                        currentScreen = "health_metrics"
                    },
                    containerColor = Color(0xFF8BC34A), // Hijau lime
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Data",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            when (currentScreen) {
                "health_metrics" -> {
                    UnifiedHealthMetricsScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null }
                    )
                }
                "physical_activity" -> {
                    PhysicalActivityScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null },
                        onStartGPSTracking = {
                            // Check permission first
                            if (PermissionHelper.hasLocationPermission(context)) {
                                currentScreen = "run_tracking_setup"
                            } else {
                                currentScreen = "run_tracking_permission"
                            }
                        }
                    )
                }
                "run_tracking_permission" -> {
                    RunTrackingPermissionScreen(
                        onPermissionGranted = {
                            currentScreen = "run_tracking_setup"
                        },
                        onNavigateBack = {
                            currentScreen = "physical_activity"
                        }
                    )
                }
                "run_tracking_setup" -> {
                    RunTrackingSetupScreen(
                        viewModel = runTrackingViewModel,
                        onStartRun = {
                            currentScreen = "live_run"
                        },
                        onNavigateBack = {
                            currentScreen = "physical_activity"
                        }
                    )
                }
                "live_run" -> {
                    LiveRunScreen(
                        viewModel = runTrackingViewModel,
                        onFinishRun = {
                            currentScreen = "run_summary"
                        },
                        onDiscardRun = {
                            currentScreen = "physical_activity"
                        }
                    )
                }
                "run_summary" -> {
                    RunSummaryScreen(
                        viewModel = runTrackingViewModel,
                        onSaveAndExit = {
                            currentScreen = "physical_activity"
                        },
                        onDiscardAndExit = {
                            currentScreen = "physical_activity"
                        }
                    )
                }
                "food_intake" -> {
                    FoodIntakeScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null }
                    )
                }
                "health_records" -> {
                    HealthRecordsScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null }
                    )
                }
                "profile" -> {
                    ProfileScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null },
                        onLogout = onLogoutClick,
                        onChangePIN = { currentScreen = "change_pin" },
                        onThemeToggle = onThemeToggle,
                        onShowStatistic = { currentScreen = "profile_statistic" },
                        onEditProfile = { currentScreen = "edit_profile" },
                        onShowAccount = { currentScreen = "account" }
                    )
                }
                "edit_profile" -> {
                    EditProfileScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = "profile" },
                        onProfileSaved = { currentScreen = "profile" }
                    )
                }
                "profile_statistic" -> {
                    ProfileStatisticScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = "profile" }
                    )
                }
                "change_pin" -> {
                    ChangePINScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = "profile" },
                        onPINChanged = { currentScreen = "profile" }
                    )
                }
                "account" -> {
                    AccountScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = "profile" },
                        onLogout = onLogoutClick,
                        onThemeToggle = onThemeToggle
                    )
                }
                "charts" -> {
                    HealthChartsScreen(
                        healthDataManager = healthDataManager,
                        onBackClick = { currentScreen = null }
                    )
                }
                "reminders" -> {
                    ReminderSettingsScreen(
                        onBackClick = { currentScreen = null }
                    )
                }
                else -> {
                    // Main Dashboard
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // Top Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.LocalHospital,
                                        contentDescription = "LifeCare Logo",
                                        tint = Color(0xFF5DCCB4),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "LifeCare",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        "Tracking Kesehatan Pribadi",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp)
                        ) {
                            // Greeting
                            val dateFormat = SimpleDateFormat("EEEE, d MMM", Locale("id", "ID"))
                            Text(
                                dateFormat.format(Date()),
                                fontSize = 14.sp,
                                color = Color(0xFFADB5BD)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Hi, Selamat Datang",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D3748)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Calculate today's health metrics for goals and tips
                            val todaySteps = healthDataManager.getTodayTotalSteps()
                            val todayCalories = healthDataManager.getTodayTotalCaloriesIntake()
                            val todayExercise = healthDataManager.getTodayTotalExerciseMinutes()

                            // Latest Records Card
                            val latestBP = healthDataManager.getLatestBloodPressure()
                            val latestBS = healthDataManager.getLatestBloodSugar()
                            val latestBM = healthDataManager.getLatestBodyMetrics()

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        "Data Kesehatan Terbaru",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Blood Pressure
                                    if (latestBP != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFFCE4EC))
                                                .clickable { currentScreen = "health_metrics" }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.Favorite,
                                                        contentDescription = null,
                                                        tint = Color(0xFFE91E63),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        "Tekanan Darah",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF2D3748)
                                                    )
                                                    Text(
                                                        "${latestBP.systolic}/${latestBP.diastolic} mmHg",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFE91E63)
                                                    )
                                                }
                                            }
                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFFADB5BD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    // Blood Sugar
                                    if (latestBS != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFF3E5F5))
                                                .clickable { currentScreen = "health_metrics" }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.WaterDrop,
                                                        contentDescription = null,
                                                        tint = Color(0xFF9C27B0),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        "Gula Darah",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF2D3748)
                                                    )
                                                    Text(
                                                        "${latestBS.level} mg/dL",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF9C27B0)
                                                    )
                                                }
                                            }
                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFFADB5BD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    // Body Metrics
                                    if (latestBM != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFE3F2FD))
                                                .clickable { currentScreen = "health_metrics" }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.MonitorWeight,
                                                        contentDescription = null,
                                                        tint = Color(0xFF2196F3),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        "BMI",
                                                        fontSize = 12.sp,
                                                        color = Color(0xFF2D3748)
                                                    )
                                                    Text(
                                                        String.format("%.1f", latestBM.bmi),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF2196F3)
                                                    )
                                                }
                                            }
                                            Icon(
                                                Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color(0xFFADB5BD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // If no data
                                    if (latestBP == null && latestBS == null && latestBM == null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFF8F9FA))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFFADB5BD),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Belum ada data. Mulai catat kesehatan Anda!",
                                                fontSize = 14.sp,
                                                color = Color(0xFFADB5BD)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Goals & Progress
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        "Target Kesehatan Hari Ini",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Steps Goal
                                    val stepsGoal = 10000
                                    ProgressGoal(
                                        label = "Langkah",
                                        current = todaySteps,
                                        goal = stepsGoal,
                                        icon = Icons.Default.DirectionsWalk,
                                        color = Color(0xFF5DCCB4),
                                        unit = "langkah"
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Calories Goal
                                    val caloriesGoal = 2000
                                    ProgressGoal(
                                        label = "Kalori",
                                        current = todayCalories,
                                        goal = caloriesGoal,
                                        icon = Icons.Default.Restaurant,
                                        color = Color(0xFFFF9800),
                                        unit = "kal"
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Exercise Goal
                                    val exerciseGoal = 30
                                    ProgressGoal(
                                        label = "Olahraga",
                                        current = todayExercise,
                                        goal = exerciseGoal,
                                        icon = Icons.Default.FitnessCenter,
                                        color = Color(0xFF4CAF50),
                                        unit = "menit"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Summary Today
                            Text(
                                "Ringkasan Hari Ini",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5F3)),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.DirectionsWalk,
                                            contentDescription = null,
                                            tint = Color(0xFF5DCCB4),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "${healthDataManager.getTodayTotalSteps()}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Text(
                                            "Langkah",
                                            fontSize = 12.sp,
                                            color = Color(0xFFADB5BD)
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Restaurant,
                                            contentDescription = null,
                                            tint = Color(0xFFFF9800),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "${healthDataManager.getTodayTotalCaloriesIntake()}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Text(
                                            "Kalori",
                                            fontSize = 12.sp,
                                            color = Color(0xFFADB5BD)
                                        )
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "${healthDataManager.getTodayTotalExerciseMinutes()}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Text(
                                            "Menit",
                                            fontSize = 12.sp,
                                            color = Color(0xFFADB5BD)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Monitoring Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Monitoring Kesehatan",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grid of health categories
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    HealthCategoryCard(
                                        title = "Data\nKesehatan",
                                        subtitle = "Tekanan darah, gula darah, berat badan",
                                        icon = Icons.Default.MonitorHeart,
                                        backgroundColor = Color(0xFFE8F5F3),
                                        iconColor = Color(0xFF5DCCB4),
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "health_metrics" }
                                    )
                                    HealthCategoryCard(
                                        title = "Aktivitas\nFisik",
                                        subtitle = "Catat olahraga",
                                        icon = Icons.Default.DirectionsRun,
                                        backgroundColor = Color(0xFFE8F5E9),
                                        iconColor = Color(0xFF4CAF50),
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "physical_activity" }
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    HealthCategoryCard(
                                        title = "Grafik\nKesehatan",
                                        subtitle = "Visualisasi data kesehatan",
                                        icon = Icons.Default.ShowChart,
                                        backgroundColor = Color(0xFFFFF3E0),
                                        iconColor = Color(0xFFFF9800),
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "charts" }
                                    )
                                    HealthCategoryCard(
                                        title = "Pengingat\nKesehatan",
                                        subtitle = "Atur reminder harian",
                                        icon = Icons.Default.Notifications,
                                        backgroundColor = Color(0xFFF3E5F5),
                                        iconColor = Color(0xFF9C27B0),
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "reminders" }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Food Intake Summary
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentScreen = "food_intake" },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFFFF3E0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Restaurant,
                                                contentDescription = null,
                                                tint = Color(0xFFFF9800),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column {
                                            Text(
                                                "Asupan Makanan",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2D3748)
                                            )
                                            Text(
                                                "${healthDataManager.getTodayTotalCaloriesIntake()} kalori hari ini",
                                                fontSize = 12.sp,
                                                color = Color(0xFFADB5BD)
                                            )
                                        }
                                    }
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFFADB5BD)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Weekly Summary
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Statistik Minggu Ini",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Icon(
                                            Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            tint = Color(0xFF5DCCB4),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Get weekly stats
                                    val bpCount = healthDataManager.getBloodPressureList().count {
                                        isThisWeek(it.timestamp)
                                    }
                                    val bsCount = healthDataManager.getBloodSugarList().count {
                                        isThisWeek(it.timestamp)
                                    }
                                    val activityCount = healthDataManager.getPhysicalActivityList().count {
                                        isThisWeek(it.timestamp)
                                    }
                                    val foodCount = healthDataManager.getFoodIntakeList().count {
                                        isThisWeek(it.timestamp)
                                    }

                                    WeeklyStatItem(
                                        label = "Tekanan Darah",
                                        count = bpCount,
                                        icon = Icons.Default.Favorite,
                                        color = Color(0xFFE91E63)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeeklyStatItem(
                                        label = "Gula Darah",
                                        count = bsCount,
                                        icon = Icons.Default.WaterDrop,
                                        color = Color(0xFF9C27B0)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeeklyStatItem(
                                        label = "Aktivitas Fisik",
                                        count = activityCount,
                                        icon = Icons.Default.DirectionsRun,
                                        color = Color(0xFF4CAF50)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeeklyStatItem(
                                        label = "Asupan Makanan",
                                        count = foodCount,
                                        icon = Icons.Default.Restaurant,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Insights
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5F3)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = Color(0xFF5DCCB4),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column {
                                        Text(
                                            "Tips Kesehatan",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2D3748)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            getHealthTip(todaySteps, todayCalories, todayExercise),
                                            fontSize = 12.sp,
                                            color = Color(0xFF4A5568),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp)) // Extra space
                        }
                    }
                }
            }
        }
    }
}

fun isThisWeek(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance()
    val weekStart = calendar.apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    return timestamp >= weekStart
}

fun getHealthTip(steps: Int, calories: Int, exercise: Int): String {
    return when {
        steps < 5000 && exercise < 15 -> "Tingkatkan aktivitas fisik Anda! Target minimal 10.000 langkah per hari."
        calories > 2500 -> "Perhatikan asupan kalori Anda. Pertimbangkan untuk mengurangi porsi makan."
        exercise >= 30 && steps >= 8000 -> "Luar biasa! Pertahankan gaya hidup aktif Anda."
        steps >= 10000 -> "Hebat! Anda telah mencapai target langkah harian."
        calories < 1200 -> "Pastikan Anda mendapat nutrisi yang cukup. Konsultasikan dengan ahli gizi jika perlu."
        else -> "Jaga pola makan seimbang dan tetap aktif bergerak setiap hari!"
    }
}

@Composable
fun ProgressGoal(
    label: String,
    current: Int,
    goal: Int,
    icon: ImageVector,
    color: Color,
    unit: String
) {
    val progress = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3748)
                )
            }
            Text(
                "$current / $goal $unit",
                fontSize = 12.sp,
                color = Color(0xFFADB5BD)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF1F3F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "$percentage% tercapai",
            fontSize = 11.sp,
            color = if (progress >= 1f) color else Color(0xFFADB5BD),
            fontWeight = if (progress >= 1f) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun WeeklyStatItem(
    label: String,
    count: Int,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                label,
                fontSize = 14.sp,
                color = Color(0xFF2D3748)
            )
        }
        Text(
            "$count kali",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF2D3748),
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun HealthCategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFFADB5BD),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        // Background dengan notch kecil di tengah
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            val path = Path().apply {
                val centerX = size.width / 2f
                val notchRadius = 30.dp.toPx() // Radius notch yang lebih kecil
                val notchHeight = 16.dp.toPx() // Tinggi notch yang terbatas
                val cornerRadius = 24.dp.toPx()

                // Mulai dari kiri atas dengan rounded corner
                moveTo(0f, cornerRadius)
                quadraticBezierTo(0f, 0f, cornerRadius, 0f)

                // Garis ke sebelum notch
                lineTo(centerX - notchRadius - 18.dp.toPx(), 0f)

                // Kurva transisi masuk (kiri) - smooth
                cubicTo(
                    centerX - notchRadius - 8.dp.toPx(), 0f,
                    centerX - notchRadius, 0f,
                    centerX - notchRadius + 4.dp.toPx(), notchHeight * 0.4f
                )

                // Arc setengah lingkaran terbatas (hanya bagian atas)
                arcTo(
                    rect = Rect(
                        left = centerX - notchRadius + 4.dp.toPx(),
                        top = 0f,
                        right = centerX + notchRadius - 4.dp.toPx(),
                        bottom = notchHeight * 2
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )

                // Kurva transisi keluar (kanan) - smooth
                cubicTo(
                    centerX + notchRadius, 0f,
                    centerX + notchRadius + 8.dp.toPx(), 0f,
                    centerX + notchRadius + 18.dp.toPx(), 0f
                )

                // Garis ke kanan atas dengan rounded corner
                lineTo(size.width - cornerRadius, 0f)
                quadraticBezierTo(size.width, 0f, size.width, cornerRadius)

                // Sisi kanan, bawah, kiri
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = Color.White
            )

            // Shadow
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.08f),
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        // Navigation items overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                icon = Icons.Default.Home,
                selected = selectedItem == "home",
                onClick = { onItemSelected("home") }
            )

            // Activity
            BottomNavItem(
                icon = Icons.Default.DirectionsRun,
                selected = selectedItem == "activity",
                onClick = { onItemSelected("activity") }
            )

            // Spacer untuk FAB di tengah
            Spacer(modifier = Modifier.width(56.dp))

            // Calendar
            BottomNavItem(
                icon = Icons.Default.CalendarToday,
                selected = selectedItem == "calendar",
                onClick = { onItemSelected("calendar") }
            )

            // Profile
            BottomNavItem(
                icon = Icons.Default.Person,
                selected = selectedItem == "profile",
                onClick = { onItemSelected("profile") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) Color(0xFFB3E5FC) // Biru muda
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (selected) Color(0xFF0288D1) // Biru tua saat selected
            else Color(0xFF9E9E9E) // Abu-abu saat tidak selected
        )
    }
}
