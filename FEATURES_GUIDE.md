# LifeCare - Panduan Fitur Lengkap

## 📋 Daftar Isi
1. [Autentikasi & Keamanan](#autentikasi--keamanan)
2. [Tracking Kesehatan](#tracking-kesehatan)
3. [Grafik & Visualisasi](#grafik--visualisasi)
4. [Pengingat Kesehatan](#pengingat-kesehatan)
5. [Dashboard & Laporan](#dashboard--laporan)
6. [Profil & Pengaturan](#profil--pengaturan)

---

## 🔐 Autentikasi & Keamanan

### Login & Register
- **Email/Password**: Registrasi dengan validasi email & password strength
- **Google Sign-In**: Login cepat dengan akun Google (opsional)
- **Validasi Real-time**: Feedback langsung saat input data

### PIN Security
- **6-digit PIN**: Proteksi ekstra untuk data kesehatan
- **Session Management**: PIN valid 30 menit, auto-lock setelah timeout
- **Forgot PIN**: Reset PIN dengan verifikasi password Firebase
- **Change PIN**: Ganti PIN kapan saja dari menu Profil

### Data Encryption
- **AES256_GCM**: Enkripsi untuk data lokal (PIN, preferences, reminders)
- **Firebase Firestore**: Data tersimpan aman di cloud
- **Secure Session**: Session token management otomatis

---

## 📊 Tracking Kesehatan

### 1. Tekanan Darah
**Cara Input:**
1. Dari Home → Tap "Data Kesehatan"
2. Pilih tab "Tekanan Darah"
3. Input: Sistolik (70-250), Diastolik (40-150), Heart Rate
4. Pilih waktu & tanggal
5. Klik "Simpan Data"

**Informasi:**
- **Normal**: 120/80 mmHg
- **Hipertensi**: >140/90 mmHg
- **Hipotensi**: <90/60 mmHg
- Kategori otomatis ditampilkan berdasarkan nilai

### 2. Gula Darah
**Cara Input:**
1. Home → "Data Kesehatan" → Tab "Gula Darah"
2. Input level (20-600 mg/dL)
3. Pilih tipe: Puasa, Setelah Makan, Random, Pre-meal, Bedtime
4. Simpan

**Informasi:**
- **Normal (Puasa)**: 70-100 mg/dL
- **Pre-diabetes**: 100-125 mg/dL
- **Diabetes**: >126 mg/dL
- Rekomendasi ditampilkan otomatis

### 3. Berat & Tinggi (BMI)
**Cara Input:**
1. Home → "Data Kesehatan" → Tab "Berat & Tinggi"
2. Input berat (kg) dan tinggi (cm)
3. BMI dihitung otomatis
4. Simpan

**Kategori BMI:**
- **Underweight**: <18.5
- **Normal**: 18.5-24.9
- **Overweight**: 25-29.9
- **Obesitas**: ≥30

### 4. Aktivitas Fisik
**Cara Input Manual:**
1. Home → "Aktivitas Fisik"
2. Pilih "Tambah Manual"
3. Input jenis, durasi (menit), langkah, kalori
4. Simpan

**GPS Tracking (Lari/Jalan):**
1. Home → "Aktivitas Fisik" → "GPS Tracking"
2. Izinkan akses lokasi
3. Set target jarak/durasi
4. Tap "Mulai Tracking"
5. Lihat real-time: jarak, durasi, kecepatan, kalori
6. Tap "Selesai" → Review summary → Simpan

**Informasi:**
- Kalori dihitung berdasarkan durasi & intensitas
- GPS tracking hanya untuk outdoor
- Data disimpan dengan rute perjalanan

### 5. Asupan Makanan
**Cara Input:**
1. Home → Tap card "Asupan Makanan"
2. Input nama makanan, kalori, waktu
3. Input nutrisi: Protein, Karbohidrat, Lemak (opsional)
4. Simpan

**Informasi:**
- Target kalori harian: 2000 kal (default)
- Progress bar menunjukkan % dari target
- History makanan tersimpan per hari

---

## 📈 Grafik & Visualisasi

### Mengakses Charts
**Dari Home:**
1. Scroll ke bawah
2. Tap card **"Grafik Kesehatan"** (icon chart, warna orange)

### Kategori Chart

#### 1. Tekanan Darah
- **2 Line Charts**: Sistolik (merah) & Diastolik (biru)
- Menampilkan trend perubahan tekanan darah
- Garis horizontal menunjukkan nilai normal (120/80)

#### 2. Gula Darah
- **Line Chart**: Level gula darah
- Warna chart berubah sesuai kategori (hijau=normal, kuning=warning, merah=tinggi)
- Tracking tipe pengukuran (puasa, setelah makan, dll)

#### 3. BMI (Berat & Tinggi)
- **Line Chart**: Perubahan BMI dari waktu ke waktu
- Zona warna: Underweight, Normal, Overweight, Obesitas
- Membantu tracking progress diet/fitness

#### 4. Aktivitas Fisik
- **Bar Chart**: Total kalori terbakar per hari
- Agregasi dari semua aktivitas dalam satu hari
- Target harian ditampilkan sebagai garis referensi

#### 5. Asupan Makanan
- **Bar Chart**: Total kalori intake per hari
- Membantu tracking konsumsi kalori harian
- Warna bar berubah jika melebihi target

### Date Range Filter
- **7 Hari**: Trend mingguan (detail harian)
- **30 Hari**: Trend bulanan
- **90 Hari**: Trend 3 bulan (overview)

**Cara Ganti Filter:**
1. Tap chip filter di atas chart (7/30/90 hari)
2. Chart otomatis update dengan data baru

### Tips Menggunakan Charts
- ✅ Input data secara rutin untuk chart yang informatif
- ✅ Gunakan filter 7 hari untuk tracking detail
- ✅ Gunakan filter 90 hari untuk melihat trend jangka panjang
- ✅ Screenshot chart untuk dibagikan ke dokter

---

## ⏰ Pengingat Kesehatan

### Mengakses Reminder Settings
**Dari Home:**
1. Scroll ke bawah
2. Tap card **"Pengingat Kesehatan"** (icon bell, warna ungu)

### 6 Tipe Reminder

#### 1. Tekanan Darah (Pagi)
- **Default Time**: 08:00
- **Tujuan**: Cek tekanan darah di pagi hari (saat bangun tidur)
- **Notifikasi**: "Waktunya mengukur tekanan darah pagi Anda"

#### 2. Tekanan Darah (Malam)
- **Default Time**: 20:00
- **Tujuan**: Cek tekanan darah di malam hari (sebelum tidur)
- **Notifikasi**: "Waktunya mengukur tekanan darah malam Anda"

#### 3. Gula Darah
- **Default Time**: 07:00
- **Tujuan**: Cek gula darah puasa di pagi hari
- **Notifikasi**: "Saatnya cek gula darah Anda"

#### 4. Berat Badan
- **Default Time**: 07:30
- **Tujuan**: Timbang berat badan (1x per hari/minggu)
- **Notifikasi**: "Waktunya timbang berat badan"

#### 5. Aktivitas Fisik
- **Default Time**: 17:00
- **Tujuan**: Reminder untuk olahraga sore
- **Notifikasi**: "Sudahkah Anda berolahraga hari ini?"

#### 6. Asupan Makanan
- **Default Time**: 19:00
- **Tujuan**: Catat makanan yang dikonsumsi hari ini
- **Notifikasi**: "Jangan lupa catat makanan Anda"

### Cara Setting Reminder

#### Aktifkan Reminder:
1. Buka "Pengingat Kesehatan"
2. Toggle ON pada reminder yang diinginkan
3. Reminder langsung aktif dengan waktu default

#### Ubah Waktu:
1. Toggle ON reminder terlebih dahulu
2. Tap tombol waktu (misal: "08:00")
3. Time picker muncul
4. Pilih jam & menit baru
5. Tap OK
6. Waktu otomatis tersimpan

#### Matikan Reminder:
1. Toggle OFF pada reminder
2. Notifikasi akan berhenti

### Notification Behavior
- **Daily Repeating**: Reminder aktif setiap hari di waktu yang sama
- **Deep Link**: Tap notifikasi langsung buka screen terkait
  - BP/BS/Weight → Health Metrics Screen
  - Activity → Physical Activity Screen
  - Food → Food Intake Screen
- **Persistent**: Reminder tetap aktif meskipun app ditutup
- **Battery Optimized**: Menggunakan AlarmManager yang efficient

### Tips Reminder
- ✅ Set reminder sesuai rutinitas harian Anda
- ✅ Gunakan reminder BP pagi untuk tracking konsisten
- ✅ Reminder makanan membantu food journaling yang lengkap
- ✅ Jangan set terlalu banyak reminder agar tidak mengganggu

### Permission yang Diperlukan
- **Android 12+**: Exact Alarm permission
  - App akan meminta izin otomatis saat pertama kali set reminder
  - Izin ini diperlukan agar reminder muncul tepat waktu
- **Notifications**: Post notifications permission
  - Diperlukan untuk menampilkan notifikasi

---

## 🏠 Dashboard & Laporan

### Home Dashboard
**Sections:**
1. **Data Kesehatan Terbaru**: 3 data terakhir (BP, BS, BMI)
2. **Target Hari Ini**: Progress langkah, kalori, olahraga
3. **Ringkasan Hari Ini**: Total langkah, kalori, menit olahraga
4. **Monitoring Kesehatan**: Quick access cards

### Health Records (Riwayat)
**Akses:** Tap icon calendar di bottom navigation

**Fitur:**
- View semua data kesehatan dalam satu tempat
- Filter by tanggal
- Sort by newest/oldest
- Detail view untuk setiap entry
- Edit & delete data

### Export Data
**Cara Export:**
1. Buka "Riwayat Kesehatan"
2. Tap icon "Export"
3. Pilih format: CSV, JSON, atau TXT
4. File tersimpan di Downloads
5. Share via email/WhatsApp ke dokter

**Format Export:**
- **CSV**: Untuk analisis di Excel/Google Sheets
- **JSON**: Untuk backup/import ke aplikasi lain
- **TXT**: Human-readable format

---

## 👤 Profil & Pengaturan

### Edit Profil
1. Tap icon profil di bottom nav
2. Tap "Edit Profil"
3. Update: Nama, Umur, Jenis Kelamin
4. Simpan

### Ganti PIN
1. Profil → "Ganti PIN"
2. Input PIN lama
3. Input PIN baru (2x)
4. Verifikasi password Firebase
5. PIN berhasil diubah

### Dark Mode
1. Profil → Toggle "Dark Mode"
2. Atau gunakan "Ikuti Sistem" untuk auto dark mode

### Logout
1. Profil → "Logout"
2. Konfirmasi logout
3. Kembali ke login screen
4. Session & PIN terhapus

---

## 🎯 Tips & Best Practices

### Tracking Rutin
- ✅ Catat data di waktu yang sama setiap hari
- ✅ Gunakan reminder untuk konsistensi
- ✅ Input data segera setelah pengukuran
- ✅ Jangan skip hari, meskipun nilai normal

### Grafik yang Informatif
- ✅ Minimal 7 hari data untuk trend yang meaningful
- ✅ Perhatikan pola naik/turun di chart
- ✅ Bandingkan dengan garis normal/target
- ✅ Screenshot & diskusikan dengan dokter

### Keamanan Data
- ✅ Jangan share PIN dengan orang lain
- ✅ Logout jika device dipakai bersama
- ✅ Backup data secara berkala (export)
- ✅ Ganti PIN secara periodik

### Optimasi Baterai
- ✅ GPS tracking konsumsi baterai, gunakan seperlunya
- ✅ Matikan reminder yang tidak digunakan
- ✅ Logout dari app jika tidak dipakai lama

---

## ❓ FAQ

### Q: Apakah data saya aman?
**A:** Ya! Data dienkripsi dengan AES256_GCM dan tersimpan di Firebase yang aman.

### Q: Bisakah saya pakai tanpa internet?
**A:** Tracking data bisa offline, tapi login & sync memerlukan internet.

### Q: Bagaimana jika lupa PIN?
**A:** Gunakan "Lupa PIN" di PIN screen, verifikasi dengan password Firebase.

### Q: Apakah bisa export data ke PDF?
**A:** Saat ini support CSV/JSON/TXT. Export PDF dalam development.

### Q: Reminder tidak muncul, kenapa?
**A:** Pastikan izin notifikasi & exact alarm sudah diberikan. Cek di Settings → Apps → LifeCare → Permissions.

### Q: Bisa sync dengan Google Fit?
**A:** Fitur ini masih dalam development untuk versi mendatang.

### Q: Data tersimpan dimana?
**A:** Data utama di Firebase Firestore (cloud), data lokal di EncryptedSharedPreferences.

### Q: Apakah ada batasan jumlah data?
**A:** Tidak ada batasan, Anda bisa input data sebanyak mungkin.

---

## 📞 Support

Jika mengalami kendala atau memiliki pertanyaan:
- 📧 **Email**: [your-email]
- 📱 **GitHub Issues**: [github-repo-url]
- 📖 **Documentation**: README.md & IMPLEMENTATION_SUMMARY.md

---

**Version**: 1.2.0
**Last Updated**: 28 November 2025
**Status**: Production Ready

*Your health, tracked with care.* 🏥💚
