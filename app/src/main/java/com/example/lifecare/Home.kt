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
import com.example.lifecare.ui.theme.HealthColors
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
                modifier = Modifier
                    .offset(y = 48.dp)
                    .size(64.dp), // Larger FAB sesuai wireframe
                shape = CircleShape,
                containerColor = HealthColors.NeonGreen, // Neon green dari wireframe
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah Data",
                    modifier = Modifier.size(32.dp)
                )
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
                    // Main Dashboard - Exact match to reference images
                    HomeMainScreen(
                        healthDataManager = healthDataManager,
                        isGoalsExpanded = isGoalsExpanded,
                        isWeeklyStatsExpanded = isWeeklyStatsExpanded,
                        onGoalsExpandToggle = { isGoalsExpanded = !isGoalsExpanded },
                        onWeeklyStatsExpandToggle = { isWeeklyStatsExpanded = !isWeeklyStatsExpanded },
                        onNavigateToScreen = { screen -> currentScreen = screen }
                    )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side icons
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
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

                // Center spacer for FAB
                Spacer(modifier = Modifier.width(80.dp))

                // Right side icons
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
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
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (selected) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HomeMainScreen(
    healthDataManager: HealthDataManager,
    isGoalsExpanded: Boolean,
    isWeeklyStatsExpanded: Boolean,
    onGoalsExpandToggle: () -> Unit,
    onWeeklyStatsExpandToggle: () -> Unit,
    onNavigateToScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with Home title, search and notification icons
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Home",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp).clickable { }
                    )
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp).clickable { }
                    )
                }
            }
        }

        // Divider below header
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Section 1: Greeting & Daily Challenge Card
            val userName = healthDataManager.getUserData()?.fullName ?: "Yaya"

            // Calculate daily progress from all health data
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Count today's activities from all categories
            val todayBloodPressure = healthDataManager.getBloodPressureList().count { it.timestamp >= todayStart }
            val todayBloodSugar = healthDataManager.getBloodSugarList().count { it.timestamp >= todayStart }
            val todayPhysicalActivity = healthDataManager.getPhysicalActivityList().count { it.timestamp >= todayStart }
            val todayFoodIntake = healthDataManager.getFoodIntakeList().count { it.timestamp >= todayStart }
            val todayActivitiesCount = todayBloodPressure + todayBloodSugar + todayPhysicalActivity + todayFoodIntake

            val dailyGoal = 3 // Target: 3 aktivitas per hari
            val dailyProgress = (todayActivitiesCount.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)

            GreetingWithChallengeCard(
                userName = userName,
                dailyProgress = dailyProgress
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Weekly Calendar
            WeeklyCalendar()

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Monitoring Kesehatan
            Text(
                text = "Monitoring Kesehatan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate today's summary from physical activities
            val todayPhysicalActivities = healthDataManager.getPhysicalActivityList()
                .filter { it.timestamp >= todayStart }
            val totalSteps = todayPhysicalActivities.sumOf { it.steps ?: 0 }
            val totalCalories = todayPhysicalActivities.sumOf { it.caloriesBurned ?: 0 }
            val totalMinutes = todayPhysicalActivities.sumOf { it.duration }

            // Calculate activity progress for monitoring card
            val activityProgress = if (totalMinutes > 0) {
                (totalMinutes.toFloat() / 30f).coerceIn(0f, 1f)
            } else {
                0f
            }

            MonitoringKesehatanSection(
                onNavigateToScreen = onNavigateToScreen,
                activityProgress = activityProgress
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 4: Ringkasan hari ini
            Text(
                text = "Ringkasan hari ini",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            RingkasanHariIni(
                steps = totalSteps,
                calories = totalCalories,
                minutes = totalMinutes
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 5: Target Kesehatan Hari Ini (Expandable)
            TargetKesehatanHariIni(
                isExpanded = isGoalsExpanded,
                onExpandToggle = onGoalsExpandToggle,
                currentSteps = totalSteps,
                currentCalories = totalCalories,
                currentMinutes = totalMinutes
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 6: Statistik Minggu Ini (Expandable)
            // Calculate weekly statistics
            val weekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Count weekly data from each category
            val bloodPressureCount = healthDataManager.getBloodPressureList().count { it.timestamp >= weekStart }
            val bloodSugarCount = healthDataManager.getBloodSugarList().count { it.timestamp >= weekStart }
            val activityCount = healthDataManager.getPhysicalActivityList().count { it.timestamp >= weekStart }
            val foodCount = healthDataManager.getFoodIntakeList().count { it.timestamp >= weekStart }

            StatistikMingguIni(
                isExpanded = isWeeklyStatsExpanded,
                onExpandToggle = onWeeklyStatsExpandToggle,
                bloodPressureCount = bloodPressureCount,
                bloodSugarCount = bloodSugarCount,
                activityCount = activityCount,
                foodCount = foodCount
            )

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }
    }
}

@Composable
fun GreetingWithChallengeCard(
    userName: String,
    dailyProgress: Float = 0f
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: illustration + text
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Illustration placeholder (box with icon)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthColors.NeonGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = HealthColors.NeonGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Halo, $userName!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily Challenges",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HealthColors.NeonGreen
                    )
                    Text(
                        text = "Capai targetmu hari ini sebelum\njam 09.00",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.sp
                    )
                }
            }

            // Right side: Circular progress with real data
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { dailyProgress },
                    modifier = Modifier.size(72.dp),
                    color = HealthColors.NeonGreen,
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                )
                Text(
                    text = "${(dailyProgress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun WeeklyCalendar() {
    val calendar = Calendar.getInstance()
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed

    // Get the current week's dates
    val currentWeekDates = mutableListOf<Int>()
    val tempCalendar = Calendar.getInstance()
    tempCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    for (i in 0..6) {
        currentWeekDates.add(tempCalendar.get(Calendar.DAY_OF_MONTH))
        tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEachIndexed { index, day ->
            val dayNumber = currentWeekDates[index]
            val isSelected = index == currentDayOfWeek

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) HealthColors.NeonGreen
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format("%02d", dayNumber),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun MonitoringKesehatanSection(
    onNavigateToScreen: (String) -> Unit,
    activityProgress: Float = 0f
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // First row: 3 cards (Data Kesehatan, Aktivitas Fisik, Asupan Makanan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonitoringCard(
                title = "Data Kesehatan",
                subtitle = "Tambahkan Data\nKesehatan Anda",
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToScreen("health_records") }
            )
            MonitoringCard(
                title = "Aktivitas Fisik",
                subtitle = "Tambahkan Aktivitas\nHari ini",
                icon = Icons.Default.DirectionsRun,
                modifier = Modifier.weight(1f),
                progress = activityProgress,
                onClick = { onNavigateToScreen("physical_activity") }
            )
            MonitoringCard(
                title = "Asupan Makanan",
                subtitle = "Tambahkan Aktivitas\nHari ini",
                icon = Icons.Default.Restaurant,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToScreen("food_intake") }
            )
        }

        // Second row: 2 cards (Grafik Kesehatan, Pengingat Kesehatan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonitoringCard(
                title = "Grafik Kesehatan",
                subtitle = "Tambahkan Aktivitas\nHari ini",
                icon = Icons.Default.Assessment,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToScreen("charts") }
            )
            MonitoringCard(
                title = "Pengingat Kesehatan",
                subtitle = "Tambahkan Aktivitas\nHari ini",
                icon = Icons.Default.Alarm,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToScreen("reminders") }
            )
        }
    }
}

@Composable
fun MonitoringCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    progress: Float? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = HealthColors.NeonGreen,
                modifier = Modifier.size(26.dp)
            )

            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 10.sp
                )
            }

            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = HealthColors.NeonGreen,
                    trackColor = MaterialTheme.colorScheme.outlineVariant,
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun RingkasanHariIni(
    steps: Int = 0,
    calories: Int = 0,
    minutes: Int = 0
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RingkasanCard(
            value = steps.toString(),
            label = "Langkah",
            icon = Icons.Default.DirectionsRun,
            modifier = Modifier.weight(1f)
        )
        RingkasanCard(
            value = calories.toString(),
            label = "Kalori",
            icon = Icons.Default.Restaurant,
            modifier = Modifier.weight(1f)
        )
        RingkasanCard(
            value = minutes.toString(),
            label = "Menit",
            icon = Icons.Default.Alarm,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RingkasanCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = HealthColors.NeonGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TargetKesehatanHariIni(
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    currentSteps: Int = 0,
    currentCalories: Int = 0,
    currentMinutes: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target Kesehatan Hari Ini",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // Langkah
                TargetProgressItem(
                    icon = Icons.Default.DirectionsRun,
                    label = "Langkah",
                    current = currentSteps,
                    goal = 10000,
                    unit = "Langkah"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kalori
                TargetProgressItem(
                    icon = Icons.Default.Restaurant,
                    label = "Kalori",
                    current = currentCalories,
                    goal = 2000,
                    unit = "Kal"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Menit
                TargetProgressItem(
                    icon = Icons.Default.Alarm,
                    label = "Menit",
                    current = currentMinutes,
                    goal = 30,
                    unit = "Menit"
                )
            }
        }
    }
}

@Composable
fun TargetProgressItem(
    icon: ImageVector,
    label: String,
    current: Int,
    goal: Int,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$current / $goal $unit",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = HealthColors.NeonGreen,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
fun StatistikMingguIni(
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    bloodPressureCount: Int = 0,
    bloodSugarCount: Int = 0,
    activityCount: Int = 0,
    foodCount: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Statistik Minggu Ini",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                StatistikItem(
                    icon = Icons.Default.Favorite,
                    label = "Tekanan Darah",
                    count = bloodPressureCount,
                    iconBgColor = HealthColors.BloodPressure
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatistikItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Gula Darah",
                    count = bloodSugarCount,
                    iconBgColor = HealthColors.BloodSugar
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatistikItem(
                    icon = Icons.Default.DirectionsRun,
                    label = "Aktivitas Fisik",
                    count = activityCount,
                    iconBgColor = HealthColors.Activity
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatistikItem(
                    icon = Icons.Default.Restaurant,
                    label = "Asupan Makanan",
                    count = foodCount,
                    iconBgColor = HealthColors.Food
                )
            }
        }
    }
}

@Composable
fun StatistikItem(
    icon: ImageVector,
    label: String,
    count: Int,
    iconBgColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = iconBgColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "$count Kali",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$current / $goal $unit",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "$count kali",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
