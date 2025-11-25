# LifeCare - Testing Checklist & Manual Testing Guide

**Version**: 1.2.0
**Last Updated**: 25 November 2025
**Build Status**: ✅ Successful
**Installed on**: Emulator-5554

---

## 📋 PRE-TESTING VERIFICATION

### ✅ Build & Installation Status
- [x] **Clean build**: Successful (9 seconds)
- [x] **APK generated**: `app/build/outputs/apk/debug/app-debug.apk`
- [x] **Installation**: Success on emulator-5554
- [x] **google-services.json**: Present in `app/` folder
- [x] **Firebase configured**: Project `lifecaree28-8cc63`
- [x] **SHA-1 obtained**: `B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA`

### ✅ Key Files Verification
```
✅ app/google-services.json (684 bytes)
✅ app/build.gradle.kts (2994 bytes) - with Google Services plugin
✅ build.gradle.kts (304 bytes) - with Google Services plugin
✅ README.md (16116 bytes)
✅ GOOGLE_SIGNIN_SETUP.md (7971 bytes)
✅ GOOGLE_SIGNIN_NEXT_STEPS.md (7912 bytes)
✅ QUICK_START.md (6226 bytes)
```

### ✅ Source Code Files
```
Total Kotlin files: 19
- MainActivity.kt
- Login.kt
- Register.kt
- Home.kt
- PINScreen.kt
- GoogleSignInHelper.kt
- HealthDataManager.kt
- BloodPressureScreen.kt
- BloodSugarScreen.kt
- BodyMetricsScreen.kt
- PhysicalActivityScreen.kt
- FoodIntakeScreen.kt
- HealthRecordsScreen.kt
- ProfileScreen.kt
- ChangePINScreen.kt
- + 4 UI theme files
```

---

## 🧪 TESTING SCENARIOS

### 1. AUTHENTICATION TESTING

#### A. Manual Registration (Primary Method) ✅ RECOMMENDED
**Test Steps:**
1. [ ] Launch app (should show Login screen)
2. [ ] Tap "Sign up" button
3. [ ] Fill registration form:
   - Name: "Test User"
   - Email: "test@email.com"
   - Password: "test123"
   - Confirm Password: "test123"
   - Age: 25
   - Gender: Male
4. [ ] Tap "Buat Akun"
5. [ ] Should navigate to PIN creation screen
6. [ ] Enter 6-digit PIN: "123456"
7. [ ] Confirm PIN: "123456"
8. [ ] Should navigate to Home/Dashboard

**Expected Results:**
- ✅ All validation messages appear correctly
- ✅ Password minimum 6 characters enforced
- ✅ Age minimum 13 enforced
- ✅ Gender selection required
- ✅ PIN exactly 6 digits enforced
- ✅ Data saved with encryption

**Validation Tests:**
- [ ] Try password < 6 chars (should show error)
- [ ] Try age < 13 (should show error)
- [ ] Try mismatched passwords (should show error)
- [ ] Try empty fields (should show error)
- [ ] Try mismatched PIN confirmation (should show error)

#### B. Manual Login
**Test Steps:**
1. [ ] Launch app
2. [ ] Enter registered credentials:
   - Email: "test@email.com"
   - Password: "test123"
3. [ ] Tap "Login"
4. [ ] Enter PIN: "123456"
5. [ ] Should navigate to Home/Dashboard

**Expected Results:**
- ✅ Correct credentials → PIN screen
- ✅ Wrong credentials → Error message
- ✅ Correct PIN → Dashboard
- ✅ Wrong PIN → Error message

#### C. Google Sign-In Testing (Optional)
**Test Steps:**
1. [ ] Launch app
2. [ ] Tap "Sign in with Google" button
3. [ ] Should show Google account picker (if configured)
   - OR show "No credentials available" error (if SHA-1 not registered)

**Expected Results:**
- ⚠️ **If SHA-1 NOT registered in Firebase**:
  - Error: "No credentials available" → **EXPECTED BEHAVIOR**
  - User should use Manual Registration instead
- ✅ **If SHA-1 registered in Firebase**:
  - Google account picker appears
  - Select account → Create PIN → Dashboard

**Configuration Status:**
- ✅ Code: Implemented
- ✅ Client ID: Configured (`573764659302-...`)
- ✅ google-services.json: Installed
- ✅ Gradle plugin: Configured
- ⚠️ SHA-1: Obtained but NOT YET registered in Firebase Console

**To Enable Google Sign-In (Optional):**
1. Open [Firebase Console](https://console.firebase.google.com/)
2. Select project: `lifecaree28-8cc63`
3. Project Settings → Your apps → Android app
4. Add SHA-1: `B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA`
5. Wait 5-10 minutes for propagation
6. Uninstall & reinstall app
7. Test Google Sign-In

---

### 2. HEALTH TRACKING MODULES TESTING

#### A. Blood Pressure Tracking
**Test Steps:**
1. [ ] Navigate to Dashboard
2. [ ] Tap "Tekanan Darah" card or FAB button
3. [ ] Fill form:
   - Systolic: 120 mmHg
   - Diastolic: 80 mmHg
   - Heart Rate: 75 BPM (optional)
4. [ ] Tap "Simpan"
5. [ ] Should show success message
6. [ ] Data should appear in list and dashboard

**Validation Tests:**
- [ ] Systolic < 70 (should show error)
- [ ] Systolic > 250 (should show error)
- [ ] Diastolic < 40 (should show error)
- [ ] Diastolic > 150 (should show error)
- [ ] Systolic ≤ Diastolic (should show error: "Sistolik harus lebih besar dari diastolik")
- [ ] Empty fields (should show error)

**Category Detection:**
- [ ] Systolic ≥ 180 or Diastolic ≥ 120 → Crisis warning
- [ ] Systolic ≥ 140 or Diastolic ≥ 90 → Stage 2 warning
- [ ] Systolic ≥ 130 or Diastolic ≥ 80 → Stage 1 warning

#### B. Blood Sugar Tracking
**Test Steps:**
1. [ ] Tap "Gula Darah" card or FAB
2. [ ] Fill form:
   - Blood Sugar Level: 100 mg/dL
   - Measurement Type: "Puasa"
3. [ ] Tap "Simpan"
4. [ ] Check data appears in list

**Validation Tests:**
- [ ] Level < 20 (should show error)
- [ ] Level > 600 (should show error)
- [ ] Empty fields (should show error)
- [ ] Measurement type required

**Warning Detection:**
- [ ] Puasa ≥ 126 → Diabetes warning
- [ ] Puasa 100-125 → Prediabetes warning
- [ ] Level < 70 → Hypoglycemia warning

#### C. Body Metrics (BMI) Tracking
**Test Steps:**
1. [ ] Tap "Berat & Tinggi" card or FAB
2. [ ] Fill form:
   - Weight: 70 kg
   - Height: 170 cm
3. [ ] Check BMI preview appears automatically
4. [ ] Tap "Simpan"
5. [ ] Verify BMI calculation: 70 / (1.7 × 1.7) = 24.2

**Validation Tests:**
- [ ] Weight < 20 (should show error)
- [ ] Weight > 300 (should show error)
- [ ] Height < 50 (should show error)
- [ ] Height > 250 (should show error)

**BMI Categories:**
- [ ] BMI < 18.5 → Underweight (Yellow)
- [ ] BMI 18.5-24.9 → Normal (Green)
- [ ] BMI 25-29.9 → Overweight (Orange)
- [ ] BMI ≥ 30 → Obese (Red)

#### D. Physical Activity Tracking
**Test Steps:**
1. [ ] Tap "Aktivitas Fisik" card or FAB
2. [ ] Fill form:
   - Activity Type: "Lari"
   - Duration: 30 minutes
   - Steps: 5000 (optional)
   - Calories: (auto-calculated or manual)
3. [ ] Tap "Simpan"

**Validation Tests:**
- [ ] Duration < 1 (should show error)
- [ ] Duration > 1440 (should show error)
- [ ] Activity type required

**Auto-Calorie Estimation:**
- [ ] Different activity types show different calorie estimates
- [ ] Duration affects calorie calculation

#### E. Food Intake Tracking
**Test Steps:**
1. [ ] Tap "Asupan Makanan" card or FAB
2. [ ] Fill form:
   - Food Name: "Nasi Goreng"
   - Calories: 500 kcal
   - Meal Type: "Makan Siang"
   - Protein: 20g (optional)
   - Carbs: 60g (optional)
   - Fat: 15g (optional)
3. [ ] Tap "Simpan"

**Validation Tests:**
- [ ] Food name < 2 chars (should show error)
- [ ] Food name > 50 chars (should show error)
- [ ] Calories < 1 (should show error)
- [ ] Calories > 10000 (should show error)
- [ ] Negative nutrition values (should show error)
- [ ] Meal type required

---

### 3. DASHBOARD TESTING

**Test Steps:**
1. [ ] Open Dashboard (Home screen)
2. [ ] Verify sections display:
   - Welcome message with user name
   - Latest health data summary
   - Daily goals progress bars:
     - Steps progress (target: 10,000)
     - Calories progress (target: 2,000)
     - Exercise minutes progress (target: 30)
   - Today's summary
   - This week's statistics
   - Health tips

**Expected Results:**
- ✅ All data from tracking modules appear
- ✅ Progress bars update in real-time
- ✅ Percentages calculated correctly
- ✅ Health tips are relevant
- ✅ Quick action cards work

---

### 4. HEALTH RECORDS TESTING

#### A. Tab Navigation
**Test Steps:**
1. [ ] Open Health Records screen
2. [ ] Test all tabs:
   - [ ] "Semua" → Shows all records
   - [ ] "Berat & Tinggi" → Shows only BMI records
   - [ ] "Tekanan Darah" → Shows only blood pressure
   - [ ] "Gula Darah" → Shows only blood sugar
   - [ ] "Aktivitas" → Shows only physical activity
   - [ ] "Makanan" → Shows only food intake

#### B. Filter Testing
**Test Steps:**
1. [ ] Tap Filter icon
2. [ ] Test each filter:
   - [ ] "Semua" → All dates
   - [ ] "Hari Ini" → Today's records only
   - [ ] "Minggu Ini" → This week's records
   - [ ] "Bulan Ini" → This month's records

#### C. Sort Testing
**Test Steps:**
1. [ ] Tap Sort icon
2. [ ] Test each sort option:
   - [ ] "Terbaru" → Newest first
   - [ ] "Terlama" → Oldest first
   - [ ] "Nilai Tertinggi" → Highest values first
   - [ ] "Nilai Terendah" → Lowest values first

#### D. Delete Testing
**Test Steps:**
1. [ ] Tap delete icon on a record
2. [ ] Confirm deletion dialog appears
3. [ ] Tap "Ya, Hapus"
4. [ ] Record should be removed
5. [ ] Toast message appears

**Note**: Current implementation may be placeholder - verify actual deletion from storage.

---

### 5. EXPORT FUNCTIONALITY TESTING

**Test Steps:**
1. [ ] Open Health Records screen
2. [ ] Tap Share/Export icon
3. [ ] Test each export format:
   - [ ] **CSV**: Should download to Downloads folder
   - [ ] **JSON**: Should download with proper structure
   - [ ] **TXT**: Should download with readable format
4. [ ] Check Downloads folder for files
5. [ ] Verify file names include timestamp

**Expected File Format:**
- CSV: `lifecare_export_YYYYMMDD_HHMMSS.csv`
- JSON: `lifecare_export_YYYYMMDD_HHMMSS.json`
- TXT: `lifecare_export_YYYYMMDD_HHMMSS.txt`

**Validation:**
- [ ] Files contain all health records
- [ ] Data is properly formatted
- [ ] All categories included
- [ ] Timestamps accurate

---

### 6. PROFILE MANAGEMENT TESTING

#### A. View Profile
**Test Steps:**
1. [ ] Tap Profile icon in bottom navigation
2. [ ] Verify displayed information:
   - [ ] Full name
   - [ ] Email
   - [ ] Age
   - [ ] Gender
   - [ ] Current PIN indicator

#### B. Edit Profile
**Test Steps:**
1. [ ] Tap "Edit Profil" button
2. [ ] Update information:
   - Name: "Updated Name"
   - Age: 30
   - Gender: Female
3. [ ] Tap "Simpan"
4. [ ] Verify changes reflected immediately

**Validation Tests:**
- [ ] Name < 3 chars (should show error)
- [ ] Name > 50 chars (should show error)
- [ ] Age < 1 (should show error)
- [ ] Age > 150 (should show error)
- [ ] Gender required

#### C. Change PIN
**Test Steps:**
1. [ ] Tap "Ubah PIN"
2. [ ] Enter old PIN: "123456"
3. [ ] Enter new PIN: "654321"
4. [ ] Confirm new PIN: "654321"
5. [ ] Tap "Simpan PIN Baru"
6. [ ] Logout and login again
7. [ ] Should require new PIN: "654321"

**Validation Tests:**
- [ ] Old PIN incorrect (should show error)
- [ ] New PIN < 6 digits (should show error)
- [ ] New PIN ≠ Confirm PIN (should show error)

#### D. Logout & Clear Data
**Test Steps:**
1. [ ] Tap "Hapus Semua Data & Logout"
2. [ ] Confirmation dialog appears
3. [ ] Tap "Ya, Hapus Semua"
4. [ ] Should return to Login screen
5. [ ] All data should be cleared
6. [ ] Previous credentials should not work

---

### 7. NAVIGATION TESTING

**Test Steps:**
1. [ ] Test bottom navigation bar:
   - [ ] Home icon → Dashboard
   - [ ] Add icon → Floating action button menu
   - [ ] Records icon → Health Records
   - [ ] Profile icon → Profile screen
2. [ ] Verify navigation persistent across screens
3. [ ] Test back button navigation
4. [ ] Test floating action button (FAB) on each screen

---

### 8. DATA PERSISTENCE TESTING

**Test Steps:**
1. [ ] Input various health records
2. [ ] Close app completely (swipe from recent apps)
3. [ ] Reopen app
4. [ ] Enter PIN
5. [ ] Verify all data still present

**Expected Results:**
- ✅ All records preserved
- ✅ User profile intact
- ✅ Dashboard shows correct data
- ✅ Encryption maintained

---

### 9. ERROR HANDLING TESTING

**Test Scenarios:**
1. [ ] Empty form submissions → Error messages
2. [ ] Invalid data ranges → Range validation errors
3. [ ] Network issues (Google Sign-In) → Graceful error
4. [ ] Rapid button clicking → No duplicate entries
5. [ ] Invalid PIN attempts → Error message
6. [ ] Missing required fields → Clear error indicators

---

### 10. UI/UX TESTING

**Visual Tests:**
1. [ ] All text readable (no overlap)
2. [ ] Colors consistent with design system:
   - Primary: #5DCCB4 (Turquoise)
   - Background: #F8F9FA
   - Error: Red
3. [ ] Icons display correctly
4. [ ] Progress bars animate smoothly
5. [ ] Dialogs centered and readable
6. [ ] Forms properly aligned
7. [ ] Category color coding works:
   - Blood Pressure: Pink
   - Blood Sugar: Purple
   - BMI: Blue
   - Activity: Green
   - Food: Orange

**Interaction Tests:**
1. [ ] Buttons respond to clicks
2. [ ] Form inputs accept keyboard input
3. [ ] Dropdowns expand properly
4. [ ] Scrolling smooth in lists
5. [ ] No UI freezing

---

## 📊 TESTING RESULTS SUMMARY

### Critical Features (Must Work)
- [ ] Manual Registration ✅ PRIMARY METHOD
- [ ] Manual Login ✅ PRIMARY METHOD
- [ ] PIN Creation & Verification
- [ ] All 5 Health Tracking Modules
- [ ] Dashboard Display
- [ ] Data Persistence (Encryption)
- [ ] Profile Management
- [ ] Navigation

### Important Features (Should Work)
- [ ] Health Records Filter & Sort
- [ ] Export Functionality
- [ ] Data Validation
- [ ] Error Handling
- [ ] Change PIN
- [ ] Logout & Clear Data

### Optional Features (May Need Configuration)
- [ ] Google Sign-In (⚠️ Requires SHA-1 registration)
- [ ] Delete Records (May be placeholder)

---

## 🐛 KNOWN ISSUES & NOTES

### Expected Behaviors:
1. **Google Sign-In Error**: "No credentials available" is **EXPECTED** until SHA-1 is registered in Firebase Console
   - **Solution**: Use Manual Registration (fully functional)
   - **Alternative**: Register SHA-1 in Firebase (see GOOGLE_SIGNIN_NEXT_STEPS.md)

2. **Deprecation Warnings**: Build shows deprecation warnings but **does not affect functionality**
   - Locale constructor
   - ArrowBack icon
   - EncryptedSharedPreferences
   - These are cosmetic warnings only

3. **Delete Functionality**: May be placeholder implementation - verify actual deletion from encrypted storage

### Configuration Status:
```
✅ Build System: Working (SDK 36, AGP 8.9.1)
✅ Firebase Integration: Complete
✅ Google Services Plugin: Configured (v4.4.4)
✅ Firebase BoM: Added (v34.6.0)
✅ Encryption: AES256_GCM
⚠️ SHA-1 Registration: Pending (optional)
```

---

## ✅ FINAL CHECKLIST

Before considering testing complete:
- [ ] All authentication methods tested
- [ ] All 5 tracking modules tested
- [ ] Dashboard displays correctly
- [ ] Health records filter/sort works
- [ ] Export functionality works
- [ ] Profile edit works
- [ ] PIN change works
- [ ] Data persists after app restart
- [ ] Logout clears data
- [ ] No critical bugs found

---

## 📝 TEST EXECUTION LOG

**Date**: _________________
**Tester**: _________________
**Environment**: Emulator-5554
**Build**: 1.2.0

**Test Results**:
- Total Tests Planned: ___________
- Tests Passed: ___________
- Tests Failed: ___________
- Tests Skipped: ___________

**Critical Bugs Found**:
1. _________________________________
2. _________________________________
3. _________________________________

**Minor Issues Found**:
1. _________________________________
2. _________________________________
3. _________________________________

**Overall Assessment**:
- [ ] Ready for Demo
- [ ] Ready for Submission
- [ ] Needs Fixes

---

## 🎯 RECOMMENDED TESTING ORDER

1. **First Priority** (Core Functionality):
   - Manual Registration & Login
   - PIN Security
   - All 5 Health Tracking Modules
   - Dashboard Display

2. **Second Priority** (Important Features):
   - Health Records (Filter/Sort)
   - Profile Management
   - Data Persistence

3. **Third Priority** (Nice to Have):
   - Export Functionality
   - Google Sign-In (if SHA-1 registered)
   - Advanced Validation

---

**Testing Status**: ⏳ Ready to Begin
**App Status**: ✅ Installed and Ready
**Documentation**: ✅ Complete

**Good luck with testing!** 🎉
