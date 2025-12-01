package com.example.lifecare

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val LifeCareBlue = Color(0xFF33A1E0)
private val LifeCareGreen = Color(0xFF98CD00)

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
    var isGoalsExpanded by remember { mutableStateOf(false) }
    var isWeeklyStatsExpanded by remember { mutableStateOf(false) }

    // Back button handler
    BackHandler(enabled = currentScreen != null) {
        when (currentScreen) {
            "edit_profile", "profile_statistic", "change_pin", "account" -> {
                currentScreen = "profile"
            }
            "run_tracking_permission", "run_tracking_setup", "live_run", "run_summary" -> {
                currentScreen = "physical_activity"
            }
            "profile", "health_records", "physical_activity" -> {
                currentScreen = null
                selectedBottomNav = "home"
            }
            else -> {
                currentScreen = null
                selectedBottomNav = "home"
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { currentScreen = "health_metrics" },
                modifier = Modifier.offset(y = 48.dp),
                shape = CircleShape,
                containerColor = LifeCareGreen,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Data")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
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
                        onPermissionGranted = { currentScreen = "run_tracking_setup" },
                        onNavigateBack = { currentScreen = "physical_activity" }
                    )
                }
                "run_tracking_setup" -> {
                    RunTrackingSetupScreen(
                        viewModel = runTrackingViewModel,
                        onStartRun = { currentScreen = "live_run" },
                        onNavigateBack = { currentScreen = "physical_activity" }
                    )
                }
                "live_run" -> {
                    LiveRunScreen(
                        viewModel = runTrackingViewModel,
                        onFinishRun = { currentScreen = "run_summary" },
                        onDiscardRun = { currentScreen = "physical_activity" }
                    )
                }
                "run_summary" -> {
                    RunSummaryScreen(
                        viewModel = runTrackingViewModel,
                        onSaveAndExit = { currentScreen = "physical_activity" },
                        onDiscardAndExit = { currentScreen = "physical_activity" }
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
                            .background(Color.White)
                    ) {
                        // Top Header
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = "LifeCare Logo",
                                        modifier = Modifier
                                            .height(25.dp),
                                        contentScale = ContentScale.FillHeight
                                    )
                                }

                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = Color(0xFF33A1E0).copy(alpha = 0.08f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Profile",
                                            tint = Color(0xFF33A1E0)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 20.dp
                                )
                        ) {
                            // Greeting
                            val userName = healthDataManager.getUserData()?.fullName ?: "User"

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Halo, $userName",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LifeCareBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Calculate today's health metrics for goals and tips
                            val todaySteps = healthDataManager.getTodayTotalSteps()
                            val todayCalories = healthDataManager.getTodayTotalCaloriesIntake()
                            val todayExercise = healthDataManager.getTodayTotalExerciseMinutes()

                            // Latest Records Card
                            val latestBP = healthDataManager.getLatestBloodPressure()
                            val latestBS = healthDataManager.getLatestBloodSugar()
                            val latestBM = healthDataManager.getLatestBodyMetrics()

// format nilai
                            val latestBpValue =
                                latestBP?.let { "${it.systolic}/${it.diastolic} mmHg" }
                            val latestBsValue =
                                latestBS?.let { "${it.level} mg/dL" }
                            val latestBmiValue =
                                latestBM?.let { String.format("%.1f", it.bmi) } // tetap pakai BMI yang sudah ada

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { currentScreen = "health_metrics" },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, Color(0xFFEFEFEF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Data Kesehatan Terbaru",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LifeCareBlue
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 3 kolom: Berat Badan (BMI), Tekanan Darah, Gula Darah
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LatestSummaryItem(
                                            label = "Berat Badan",
                                            value = latestBmiValue,              // atau ganti sendiri ke berat kalau ada field-nya
                                            icon = Icons.Default.MonitorWeight,
                                            iconColor = Color(0xFF2196F3)
                                        )

                                        LatestSummaryItem(
                                            label = "Tekanan Darah",
                                            value = latestBpValue,
                                            icon = Icons.Default.Favorite,
                                            iconColor = Color(0xFFE91E63)
                                        )

                                        LatestSummaryItem(
                                            label = "Gula Darah",
                                            value = latestBsValue,
                                            icon = Icons.Default.WaterDrop,
                                            iconColor = Color(0xFF9C27B0)
                                        )
                                    }
                                }
                            }

                            // deretan tanggal minggu ini di bawah card
                            Spacer(modifier = Modifier.height(12.dp))

                            WeeklyDateRow()

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Monitoring Section
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Monitoring Kesehatan",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LifeCareBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Grid of health categories (3 kartu atas, 2 kartu bawah rata kiri)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                // Baris pertama: 3 kartu
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    HealthCategoryCard(
                                        title = "Data\nKesehatan",
                                        icon = Icons.Default.MonitorHeart,
                                        backgroundColor = Color(0xFF33A1E0),
                                        iconColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "health_metrics" }
                                    )
                                    HealthCategoryCard(
                                        title = "Aktivitas\nFisik",
                                        icon = Icons.Default.DirectionsRun,
                                        backgroundColor = LifeCareGreen,
                                        iconColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "physical_activity" }
                                    )
                                    HealthCategoryCard(
                                        title = "Asupan\nMakanan",
                                        icon = Icons.Default.Restaurant,
                                        backgroundColor = Color(0xFFFFE100),
                                        iconColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "food_intake" }
                                    )
                                }

                                // Baris kedua: 2 kartu, rata kiri
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    HealthCategoryCard(
                                        title = "Grafik\nKesehatan",
                                        icon = Icons.Default.ShowChart,
                                        backgroundColor = Color(0xFF00809D),
                                        iconColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "charts" }
                                    )
                                    HealthCategoryCard(
                                        title = "Pengingat\nKesehatan",
                                        icon = Icons.Default.Notifications,
                                        backgroundColor = Color(0xFF9C27B0),
                                        iconColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        onClick = { currentScreen = "reminders" }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Health Summary Today
                            Text(
                                "Ringkasan Hari Ini",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF33A1E0)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFE8F5F3
                                        )
                                    ),
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
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFFFF3E0
                                        )
                                    ),
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
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFE8F5E9
                                        )
                                    ),
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

                            Spacer(modifier = Modifier.height(16.dp))

                            // Health Goals & Progress (expandable card)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isGoalsExpanded = !isGoalsExpanded },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(1.dp, Color(0xFFEFEFEF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Target Kesehatan Hari Ini",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LifeCareBlue
                                        )
                                        Icon(
                                            imageVector = if (isGoalsExpanded)
                                                Icons.Default.KeyboardArrowUp
                                            else
                                                Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (isGoalsExpanded) "Sembunyikan" else "Tampilkan",
                                            tint = LifeCareBlue
                                        )
                                    }

                                    // Isi card muncul hanya jika expanded
                                    if (isGoalsExpanded) {
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
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Weekly Summary (expandable)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isWeeklyStatsExpanded = !isWeeklyStatsExpanded },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(1.dp, Color(0xFFEFEFEF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                ) {
                                    // Header seperti gambar: judul hijau + panah
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Statistik Minggu Ini",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LifeCareGreen      // hijau 0xFF98CD00
                                        )
                                        Icon(
                                            imageVector = if (isWeeklyStatsExpanded)
                                                Icons.Default.KeyboardArrowUp
                                            else
                                                Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (isWeeklyStatsExpanded) "Sembunyikan" else "Tampilkan",
                                            tint = LifeCareGreen
                                        )
                                    }

                                    // Isi muncul hanya kalau expanded
                                    if (isWeeklyStatsExpanded) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Hitung statistik minggu ini
                                        val bpCount =
                                            healthDataManager.getBloodPressureList().count {
                                                isThisWeek(it.timestamp)
                                            }
                                        val bsCount = healthDataManager.getBloodSugarList().count {
                                            isThisWeek(it.timestamp)
                                        }
                                        val activityCount =
                                            healthDataManager.getPhysicalActivityList().count {
                                                isThisWeek(it.timestamp)
                                            }
                                        val foodCount =
                                            healthDataManager.getFoodIntakeList().count {
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
                            }
                        }
                    }
                }
            }
        }
    }
}

private class CustomNotchedBottomBarShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val fabSize = with(density) { 56.dp.toPx() }
        val fabRadius = fabSize / 2f
        val notchRadius = fabRadius + with(density) { 12.dp.toPx() }
        val cornerRadius = with(density) { 32.dp.toPx() }

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, 0f, size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        }

        val cutoutPath = Path().apply {
            addOval(
                Rect(
                    center = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                    radius = notchRadius
                )
            )
        }

        return Outline.Generic(
            Path.combine(
                operation = PathOperation.Difference,
                path1 = path,
                path2 = cutoutPath
            )
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val shape = remember { CustomNotchedBottomBarShape() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp), // Height to contain the bar and give space
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp)
                .border(1.5.dp, LifeCareBlue, shape),
            shape = shape,
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    BottomNavItem(
                        icon = Icons.Default.Home,
                        selected = selectedItem == "home",
                        onClick = { onItemSelected("home") }
                    )
                    BottomNavItem(
                        icon = Icons.Default.DirectionsRun,
                        selected = selectedItem == "activity",
                        onClick = { onItemSelected("activity") }
                    )
                }
                Spacer(modifier = Modifier.width(64.dp)) // Spacer for FAB
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    BottomNavItem(
                        icon = Icons.Default.CalendarToday,
                        selected = selectedItem == "calendar",
                        onClick = { onItemSelected("calendar") }
                    )
                    BottomNavItem(
                        icon = Icons.Default.Person,
                        selected = selectedItem == "profile",
                        onClick = { onItemSelected("profile") }
                    )
                }
            }
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
            .clip(RoundedCornerShape(16.dp))
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
            else Color(0xFF757575) // Abu-abu saat tidak selected
        )
    }
}

// Composable lainnya (ProgressGoal, WeeklyStatItem, dll.) tetap sama ...
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
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFF1F3F5)
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
fun HealthCategoryCard(
    title: String,
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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

            Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
        }
    }
}

@Composable
private fun LatestSummaryItem(
    label: String,
    value: String?,
    icon: ImageVector,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3748)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value ?: "-",
            fontSize = 13.sp,
            fontWeight = if (label == "Tekanan Darah" && value != null) FontWeight.Bold else FontWeight.Medium,
            color = when {
                label == "Tekanan Darah" && value != null -> Color(0xFFD50000)
                value == null -> Color(0xFFADB5BD)
                else -> Color(0xFF2D3748)
            }
        )
    }
}

@Composable
private fun WeeklyDateRow() {
    // Format nama hari & tanggal
    val localeEn = Locale("en") // biar Sun, Mon, Tue...
    val dayFormat = remember { SimpleDateFormat("EEE", localeEn) }   // Sun
    val dateFormat = remember { SimpleDateFormat("dd", localeEn) }   // 01

    val today = Calendar.getInstance()
    val startOfWeek = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.SUNDAY
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val cal = startOfWeek.clone() as Calendar

        repeat(7) {
            val dayName = dayFormat.format(cal.time)    // Sun, Mon, ...
            val dayNumber = dateFormat.format(cal.time) // 01, 02, ...
            val isToday =
                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

            // HANYA hari ini yang berwarna biru, lainnya abu-abu
            val backgroundColor = if (isToday) LifeCareBlue else Color(0xFFE0E0E0)
            val textColor = if (isToday) Color.White else Color.White
            val dotColor = if (isToday) Color.White else Color(0xFFBDBDBD)

            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(23.dp))          // bentuk oval
                    .background(backgroundColor),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp, bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // titik kecil di atas
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Hari + tanggal
                    Text(
                        text = "$dayName\n$dayNumber",   // Sun\n01
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }

            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}