# LifeCare - Aplikasi Tracking Kesehatan Pribadi

![LifeCare](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange)

## 📱 Deskripsi

LifeCare adalah aplikasi mobile Android untuk tracking kesehatan pribadi yang membantu pengguna memantau berbagai aspek kesehatan mereka seperti tekanan darah, gula darah, BMI, aktivitas fisik, dan asupan makanan. Aplikasi ini dibangun dengan teknologi modern menggunakan Jetpack Compose dan menyediakan keamanan data dengan enkripsi.

## ✨ Fitur Utama

### 1. 🔐 Autentikasi & Keamanan
- **Login & Register**: Sistem autentikasi dengan email dan password
- **Google Sign-In**: Integrasi dengan Google untuk login cepat
- **PIN Security**: Proteksi tambahan dengan 6-digit PIN
- **Change PIN**: Fitur ubah PIN dengan validasi
- **Data Encryption**: Semua data disimpan dengan EncryptedSharedPreferences (AES256_GCM)

### 2. 📊 Tracking Kesehatan

#### Tekanan Darah
- Input sistolik, diastolik, dan detak jantung
- Validasi medis (70-250 mmHg sistolik, 40-150 diastolik)
- Peringatan otomatis untuk nilai abnormal (Crisis, Stage 2, Stage 1)
- Validasi logika medis (sistolik harus > diastolik)

#### Gula Darah
- Monitor level gula darah (20-600 mg/dL)
- Tipe pengukuran: Puasa, Setelah Makan, Sebelum Makan, Sebelum Tidur
- Deteksi prediabetes dan diabetes
- Peringatan hipoglikemia untuk nilai rendah

#### Berat & Tinggi Badan
- Input berat (20-300 kg) dan tinggi (50-250 cm)
- **Kalkulasi BMI real-time** dengan preview langsung
- Kategori BMI: Kurang, Normal, Kelebihan, Obesitas
- Color-coded BMI indicators
- Rekomendasi kesehatan berdasarkan BMI

#### Aktivitas Fisik
- Jenis aktivitas: Jalan Kaki, Lari, Bersepeda, Berenang, Yoga, Gym
- Durasi aktivitas (1-1440 menit)
- **Auto-calculated calorie estimates** berdasarkan jenis aktivitas
- Tracking langkah dan kalori terbakar (opsional)
- Pesan motivasi berdasarkan durasi

#### Asupan Makanan
- Input nama makanan (2-50 karakter) dan kalori (1-10000 kal)
- Waktu makan: Sarapan, Makan Siang, Makan Malam, Snack
- Tracking nutrisi detail: Protein, Karbohidrat, Lemak
- Validasi nutrisi (nilai non-negatif)
- Kategorisasi kalori (tinggi, sedang, rendah)

### 3. 🏠 Dashboard Interaktif
- **Data Kesehatan Terbaru**: Tampilan ringkas data terakhir dengan quick access
- **Target Kesehatan Harian**:
  - Progress bar langkah (target: 10,000 langkah)
  - Progress bar kalori (target: 2,000 kal)
  - Progress bar olahraga (target: 30 menit)
  - Persentase pencapaian real-time
- **Ringkasan Hari Ini**: Total langkah, kalori, dan menit olahraga
- **Statistik Minggu Ini**: Ringkasan aktivitas pencatatan data mingguan
- **Tips Kesehatan**: Rekomendasi personal berdasarkan aktivitas pengguna

### 4. 📋 Riwayat Kesehatan
- **Tab Navigation**: 6 tab untuk filter (Semua, Berat & Tinggi, Tekanan Darah, Gula Darah, Aktivitas, Makanan)
- **Date Filter**:
  - Semua
  - Hari Ini
  - Minggu Ini
  - Bulan Ini
- **Sort Options**:
  - Terbaru (Date Descending)
  - Terlama (Date Ascending)
  - Nilai Tertinggi
  - Nilai Terendah
- **Delete Functionality**: Hapus data dengan konfirmasi dialog
- **Empty State**: Pesan informatif saat tidak ada data
- **Filter & Sort Info Card**: Menampilkan filter dan sort aktif dengan tombol reset

### 5. 💾 Export Data
- **Format CSV**: Untuk analisis di Excel/Google Sheets
- **Format JSON**: Untuk integrasi dengan aplikasi lain
- **Format TXT**: Untuk print dan dokumentasi
- Include semua kategori data kesehatan
- Auto-save ke folder Downloads dengan timestamp
- Export accessible dari HealthRecordsScreen

### 6. �� Manajemen Profil
- Tampilan data user: Nama lengkap, Email, Umur, Jenis Kelamin
- **Edit Profil**: Update nama, umur, dan jenis kelamin
- **Validasi Edit Profil**:
  - Nama: 3-50 karakter
  - Umur: 1-150 tahun
  - Jenis kelamin wajib dipilih
- Ganti PIN keamanan
- Logout dengan konfirmasi

### 7. 🎯 Validasi Komprehensif
- **Real-time Input Filtering**: Numeric, decimal, length limits
- **Medical-grade Range Validation**: Sesuai standar medis
- **Required Field Validation**: Semua field wajib tervalidasi
- **Error Messages**: Bahasa Indonesia yang jelas
- **Visual Indicators**: Red text, border highlighting untuk error

## 🛠️ Teknologi yang Digunakan

### Core Technologies
- **Kotlin**: Bahasa pemrograman utama
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material Design 3**: Latest design system

### Security & Storage
- **EncryptedSharedPreferences**: Enkripsi data lokal (AES256_GCM)
- **Android Keystore**: Secure key management
- **Gson**: JSON serialization/deserialization

### Authentication
- **Google Sign-In**: OAuth 2.0 authentication
- **Credential Manager**: Modern authentication API

### Architecture & Pattern
- **Compose State Management**: `remember`, `mutableStateOf`
- **Navigation**: In-app navigation dengan bottom bar persistent
- **Data Layer**: HealthDataManager untuk centralized data management

## 📦 Struktur Project

```
app/
├── src/main/java/com/example/lifecare/
│   ├── data/
│   │   ├── HealthDataManager.kt      # Data management & encryption
│   │   ├── UserData.kt               # User data model
│   │   ├── BodyMetrics.kt           # Data class untuk BMI
│   │   ├── BloodPressure.kt         # Data class untuk tekanan darah
│   │   ├── BloodSugar.kt            # Data class untuk gula darah
│   │   ├── PhysicalActivity.kt      # Data class untuk aktivitas
│   │   └── FoodIntake.kt            # Data class untuk makanan
│   ├── screens/
│   │   ├── BodyMetricsScreen.kt     # Screen BMI dengan validasi
│   │   ├── BloodPressureScreen.kt   # Screen tekanan darah
│   │   ├── BloodSugarScreen.kt      # Screen gula darah
│   │   ├── PhysicalActivityScreen.kt # Screen aktivitas
│   │   ├── FoodIntakeScreen.kt      # Screen makanan
│   │   ├── HealthRecordsScreen.kt   # Screen riwayat dengan filter/sort
│   │   ├── ProfileScreen.kt         # Screen profil dengan edit
│   │   └── ChangePINScreen.kt       # Screen ganti PIN
│   ├── auth/
│   │   ├── GoogleSignInHelper.kt    # Google OAuth helper
│   │   ├── Login.kt                 # Login screen
│   │   └── Register.kt              # Register screen
│   ├── Home.kt                      # Main dashboard
│   ├── MainActivity.kt              # Entry point
│   └── PINScreen.kt                 # PIN security screen
│
└── res/
    └── values/
        └── strings.xml              # String resources
```

## 🎨 Design System

### Color Palette
- **Primary (Turquoise)**: `#5DCCB4` - Main actions, selected states
- **Background**: `#F8F9FA` - Screen background
- **Surface**: `#FFFFFF` - Cards, elevated surfaces
- **Text Primary**: `#2D3748` - Main text
- **Text Secondary**: `#ADB5BD` - Secondary text, hints
- **Error**: `MaterialTheme.colorScheme.error` - Error messages

### Category Colors
- **Tekanan Darah**: `#E91E63` (Pink)
- **Gula Darah**: `#9C27B0` (Purple)
- **Berat & Tinggi**: `#2196F3` (Blue)
- **Aktivitas Fisik**: `#4CAF50` (Green)
- **Asupan Makanan**: `#FF9800` (Orange)

### BMI Color Coding
- **Kurang**: Yellow
- **Normal**: Green
- **Kelebihan**: Orange
- **Obesitas**: Red

### Typography
- **Heading**: 24sp, Bold
- **Title**: 18sp, Bold
- **Subtitle**: 16sp, Medium
- **Body**: 14sp, Regular
- **Caption**: 12sp, Regular

## 🔒 Keamanan

### Authentication Flow
1. **Login/Register**: Email + Password
2. **PIN Setup**: 6 digit PIN untuk data protection
3. **PIN Verify**: Validasi setiap app launch
4. **Encrypted Storage**: Semua data kesehatan dienkripsi

### Data Protection
- **AES256_GCM** encryption algorithm
- **Master Key** dengan Android KeyStore
- **PIN-based** access control
- **Secure** key derivation
- **Local-only** storage (no cloud)

## 📝 Cara Menggunakan

### First Time Setup
1. Buka aplikasi
2. Pilih **Register** untuk membuat akun baru
3. Masukkan:
   - Nama lengkap
   - Email
   - Password (min 6 karakter)
4. **Buat PIN 6-digit** untuk keamanan tambahan
5. Login dengan kredensial yang dibuat

### Daily Usage
1. **Masukkan PIN** saat membuka aplikasi
2. **Dashboard**:
   - Lihat ringkasan kesehatan hari ini
   - Check progress terhadap target harian
   - Baca tips kesehatan personal
3. **Catat Data**: Tap kategori untuk input data baru
4. **Review**: Lihat riwayat di tab "Riwayat"
5. **Export**: Bagikan data dengan dokter via export

### Menambah Data Kesehatan
1. Dari dashboard, tap kategori yang ingin dicatat
2. Tap tombol FAB (+) di kanan bawah
3. Isi form dengan data yang valid:
   - Field akan menampilkan error jika input tidak sesuai
   - Placeholder menunjukkan range normal
4. Tap "Simpan" untuk menyimpan data
5. Data akan langsung muncul di list dan dashboard

### Filtering & Sorting Data
1. Buka **Riwayat Kesehatan** dari bottom navigation
2. Pilih tab kategori atau "Semua"
3. Tap icon **Filter** untuk filter berdasarkan tanggal
4. Tap icon **Sort** untuk mengurutkan data
5. Reset filter dengan tombol "Reset" di info card

### Export Data
1. Buka **Riwayat Kesehatan**
2. Tap icon **Share** di top bar
3. Pilih format export (CSV, JSON, atau TXT)
4. Tap "Export"
5. File akan tersimpan di folder Downloads

### Edit Profil
1. Tap icon **Profile** di bottom navigation
2. Tap tombol "Edit Profil"
3. Update data (nama, umur, jenis kelamin)
4. Tap "Simpan"

### Mengubah PIN
1. Dari Profile screen, tap "Ubah PIN"
2. Masukkan PIN lama
3. Masukkan PIN baru (6 digit)
4. Konfirmasi PIN baru
5. Tap "Simpan PIN Baru"

## 🚀 Build & Run

### Requirements
- Android Studio Hedgehog | 2023.1.1 atau lebih baru
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 36 (Android 16)
- Kotlin 2.0.21
- Android Gradle Plugin 8.9.1

### Setup
1. Clone repository:
   ```bash
   git clone <repository-url>
   cd LifeCare
   ```

2. Buka project di Android Studio

3. Sync Gradle files (akan download dependencies otomatis)

4. Konfigurasi Google Sign-In (Opsional):
   - **⚠️ Note**: `google-services.json` **NOT included** in repository (for security)
   - Download dari Firebase Console (see [SETUP_FIREBASE.md](SETUP_FIREBASE.md))
   - Letakkan di folder `app/`
   - **Alternative**: Use manual registration (no setup needed)

5. Build & Run:
   ```bash
   ./gradlew assembleDebug
   ```
   Atau gunakan tombol Run (▶) di Android Studio

### Testing
- Gunakan emulator Android API 24+ (recommended: API 33+)
- Atau device fisik dengan USB debugging enabled
- Test Google Sign-In di device fisik (emulator mungkin memerlukan konfigurasi tambahan)

## 🐛 Troubleshooting

### Build Issues

**Problem**: Build berhasil dengan deprecation warnings
```
w: 'constructor(p0: String!, p1: String!): Locale' is deprecated
w: 'val Icons.Filled.ArrowBack: ImageVector' is deprecated
```
**Solution**: Ini adalah deprecation warnings yang tidak mempengaruhi fungsionalitas aplikasi. Aplikasi tetap berjalan dengan normal. Update ke API terbaru akan menghilangkan warnings ini di versi mendatang.

### Google Sign-In Issues

**Problem**: Google Sign-In gagal/tidak muncul
**Solution**:
- Pastikan `google-services.json` sudah dikonfigurasi dengan benar
- Test di device fisik dengan Google Play Services
- Periksa SHA-1 fingerprint di Google Cloud Console match dengan app
- Pastikan email test tidak ada di blocklist

### Data Issues

**Problem**: Data tidak tersimpan
**Solution**:
- Pastikan app memiliki permission storage (auto-granted untuk EncryptedSharedPreferences)
- Check HealthDataManager initialization
- Verify bahwa data di-save dengan method yang benar

**Problem**: Data hilang setelah uninstall
**Solution**: Ini adalah behavior normal. EncryptedSharedPreferences tersimpan di app data yang akan terhapus saat uninstall.

### Export Issues

**Problem**: Export gagal
**Solution**:
- Periksa storage permission untuk API 29+
- Pastikan folder Downloads accessible
- Try with different export format

## 📊 Fitur Validasi Detail

### Blood Pressure
- Sistolik: 70-250 mmHg (required)
- Diastolik: 40-150 mmHg (required)
- Heart Rate: 30-250 BPM (optional)
- Validasi: Sistolik > Diastolik

### Blood Sugar
- Level: 20-600 mg/dL (required)
- Measurement Type: Required selection
- Warnings:
  - Puasa ≥126: Diabetes warning
  - Puasa 100-125: Prediabetes
  - <70: Hypoglycemia warning

### Body Metrics
- Weight: 20-300 kg (required)
- Height: 50-250 cm (required)
- BMI: Auto-calculated
- Real-time preview dengan color coding

### Physical Activity
- Activity Type: Required selection
- Duration: 1-1440 minutes (required)
- Steps: Optional, positive integer
- Calories: Optional, positive integer
- Auto-estimate calories if not provided

### Food Intake
- Food Name: 2-50 characters (required)
- Calories: 1-10000 kcal (required)
- Meal Type: Required selection
- Nutrition (all optional, must be non-negative):
  - Protein: 0-1000g
  - Carbs: 0-1000g
  - Fat: 0-1000g

## 🗺️ Roadmap & Future Improvements

### Version 2.0 (Planned)
- [ ] Cloud backup & sync with Firebase
- [ ] Data visualization dengan charts (Line, Bar, Pie)
- [ ] Reminder & notifications untuk tracking rutin
- [ ] Health reports PDF generation
- [ ] Multi-language support (EN, ID)

### Version 2.1 (Planned)
- [ ] Dark mode support
- [ ] Home screen widget
- [ ] Integration dengan Google Fit
- [ ] Wearable support (smartwatch)
- [ ] Medication tracking
- [ ] Appointment scheduler

### Known Limitations & Notes (v1.2)
- Data hanya tersimpan lokal (tidak ada cloud sync)
- Belum ada data analytics/trends visualization
- Export memerlukan manual storage permission handling
- **Google Sign-In**: ✅ **CONFIGURED and ready to use!**
  - Firebase project: `lifecaree28-8cc63` configured
  - `google-services.json` installed in `app/` folder
  - Google Services Gradle plugin v4.4.4 added
  - Firebase BoM v34.6.0 integrated
  - Build successful with no errors
  - **Optional**: Register SHA-1 fingerprint untuk testing di device fisik - lihat [GOOGLE_SIGNIN_NEXT_STEPS.md](GOOGLE_SIGNIN_NEXT_STEPS.md)
  - **Alternative**: Manual registration (email/password) juga tetap fully functional

## 📈 Progress Status

**Overall Completion**: ~98% 🎉

### Completed Features ✅
1. ✅ Complete authentication system (Login, Register, Google Sign-In)
2. ✅ PIN security dengan encryption AES256
3. ✅ All 5 health tracking modules dengan full validation
4. ✅ Dashboard real-time dengan progress bars dan goals
5. ✅ Health records dengan filter & sort advanced
6. ✅ Export functionality (CSV, JSON, TXT)
7. ✅ Profile management dengan edit user data
8. ✅ Bottom navigation persistent across screens
9. ✅ Health insights & personalized tips
10. ✅ Comprehensive error handling & validation
11. ✅ Build system updated (SDK 36, AGP 8.9.1)
12. ✅ All compilation errors fixed
13. ✅ Successful command-line build
14. ✅ **Google Sign-In fully configured with Firebase**
15. ✅ **google-services.json integrated**
16. ✅ **Google Services Gradle plugin configured**

### Pending Features ⏳
- ⏳ SHA-1 fingerprint registration (optional, untuk device testing)
- ⏳ Storage permission handling untuk Android 10+
- ⏳ Data visualization charts
- ⏳ Medication tracking module
- ⏳ Cloud sync functionality

## 👥 Credits

Aplikasi ini dibuat untuk memenuhi tugas mata kuliah **Pemrograman Perangkat Bergerak**.

**Pengembang:**
- [Nama Mahasiswa]

**Universitas:**
- [Nama Universitas]

**Dosen Pengampu:**
- [Nama Dosen]

## 📄 License

Educational Project - Copyright © 2024-2025

---

**Version**: 1.2.0
**Last Updated**: 25 November 2025
**Status**: ✅ Production Ready (98% Complete)
**Build Status**: ✅ Successful (No compilation errors)
**Google Sign-In**: ✅ Configured with Firebase

## 📞 Support & Feedback

Untuk pertanyaan, bug report, atau saran pengembangan:
- Create issue di repository
- Contact: [email/contact info]

---

**Terima kasih telah menggunakan LifeCare!** 🏥💚
*Your health, tracked with care.*
