# Changelog - LifeCare

All notable changes to LifeCare project will be documented in this file.

## [1.2.0] - 2025-11-28

### 🎉 Added (Opsi A Implementation)

#### 📊 Health Charts Feature
- **Custom Line Charts** menggunakan Compose Canvas API
  - Blood Pressure charts (Systolic & Diastolic)
  - Blood Sugar chart dengan color-coded ranges
  - BMI chart dengan zone indicators
- **Custom Bar Charts** untuk aggregate data
  - Physical Activity: Total calories per day
  - Food Intake: Total calories per day
- **5 Chart Categories** dengan tab navigation
  - Tekanan Darah, Gula Darah, BMI, Aktivitas, Makanan
- **3 Date Range Filters**: 7, 30, 90 hari
- **Auto-scaling** charts berdasarkan data range
- **Empty State Handling** untuk data kosong
- **Material Design 3** theming konsisten

**Files Added:**
- `app/src/main/java/com/example/lifecare/charts/ChartData.kt`
- `app/src/main/java/com/example/lifecare/charts/LineChart.kt`
- `app/src/main/java/com/example/lifecare/charts/BarChart.kt`
- `app/src/main/java/com/example/lifecare/charts/HealthChartsScreen.kt`

#### ⏰ Health Reminders Feature
- **AlarmManager Integration** untuk daily reminders
- **6 Reminder Types**:
  - Tekanan Darah (Pagi) - default 08:00
  - Tekanan Darah (Malam) - default 20:00
  - Gula Darah - default 07:00
  - Berat Badan - default 07:30
  - Aktivitas Fisik - default 17:00
  - Asupan Makanan - default 19:00
- **Custom Time Setting** dengan TimePickerDialog
- **Enable/Disable Toggle** per reminder
- **Push Notifications** dengan notification channels
- **Deep Links** dari notification ke target screen
- **EncryptedSharedPreferences** untuk reminder config storage
- **Android 12+ Support** dengan exact alarm permission

**Files Added:**
- `app/src/main/java/com/example/lifecare/reminder/ReminderType.kt`
- `app/src/main/java/com/example/lifecare/reminder/ReminderManager.kt`
- `app/src/main/java/com/example/lifecare/reminder/ReminderReceiver.kt`
- `app/src/main/java/com/example/lifecare/reminder/ReminderNotification.kt`
- `app/src/main/java/com/example/lifecare/reminder/ReminderSettingsScreen.kt`

#### 📄 Documentation
- `IMPLEMENTATION_SUMMARY.md` - Detailed implementation notes
- `FEATURES_GUIDE.md` - Comprehensive user guide
- `TESTING_CHECKLIST.md` - Complete testing checklist (174 test cases)
- `CHANGELOG.md` - This file

### 🔧 Changed

#### AndroidManifest.xml
- Added `SCHEDULE_EXACT_ALARM` permission
- Added `USE_EXACT_ALARM` permission
- Registered `ReminderReceiver` BroadcastReceiver

#### MainActivity.kt
- Added `CHARTS` screen to AppScreen sealed class
- Added `REMINDERS` screen to AppScreen sealed class

#### Home.kt
- Added imports untuk HealthChartsScreen & ReminderSettingsScreen
- Added navigation cases untuk "charts" dan "reminders"
- Added 2 new HealthCategoryCard di dashboard grid:
  - **Grafik Kesehatan** (ShowChart icon, Orange)
  - **Pengingat Kesehatan** (Notifications icon, Purple)
- Updated dashboard layout dari 1x2 grid ke 2x2 grid

#### README.md
- Updated feature list dengan Charts & Reminders
- Updated teknologi section
- Updated completion status: 98% → 99%
- Updated version to 1.2.0

### 📊 Statistics
- **Lines of Code Added**: ~1,400 lines
- **New Files**: 9 files
- **Modified Files**: 3 files
- **New Features**: 2 major features
- **Completion**: 98% → 99%

### 🐛 Fixed
- No bugs in this release (new features)

### 🔒 Security
- EncryptedSharedPreferences untuk reminder data (AES256_GCM)
- PendingIntent.FLAG_IMMUTABLE untuk Android 12+
- Proper permission handling untuk notifications

### ⚡ Performance
- Pure Compose Canvas (no external chart library)
- Efficient AlarmManager scheduling
- Minimal memory footprint untuk charts
- Smooth chart rendering with auto-scaling

---

## [1.1.0] - 2025-11-25 (Previous Session)

### Added
- Firebase Authentication integration
- Google Sign-In support
- Enhanced error handling
- Code cleanup utilities (ValidationHelper, PasswordStrengthCalculator, Constants)

### Changed
- Updated RegisterScreen dengan gender options fix
- Updated GoogleSignInHelper dengan correct Web Client ID
- Improved AuthRepository error messages
- Condensed README.md (488 → 120 lines)

### Documentation
- CODE_CLEANUP_SUMMARY.md
- GOOGLE_SIGNIN_FIX.md
- GOOGLE_SIGNIN_SOLUTION.md
- GOOGLE_SIGNIN_FINAL_SETUP.md
- GOOGLE_SERVICES_VERIFICATION.md
- LOGIN_REGISTER_VERIFICATION.md

---

## [1.0.0] - 2025-11-20 (Initial Release)

### Added
- Initial project setup
- Authentication system (Email/Password)
- PIN security system (6-digit)
- 5 Health tracking modules:
  - Tekanan Darah
  - Gula Darah
  - Berat & Tinggi (BMI)
  - Aktivitas Fisik
  - Asupan Makanan
- GPS Run Tracking
- Dashboard dengan progress tracking
- Health Records dengan filter & sort
- Export functionality (CSV, JSON, TXT)
- Profile management
- Dark mode support
- EncryptedSharedPreferences
- Firebase Firestore integration

### Technologies Used
- Kotlin
- Jetpack Compose
- Material Design 3
- Firebase (Auth & Firestore)
- GPS & Location Services

---

## Roadmap

### [1.3.0] - Future (Optional)
- Health Trends & Insights calculator
- Health Score algorithm
- Weekly/monthly report generation
- AI-powered health recommendations
- Cloud sync & backup improvements

### [2.0.0] - Future (Major Update)
- WearOS support
- Google Fit integration
- Community features
- Export to PDF
- Medication tracker
- Doctor appointment scheduler

---

## Version Numbering

Format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes, major feature additions
- **MINOR**: New features, backward compatible
- **PATCH**: Bug fixes, small improvements

---

**Current Version:** 1.2.0
**Status:** Production Ready
**Last Updated:** 28 November 2025
