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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.luminance
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState

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
    var showSearchDialog by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

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
                        onLogout = onLogoutClick
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
                        onNavigateToScreen = { screen -> currentScreen = screen },
                        onSearchClick = { showSearchDialog = true },
                        onNotificationClick = { showNotifications = true }
                    )
                }
            }
        }
    }

    // Search Dialog
    if (showSearchDialog) {
        SearchDialog(
            healthDataManager = healthDataManager,
            onDismiss = { showSearchDialog = false },
            onResultClick = { screen ->
                showSearchDialog = false
                currentScreen = screen
            }
        )
    }

    // Notifications Bottom Sheet
    if (showNotifications) {
        NotificationsBottomSheet(
            healthDataManager = healthDataManager,
            onDismiss = { showNotifications = false }
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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

@Composable
fun BottomNavItem(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animated color transition
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isPressed && isDarkTheme -> Color(0xFF3C3C3C)
            isPressed && !isDarkTheme -> Color(0xFFE0E0E0)
            selected && isDarkTheme -> Color(0xFF2C2C2C)
            selected && !isDarkTheme -> Color(0xFFF0F0F0)
            else -> Color.Transparent
        },
        label = "background color"
    )

    val iconTint by animateColorAsState(
        targetValue = when {
            isPressed -> HealthColors.NeonGreen
            selected && isDarkTheme -> Color.White
            selected && !isDarkTheme -> Color.Black
            isDarkTheme -> Color.White.copy(alpha = 0.5f)
            else -> Color.Black.copy(alpha = 0.5f)
        },
        label = "icon tint"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconTint
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
    onNavigateToScreen: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
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
                        modifier = Modifier.size(24.dp).clickable { onSearchClick() }
                    )
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp).clickable { onNotificationClick() }
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
    dailyProgress: Float = 0f // 0..1
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT SIDE: Column dengan Greeting di atas dan Row (Image + Title/Desc) di bawah
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Greeting (di paling kiri, atas)
                Text(
                    text = "Halo, $userName!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // thin divider (sebagai pemisah di bawah greeting)
                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(1.dp)
                        .alpha(0.12f),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Baris: gambar di kiri, text (title + description) di kanan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // kotak kecil untuk ilustrasi
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LifeCareBlue.copy(alpha = 0.06f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.card),
                            contentDescription = "Ilustrasi Daily Challenge",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Judul + deskripsi (sejajar dengan gambar)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = "Daily Challenges",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LifeCareGreen
                        )

                        Text(
                            text = "Capai targetmu hari ini sebelum jam 09.00",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // RIGHT SIDE: Circular percentage ring (tetap)
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val percent = (dailyProgress * 100).coerceIn(0f, 100f).toInt()

                CircularProgressIndicator(
                    progress = dailyProgress.coerceIn(0f, 1f),
                    modifier = Modifier.size(96.dp),
                    color = LifeCareGreen,
                    strokeWidth = 10.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = "${percent}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun WeeklyCalendar() {
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

            // HANYA hari ini yang berwarna hijau, lainnya theme-aware
            val backgroundColor = if (isToday) Color(0xFF98CD00) else MaterialTheme.colorScheme.surfaceVariant
            val textColor = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            val dotColor = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

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

    Spacer(modifier = Modifier.height(12.dp))

    // divider tipis bawah (sebagai pemisah dari konten berikutnya)
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 1.dp
    )
}

@Composable
fun MonitoringKesehatanSection(
    onNavigateToScreen: (String) -> Unit,
    activityProgress: Float = 0f
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // First row: 3 cards (kotak proporsional, tinggi tetap)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonitoringCard(
                title = "Data Kesehatan",
                subtitle = "Tambahkan Data\nKesehatan Anda",
                icon = Icons.Default.Favorite,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),         // tinggi konsisten untuk baris atas
                onClick = { onNavigateToScreen("health_records") }
            )
            MonitoringCard(
                title = "Aktivitas Fisik",
                subtitle = "Tambahkan Aktivitas Hari ini",
                icon = Icons.Default.DirectionsRun,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                progress = activityProgress,
                onClick = { onNavigateToScreen("physical_activity") }
            )
            MonitoringCard(
                title = "Asupan Makanan",
                subtitle = "Tambahkan Asupan\nHari ini",
                icon = Icons.Default.Restaurant,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
                onClick = { onNavigateToScreen("food_intake") }
            )
        }

        // Second row: 2 wide cards (lebih lebar, tinggi sama dengan atas atau sedikit lebih besar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MonitoringCard(
                title = "Grafik Kesehatan",
                subtitle = "Cek Grafik Kesehatan",
                icon = Icons.Default.Assessment,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),     // tinggi tetap sama sehingga rata
                onClick = { onNavigateToScreen("charts") }
            )
            MonitoringCard(
                title = "Pengingat Kesehatan",
                subtitle = "Tambahkan Pengingat\nKesehatan",
                icon = Icons.Default.Alarm,
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp),
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
            .heightIn(min = 140.dp)                  // pastikan tinggi cukup
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        // susunan: top = icon, middle = content (title+subtitle) kiri, bottom = progress
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ICON di pojok kiri atas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(HealthColors.NeonGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = HealthColors.NeonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            // CONTENT (title + subtitle) — rata kiri seperti contoh
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    fontSize = 8.sp,                            // lebih besar
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    fontSize = 6.sp,                            // jelas & readable
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 8.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // PROGRESS bar (jika ada) di paling bawah
            if (progress != null) {
                val p = progress.coerceIn(0f, 1f)
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = p,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = HealthColors.NeonGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
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
        modifier = modifier
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ICON — besar dan neon green seperti gambar
            Icon(
                icon,
                contentDescription = label,
                tint = HealthColors.NeonGreen,
                modifier = Modifier.size(42.dp)
            )

            // VALUE — tebal, tidak terlalu besar agar tidak "meloncat"
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // LABEL — kecil, rapi, sama seperti referensi desain
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF98CD00)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(26.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Langkah
                TargetProgressItem(
                    icon = Icons.Default.DirectionsRun,
                    label = "Langkah",
                    current = currentSteps,
                    goal = 10000,
                    unit = "Langkah"
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Kalori
                TargetProgressItem(
                    icon = Icons.Default.Restaurant,
                    label = "Kalori",
                    current = currentCalories,
                    goal = 2000,
                    unit = "Kal"
                )

                Spacer(modifier = Modifier.height(14.dp))

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
    val p = (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HealthColors.NeonGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // label kiri
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // current / goal di kanan (angka hijau)
            val annotated = buildAnnotatedString {
                withStyle(style = SpanStyle(color = HealthColors.NeonGreen, fontWeight = FontWeight.Bold)) {
                    append(current.toString())
                }
                append(" / ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(goal.toString())
                }
                append(" ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(unit)
                }
            }
            Text(
                text = annotated,
                fontSize = 13.sp,
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // progress bar
        LinearProgressIndicator(
            progress = p,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = HealthColors.NeonGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: judul kiri + panah kanan
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
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(26.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Items
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
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // left: icon + label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconBgColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // right: count (bold)
        Text(
            text = "$count Kali",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog(
    healthDataManager: HealthDataManager,
    onDismiss: () -> Unit,
    onResultClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            buildList {
                // Search dalam menu
                if ("data kesehatan".contains(searchQuery, ignoreCase = true) ||
                    "health records".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Data Kesehatan", "Lihat riwayat data kesehatan Anda", "health_records", Icons.Default.Favorite))
                }
                if ("aktivitas".contains(searchQuery, ignoreCase = true) ||
                    "physical".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Aktivitas Fisik", "Catat aktivitas fisik harian", "physical_activity", Icons.Default.DirectionsRun))
                }
                if ("makanan".contains(searchQuery, ignoreCase = true) ||
                    "food".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Asupan Makanan", "Catat asupan makanan", "food_intake", Icons.Default.Restaurant))
                }
                if ("grafik".contains(searchQuery, ignoreCase = true) ||
                    "chart".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Grafik Kesehatan", "Lihat grafik kesehatan", "charts", Icons.Default.Assessment))
                }
                if ("pengingat".contains(searchQuery, ignoreCase = true) ||
                    "reminder".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Pengingat", "Atur pengingat kesehatan", "reminders", Icons.Default.Alarm))
                }
                if ("profil".contains(searchQuery, ignoreCase = true) ||
                    "profile".contains(searchQuery, ignoreCase = true)) {
                    add(SearchResult("Profil", "Lihat dan edit profil", "profile", Icons.Default.Person))
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cari Fitur",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari fitur atau menu...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthColors.NeonGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Results
                if (searchQuery.isNotBlank()) {
                    if (searchResults.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tidak ada hasil",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            searchResults.forEach { result ->
                                SearchResultItem(
                                    result = result,
                                    onClick = { onResultClick(result.screen) }
                                )
                            }
                        }
                    }
                } else {
                    // Suggestion saat kosong
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Saran Pencarian:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        listOf("Data Kesehatan", "Aktivitas", "Makanan", "Grafik").forEach { suggestion ->
                            SuggestionChip(
                                onClick = { searchQuery = suggestion },
                                label = { Text(suggestion, fontSize = 12.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = HealthColors.NeonGreen.copy(alpha = 0.1f),
                                    labelColor = HealthColors.NeonGreen
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SearchResult(
    val title: String,
    val description: String,
    val screen: String,
    val icon: ImageVector
)

@Composable
fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(HealthColors.NeonGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = result.icon,
                    contentDescription = result.title,
                    tint = HealthColors.NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = result.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsBottomSheet(
    healthDataManager: HealthDataManager,
    onDismiss: () -> Unit
) {
    // Generate sample notifications based on health data
    val notifications = remember {
        buildList {
            val now = Calendar.getInstance()

            // Notification for daily challenge
            add(
                NotificationItem(
                    id = 1,
                    title = "Daily Challenge",
                    message = "Jangan lupa capai target harianmu!",
                    timestamp = now.timeInMillis,
                    icon = Icons.Default.EmojiEvents,
                    color = HealthColors.NeonGreen
                )
            )

            // Notification for reminder
            add(
                NotificationItem(
                    id = 2,
                    title = "Pengingat Kesehatan",
                    message = "Saatnya cek tekanan darah Anda",
                    timestamp = now.timeInMillis - 3600000, // 1 hour ago
                    icon = Icons.Default.Alarm,
                    color = HealthColors.BloodPressure
                )
            )

            // Notification for achievement
            add(
                NotificationItem(
                    id = 3,
                    title = "Pencapaian Baru!",
                    message = "Selamat! Anda telah mencapai 10.000 langkah hari ini",
                    timestamp = now.timeInMillis - 7200000, // 2 hours ago
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFD700)
                )
            )

            // Notification for health tip
            add(
                NotificationItem(
                    id = 4,
                    title = "Tips Kesehatan",
                    message = "Jangan lupa minum air minimal 8 gelas sehari",
                    timestamp = now.timeInMillis - 86400000, // 1 day ago
                    icon = Icons.Default.Lightbulb,
                    color = HealthColors.Activity
                )
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifikasi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = { /* Mark all as read */ }) {
                    Text(
                        text = "Tandai Semua Dibaca",
                        color = HealthColors.NeonGreen,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                notifications.forEach { notification ->
                    NotificationCard(notification = notification)
                }
            }
        }
    }
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    val icon: ImageVector,
    val color: Color,
    val isRead: Boolean = false
)

@Composable
fun NotificationCard(notification: NotificationItem) {
    val timeAgo = remember(notification.timestamp) {
        val diff = System.currentTimeMillis() - notification.timestamp
        when {
            diff < 60000 -> "Baru saja"
            diff < 3600000 -> "${diff / 60000} menit yang lalu"
            diff < 86400000 -> "${diff / 3600000} jam yang lalu"
            else -> "${diff / 86400000} hari yang lalu"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(notification.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = notification.title,
                    tint = notification.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = timeAgo,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Unread indicator
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HealthColors.NeonGreen)
                )
            }
        }
    }
}
