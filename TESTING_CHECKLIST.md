# LifeCare - Testing Checklist

## 📋 Testing Guide
Checklist ini untuk memastikan semua fitur bekerja dengan baik setelah implementasi Opsi A.

**Status Legend:**
- ✅ = Tested & Working
- ⏳ = In Progress
- ❌ = Failed
- ⚠️ = Need Attention

---

## 🔐 1. Authentication & Security

### Login/Register
- [ ] Register dengan email & password baru
- [ ] Validasi email format (test@email.com)
- [ ] Validasi password strength (min 6 karakter)
- [ ] Password visibility toggle berfungsi
- [ ] Register success → navigate ke PIN create
- [ ] Login dengan email/password yang sudah terdaftar
- [ ] Login success → navigate ke PIN verify (jika sudah set)
- [ ] Login success → navigate ke PIN create (jika belum set)
- [ ] Error handling: wrong password
- [ ] Error handling: email not found
- [ ] Error handling: network error

### Google Sign-In
- [ ] Google Sign-In button tampil
- [ ] Tap button → Google account picker muncul
- [ ] Select account → sign in success
- [ ] Navigate ke PIN screen
- [ ] Error handling: cancelled by user
- [ ] Error handling: no internet

### PIN System
- [ ] Create PIN: input 6 digit
- [ ] Create PIN: confirm PIN match
- [ ] Create PIN: error jika tidak match
- [ ] Verify PIN: input correct PIN → Home
- [ ] Verify PIN: wrong PIN → error message
- [ ] Verify PIN: 3x wrong → Forgot PIN muncul
- [ ] Forgot PIN: verifikasi password → reset success
- [ ] PIN session valid 30 menit
- [ ] PIN session expired → re-verify
- [ ] Change PIN dari Profil → success

---

## 📊 2. Health Tracking

### Tekanan Darah
- [ ] Open Health Metrics → Tab Tekanan Darah
- [ ] Input sistolik (100-150 range)
- [ ] Input diastolik (60-90 range)
- [ ] Input heart rate (60-100 range)
- [ ] Pilih tanggal & waktu
- [ ] Simpan data → success message
- [ ] Data muncul di dashboard "Data Terbaru"
- [ ] Data muncul di Health Records
- [ ] Kategori otomatis (Normal/Hipertensi/Hipotensi)
- [ ] Edit data existing
- [ ] Delete data

### Gula Darah
- [ ] Open Tab Gula Darah
- [ ] Input level (70-120 range)
- [ ] Pilih tipe pengukuran (Puasa, Setelah Makan, dll)
- [ ] Simpan data
- [ ] Kategori otomatis (Normal/Pre-diabetes/Diabetes)
- [ ] Data tersimpan dengan tipe yang benar

### Berat & Tinggi (BMI)
- [ ] Open Tab Berat & Tinggi
- [ ] Input berat (kg)
- [ ] Input tinggi (cm)
- [ ] BMI kalkulasi otomatis
- [ ] Kategori BMI benar (Underweight/Normal/Overweight/Obesitas)
- [ ] Simpan data
- [ ] BMI muncul di dashboard

### Aktivitas Fisik
**Manual Input:**
- [ ] Open Aktivitas Fisik
- [ ] Tap "Tambah Manual"
- [ ] Input jenis aktivitas (dropdown)
- [ ] Input durasi (menit)
- [ ] Input langkah
- [ ] Input kalori
- [ ] Simpan → data tersimpan

**GPS Tracking:**
- [ ] Tap "GPS Tracking"
- [ ] Permission request muncul
- [ ] Grant permission
- [ ] Setup screen: set target jarak/durasi
- [ ] Tap "Mulai Tracking"
- [ ] Live tracking menampilkan:
  - [ ] Real-time distance
  - [ ] Duration timer
  - [ ] Current speed
  - [ ] Calories burned
  - [ ] Map with route (jika ada)
- [ ] Tap "Pause" → tracking paused
- [ ] Tap "Resume" → tracking resumed
- [ ] Tap "Selesai" → summary screen
- [ ] Summary menampilkan total stats
- [ ] Save → data tersimpan dengan route

### Asupan Makanan
- [ ] Open Food Intake
- [ ] Input nama makanan
- [ ] Input kalori
- [ ] Input protein, karbo, lemak (opsional)
- [ ] Pilih waktu (Sarapan/Makan Siang/Makan Malam/Snack)
- [ ] Simpan data
- [ ] Total kalori hari ini update
- [ ] Progress bar menunjukkan % dari target

---

## 📈 3. Health Charts (NEW FEATURE)

### Akses Charts
- [ ] Dari Home, card "Grafik Kesehatan" tampil
- [ ] Icon ShowChart (📊) tampil
- [ ] Background orange
- [ ] Tap card → navigate ke Charts screen

### Charts Screen UI
- [ ] TopBar dengan title "Grafik Kesehatan"
- [ ] Back button berfungsi
- [ ] 5 tab kategori tampil:
  - [ ] Tekanan Darah
  - [ ] Gula Darah
  - [ ] BMI
  - [ ] Aktivitas Fisik
  - [ ] Asupan Makanan
- [ ] Tab scrollable (bisa swipe)
- [ ] 3 filter chips tampil (7/30/90 hari)

### Tekanan Darah Chart
- [ ] Tab "Tekanan Darah" selected
- [ ] 2 line charts tampil (Sistolik & Diastolik)
- [ ] Chart title: "Tekanan Darah Sistolik" & "Diastolik"
- [ ] Y-axis label: "mmHg"
- [ ] X-axis menampilkan tanggal
- [ ] Line smooth & connect points
- [ ] Grid lines tampil
- [ ] **Test dengan data kosong**: Empty state message tampil
- [ ] **Test dengan 1 data**: Single point tampil
- [ ] **Test dengan 7+ data**: Line chart terbentuk
- [ ] Filter 7 hari: menampilkan data 7 hari terakhir
- [ ] Filter 30 hari: menampilkan data 30 hari terakhir
- [ ] Filter 90 hari: menampilkan data 90 hari terakhir
- [ ] Auto-scaling: Y-axis adjust dengan min/max values

### Gula Darah Chart
- [ ] Tab "Gula Darah" selected
- [ ] Line chart tampil
- [ ] Y-axis: mg/dL
- [ ] Data points sesuai dengan input
- [ ] Filter berfungsi
- [ ] Empty state handling

### BMI Chart
- [ ] Tab "BMI" selected
- [ ] Line chart tampil
- [ ] Y-axis: BMI value
- [ ] BMI calculated correctly (weight / (height^2))
- [ ] Filter berfungsi

### Aktivitas Fisik Chart
- [ ] Tab "Aktivitas Fisik" selected
- [ ] **Bar chart** tampil (bukan line)
- [ ] Y-axis: Kalori
- [ ] X-axis: Tanggal
- [ ] Bar chart menunjukkan **total kalori per hari**
- [ ] Multiple activities dalam 1 hari → digabung jadi 1 bar
- [ ] Bar dengan rounded corners
- [ ] Value label di atas bar
- [ ] Filter berfungsi

### Asupan Makanan Chart
- [ ] Tab "Asupan Makanan" selected
- [ ] **Bar chart** tampil
- [ ] Y-axis: Kalori
- [ ] Total kalori per hari
- [ ] Multiple food entries → agregasi per hari
- [ ] Filter berfungsi

### Chart Interactions
- [ ] Tap tab → chart change smoothly
- [ ] Tap filter chip → chart update
- [ ] Scroll page → all charts visible
- [ ] No lag atau freeze
- [ ] No memory leak (test dengan switch tab multiple times)

---

## ⏰ 4. Health Reminders (NEW FEATURE)

### Akses Reminders
- [ ] Dari Home, card "Pengingat Kesehatan" tampil
- [ ] Icon Notifications (🔔) tampil
- [ ] Background ungu/purple
- [ ] Tap card → navigate ke Reminders screen

### Reminders Screen UI
- [ ] TopBar dengan title "Pengaturan Pengingat"
- [ ] Back button berfungsi
- [ ] Info card header tampil dengan penjelasan
- [ ] 6 reminder cards tampil:
  - [ ] Tekanan Darah (Pagi) - default 08:00
  - [ ] Tekanan Darah (Malam) - default 20:00
  - [ ] Gula Darah - default 07:00
  - [ ] Berat Badan - default 07:30
  - [ ] Aktivitas Fisik - default 17:00
  - [ ] Asupan Makanan - default 19:00
- [ ] Setiap card menampilkan:
  - [ ] Nama reminder
  - [ ] Deskripsi
  - [ ] Toggle switch
  - [ ] Time button (jika enabled)

### Toggle Reminder
- [ ] Tap toggle ON → reminder enabled
- [ ] Time button muncul setelah enabled
- [ ] Default time tampil (format HH:mm)
- [ ] Tap toggle OFF → reminder disabled
- [ ] Time button hilang
- [ ] State tersimpan (test dengan keluar & masuk lagi)

### Set Custom Time
- [ ] Reminder dalam state enabled
- [ ] Tap time button
- [ ] **TimePickerDialog muncul**
- [ ] Picker menampilkan jam (00-23) dan menit (00-59)
- [ ] Select jam: 10
- [ ] Select menit: 30
- [ ] Tap OK
- [ ] Time button update jadi "10:30"
- [ ] Custom time tersimpan
- [ ] Test dengan keluar app → time masih sama

### Alarm Scheduling
- [ ] Enable reminder → AlarmManager scheduled
- [ ] Disable reminder → AlarmManager cancelled
- [ ] Change time → alarm re-scheduled
- [ ] **Android 12+ Permission Test:**
  - [ ] First time enable → permission dialog muncul
  - [ ] Grant permission → alarm scheduled
  - [ ] Deny permission → error message
- [ ] Alarm persists after app closed
- [ ] Alarm triggers daily at set time

### Notification Test
**Setup:**
1. Enable 1 reminder (e.g., Aktivitas Fisik at current time + 2 min)
2. Wait for notification

**Check:**
- [ ] Notification muncul tepat waktu
- [ ] Notification title correct (e.g., "Pengingat Aktivitas Fisik")
- [ ] Notification message correct
- [ ] Notification icon tampil
- [ ] Tap notification → app opens
- [ ] **Deep link test**: Opens correct screen
  - [ ] BP/BS/Weight reminder → Health Metrics
  - [ ] Activity reminder → Physical Activity
  - [ ] Food reminder → Food Intake
- [ ] Swipe dismiss → notification hilang
- [ ] Next day → notification muncul lagi (daily repeat)

### Data Persistence
- [ ] Enable 3 reminders dengan custom time
- [ ] Close app completely (force stop)
- [ ] Reopen app → go to Reminders
- [ ] All 3 reminders still enabled
- [ ] Custom times unchanged
- [ ] Alarms still scheduled

### Encrypted Storage Test
- [ ] Enable reminders → data saved
- [ ] Check EncryptedSharedPreferences (via code/debug)
- [ ] Data encrypted (not plain text)
- [ ] JSON format correct untuk ReminderConfig list

---

## 🏠 5. Dashboard & Integration

### Home Dashboard Updates
- [ ] 2 new cards tampil di grid:
  - [ ] Row 1: Data Kesehatan, Aktivitas Fisik
  - [ ] Row 2: **Grafik Kesehatan** (NEW), **Pengingat Kesehatan** (NEW)
- [ ] Icon & colors konsisten dengan Material Design 3
- [ ] Tap "Grafik Kesehatan" → Charts screen
- [ ] Tap "Pengingat Kesehatan" → Reminders screen
- [ ] Existing cards masih berfungsi normal
- [ ] Bottom navigation masih berfungsi

### Navigation Flow
- [ ] Home → Charts → Back → Home
- [ ] Home → Reminders → Back → Home
- [ ] Home → Health Metrics → Charts → Back → Metrics → Back → Home
- [ ] No navigation stack issues
- [ ] No memory leaks

### Data Consistency
- [ ] Input data di Health Metrics
- [ ] Check Chart → data muncul
- [ ] Check Health Records → data muncul
- [ ] Check Dashboard → data terbaru update
- [ ] Export data → data lengkap

---

## 🎨 6. UI/UX & Theme

### Dark Mode
- [ ] Toggle dark mode ON
- [ ] Charts screen: background dark, text light
- [ ] Reminders screen: cards dengan dark background
- [ ] Chart lines visible di dark mode
- [ ] Grid lines visible
- [ ] No white flash saat switch screen

### Material Design 3
- [ ] Filter chips menggunakan MD3 style
- [ ] Cards dengan proper elevation
- [ ] TopAppBar dengan MD3 theming
- [ ] Tab indicators smooth
- [ ] Ripple effects on buttons

### Responsiveness
- [ ] Test di different screen sizes
- [ ] Charts scale properly
- [ ] Text tidak terpotong
- [ ] Cards layout adaptive
- [ ] No overflow issues

---

## 🔒 7. Security & Performance

### Security
- [ ] Reminder data encrypted (EncryptedSharedPreferences)
- [ ] PIN session timeout works (30 min)
- [ ] No sensitive data in logs
- [ ] PendingIntent FLAG_IMMUTABLE (Android 12+)

### Performance
- [ ] Charts render smooth (no lag)
- [ ] Switch tabs fast (<500ms)
- [ ] Filter change instant
- [ ] No ANR (Application Not Responding)
- [ ] Memory usage normal (<100MB for charts)
- [ ] No memory leaks (test dengan LeakCanary jika ada)

### Battery
- [ ] AlarmManager tidak drain battery
- [ ] GPS tracking off saat tidak digunakan
- [ ] No background services when idle

---

## 📱 8. Device Compatibility

### Android Versions
- [ ] Android 7.0 (API 24) - Min SDK
- [ ] Android 8.0 (API 26) - Notification channels
- [ ] Android 10 (API 29) - Background location
- [ ] Android 12 (API 31) - Exact alarm permission
- [ ] Android 14 (API 34) - Target SDK

### Screen Sizes
- [ ] Phone (5-6 inch)
- [ ] Tablet (7-10 inch)
- [ ] Landscape orientation

---

## 🐛 9. Error Handling

### Charts
- [ ] Empty data → Empty state message
- [ ] 1 data point → Single point (no crash)
- [ ] Invalid data (null values) → handled gracefully
- [ ] Network error saat load → retry option

### Reminders
- [ ] Permission denied → user-friendly message
- [ ] Invalid time → validation error
- [ ] Alarm not scheduled → error logged

### General
- [ ] Network offline → cached data shown
- [ ] Firebase down → local data works
- [ ] App crash → state restored

---

## ✅ 10. Final Checks

### Build
- [✅] Build successful (exit code 0)
- [ ] No lint warnings (critical)
- [ ] APK size reasonable (<20MB)
- [ ] No unused resources
- [ ] ProGuard rules correct (jika enable)

### Documentation
- [✅] README.md updated
- [✅] IMPLEMENTATION_SUMMARY.md created
- [✅] FEATURES_GUIDE.md created
- [✅] TESTING_CHECKLIST.md created
- [ ] Code comments adequate
- [ ] API documentation (jika ada)

### Code Quality
- [ ] No TODO comments untuk critical features
- [ ] No hardcoded strings (gunakan strings.xml)
- [ ] No magic numbers
- [ ] Proper error handling
- [ ] Memory leaks fixed

---

## 📊 Testing Summary

| Category | Total Tests | Passed | Failed | Pending |
|----------|-------------|--------|--------|---------|
| Authentication | 21 | 0 | 0 | 21 |
| Health Tracking | 40 | 0 | 0 | 40 |
| **Charts (NEW)** | 35 | 0 | 0 | 35 |
| **Reminders (NEW)** | 30 | 0 | 0 | 30 |
| Dashboard | 10 | 0 | 0 | 10 |
| UI/UX | 12 | 0 | 0 | 12 |
| Security | 8 | 0 | 0 | 8 |
| Device Compat | 8 | 0 | 0 | 8 |
| Error Handling | 10 | 0 | 0 | 10 |
| **TOTAL** | **174** | **0** | **0** | **174** |

---

## 🎯 Priority Testing Order

### High Priority (Must Test First)
1. ✅ Build success
2. Charts screen basic functionality
3. Reminders enable/disable
4. Navigation Home → Charts/Reminders
5. Data persistence

### Medium Priority
6. Filter date ranges
7. Custom time setting
8. Notification delivery
9. Dark mode
10. Error handling

### Low Priority
11. Performance optimization
12. Edge cases
13. Different screen sizes
14. Memory leaks

---

## 📝 Testing Notes

**Tester Name:** _________________
**Date:** _________________
**Device:** _________________
**Android Version:** _________________

**Issues Found:**
```
1.
2.
3.
```

**Comments:**
```


```

---

**Testing Version:** 1.2.0
**Last Updated:** 28 November 2025
**Status:** Ready for Testing
