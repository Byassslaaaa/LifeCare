# LifeCare - Aplikasi Tracking Kesehatan

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange)

## 📱 Tentang Aplikasi

LifeCare adalah aplikasi mobile Android untuk tracking kesehatan pribadi dengan fitur lengkap meliputi monitoring tekanan darah, gula darah, BMI, aktivitas fisik, dan asupan makanan.

## ✨ Fitur Utama

### 🔐 Keamanan & Autentikasi
- Login/Register dengan Email & Password atau Google Sign-In
- PIN 6-digit untuk proteksi data
- Enkripsi data dengan AES256_GCM
- Session management (30 menit)

### 📊 Tracking Kesehatan
- **Tekanan Darah**: Monitoring sistolik, diastolik, dan detak jantung
- **Gula Darah**: Tracking level dengan tipe pengukuran (Puasa, Setelah Makan, dll)
- **Berat & Tinggi**: Kalkulasi BMI otomatis dengan kategori
- **Aktivitas Fisik**: GPS tracking untuk lari/jalan, estimasi kalori
- **Asupan Makanan**: Tracking kalori dan nutrisi (protein, karbo, lemak)

### 📈 Grafik & Visualisasi
- **Health Charts**: 5 kategori chart (Tekanan Darah, Gula Darah, BMI, Aktivitas, Makanan)
- **Date Range Filter**: 7 hari, 30 hari, 90 hari
- **Custom Charts**: Line charts dan bar charts dengan Compose Canvas
- **Auto-scaling**: Charts otomatis menyesuaikan dengan range data

### ⏰ Pengingat Kesehatan
- **6 Tipe Reminder**: Tekanan darah (pagi/malam), gula darah, berat badan, aktivitas, makanan
- **Custom Time**: Set waktu reminder sesuai kebutuhan
- **Daily Notifications**: Notifikasi harian otomatis
- **Deep Links**: Tap notifikasi langsung buka screen terkait

### 🏠 Dashboard & Riwayat
- Dashboard interaktif dengan progress target harian
- Riwayat lengkap dengan filter (tanggal) dan sort
- Export data (CSV, JSON, TXT)

### 👤 Profil & Pengaturan
- Edit profil (nama, umur, jenis kelamin)
- Ganti PIN dengan verifikasi password
- Dark mode / Light mode
- Logout

## 🛠️ Teknologi

- **Kotlin** + **Jetpack Compose** + **Material Design 3**
- **Firebase**: Authentication & Firestore
- **EncryptedSharedPreferences**: Keamanan data lokal (AES256_GCM)
- **GPS & Location Services**: Tracking aktivitas real-time
- **AlarmManager**: Scheduling reminder harian
- **Notification System**: Push notifications dengan notification channels
- **Canvas API**: Custom chart rendering untuk visualisasi data

## 📦 Setup & Build

### Requirements
- Android Studio Hedgehog 2023.1.1+
- Min SDK 24 (Android 7.0)
- Target SDK 36

### Build Instructions
```bash
# Clone repository
git clone <repo-url>
cd LifeCare

# Build APK
./gradlew assembleDebug

# Install ke device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Firebase Setup (Opsional - untuk Google Sign-In)
1. Download `google-services.json` dari Firebase Console
2. Letakkan di folder `app/`
3. Enable Google Sign-In di Firebase Console → Authentication
4. Rebuild aplikasi

**Note:** Login dengan Email/Password tetap bisa digunakan tanpa Firebase setup.

## 🎯 Cara Pakai

1. **Register** dengan email atau Google Sign-In
2. **Buat PIN** 6-digit
3. **Login** dan masukkan PIN
4. **Catat data** kesehatan dari dashboard
5. **Lihat riwayat** dan export jika diperlukan

## 📊 Validasi Data

| Kategori | Range/Rule |
|----------|------------|
| Tekanan Darah | Sistolik: 70-250 mmHg, Diastolik: 40-150 mmHg |
| Gula Darah | 20-600 mg/dL |
| Berat/Tinggi | 20-300 kg, 50-250 cm |
| Aktivitas | 1-1440 menit |
| Makanan | Kalori: 1-10000 kal |

## 📈 Status Project

**Completion:** ~99% ✅

### Completed
- ✅ Authentication system (Email & Google Sign-In)
- ✅ 5 health tracking modules
- ✅ Dashboard & analytics
- ✅ Export functionality
- ✅ Dark mode support
- ✅ GPS run tracking
- ✅ Firebase integration
- ✅ **Health Charts** (Line & Bar charts untuk 5 kategori)
- ✅ **Health Reminders** (6 tipe reminder dengan notifikasi harian)

### Future Enhancements
- Cloud sync & backup
- Health trends & insights dengan AI
- Integration dengan Google Fit
- WearOS support

## 👥 Credits

Aplikasi ini dibuat untuk memenuhi tugas mata kuliah **Pemrograman Perangkat Bergerak**.

---

**Version:** 1.2.0
**Last Updated:** 28 November 2025
**Status:** ✅ Production Ready

*Your health, tracked with care.* 🏥💚
