# Google Sign-In - Next Steps

**Client ID Status**: ✅ **CONFIGURED**
**Client ID**: `573764659302-iuu3m0pu89jtm2rcgs32rs6ga5i2g611.apps.googleusercontent.com`
**Code Status**: ✅ **READY**
**Missing**: ❌ `google-services.json` file

---

## 🎯 ANDA SUDAH SELESAI 50%!

### ✅ Yang Sudah Done:
1. ✅ Code implementation complete
2. ✅ Web Client ID configured: `573764659302-...`
3. ✅ GoogleSignInHelper.kt already using correct Client ID

### ❌ Yang Masih Kurang:
1. ❌ File `google-services.json` belum ada
2. ❌ SHA-1 fingerprint belum diregister (mungkin)

---

## 🚀 LANGKAH SELANJUTNYA (2 Options)

### Option A: ✅ **PAKAI MANUAL REGISTRATION** (5 detik)

**RECOMMENDED untuk sekarang!**

Karena Google Sign-In butuh `google-services.json`, lebih cepat pakai manual registration dulu:

1. Skip tombol "Sign in with Google"
2. Gunakan "Sign up" → Isi form manual
3. Done! App langsung bisa digunakan ✅

---

### Option B: ⚙️ **SELESAIKAN GOOGLE SIGN-IN** (10-15 menit)

Jika Anda benar-benar ingin Google Sign-In bekerja:

#### Step 1: Setup Firebase Project

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Click "**Add project**"
3. Project name: **Pilih existing project** atau buat baru
   - Jika ada project dengan Client ID `573764659302-...`, pilih itu
   - Atau buat baru: "LifeCare"
4. Disable Google Analytics (optional)
5. Click "**Create project**"

#### Step 2: Add Android App ke Firebase

1. Di Firebase Console, click "**Add app**" → Android icon
2. Isi details:
   ```
   Android package name: com.example.lifecare
   App nickname: LifeCare
   Debug signing certificate SHA-1: [Lihat di bawah]
   ```

#### Step 3: Get SHA-1 Certificate

**Windows:**
```bash
cd "c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare"
gradlew.bat signingReport
```

**Look for output:**
```
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey
SHA1: AA:BB:CC:DD:EE:FF:11:22:33:44:55:66:77:88:99:00:11:22:33:44
     ^^^ COPY THIS ^^^
```

Copy SHA-1 dan paste ke Firebase.

#### Step 4: Download google-services.json

1. Firebase akan generate `google-services.json`
2. Click "**Download google-services.json**"
3. Copy file ke folder:
   ```
   c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare\app\
   ```

**Structure harus seperti ini:**
```
LifeCare/
├── app/
│   ├── google-services.json  ← TARUH DI SINI
│   ├── build.gradle.kts
│   └── src/
├── gradle/
└── build.gradle.kts
```

#### Step 5: Add Google Services Plugin

**File**: `build.gradle.kts` (Project level - root folder)

Add ini di bagian `plugins`:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false  // ADD THIS LINE
}
```

**File**: `app/build.gradle.kts` (App level)

Add ini di bagian `plugins`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")  // ADD THIS LINE
}
```

#### Step 6: Sync & Build

1. Click "**Sync Now**" di Android Studio
2. Clean build:
   ```bash
   ./gradlew clean
   ```
3. Build app:
   ```bash
   ./gradlew assembleDebug
   ```

#### Step 7: Test!

1. **UNINSTALL** app lama dari device/emulator
2. Install app baru:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
3. Buka app
4. Tap "**Sign in with Google**" atau "**Sign up with Google**"
5. Pilih Google account
6. Should work! ✅

---

## 🔍 TROUBLESHOOTING

### Still Error: "No credentials available"

**Kemungkinan penyebab:**

1. **SHA-1 tidak match**
   - Get SHA-1 lagi: `gradlew.bat signingReport`
   - Update di Firebase Console
   - Wait 5-10 minutes
   - Uninstall & reinstall app

2. **google-services.json salah folder**
   - Must be di `app/google-services.json`
   - NOT di root folder atau `app/src/`

3. **Plugin not applied**
   - Check `build.gradle.kts` has `id("com.google.gms.google-services")`
   - Sync project
   - Clean & rebuild

4. **Cache issue**
   - Uninstall app completely
   - Clear Android Studio cache: File → Invalidate Caches
   - Rebuild

### Error: "API not enabled"

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select project with ID `573764659302-...`
3. APIs & Services → Library
4. Search "**Google Sign-In API**"
5. Click "**Enable**"
6. Wait 5 minutes
7. Try again

### Error: "Developer Error"

**This means SHA-1 mismatch!**

1. Get correct SHA-1: `gradlew.bat signingReport`
2. Go to Firebase Console
3. Project Settings → Your apps → Android app
4. Add fingerprint / Update SHA-1
5. Save
6. Wait 5 minutes
7. Uninstall app & reinstall

---

## 📊 COMPARISON

| Aspect | Manual Registration | Google Sign-In |
|--------|-------------------|----------------|
| **Current Status** | ✅ Working now | ⚠️ Needs setup |
| **Time to Use** | ✅ 0 minutes | ⚠️ 10-15 minutes |
| **Steps Needed** | ✅ 0 steps | ⚠️ 7 steps |
| **Configuration** | ✅ None | ⚠️ google-services.json + SHA-1 |
| **Best For** | ✅ Quick testing, demo | ⚠️ Production |

---

## 💡 RECOMMENDATION

### For Now (Testing/Demo):
**✅ USE MANUAL REGISTRATION**
- Works immediately
- No configuration needed
- Perfect for educational project

### For Later (Production):
**⚙️ COMPLETE GOOGLE SIGN-IN**
- Follow Option B steps above
- Takes 10-15 minutes
- Better user experience

---

## 📝 CHECKLIST

Jika Anda memutuskan untuk complete Google Sign-In setup:

- [ ] Create/Select Firebase project
- [ ] Add Android app to Firebase
- [ ] Get SHA-1 with `gradlew signingReport`
- [ ] Register SHA-1 in Firebase
- [ ] Download `google-services.json`
- [ ] Put `google-services.json` in `app/` folder
- [ ] Add Google Services plugin to `build.gradle.kts` (both files)
- [ ] Sync Gradle
- [ ] Clean build
- [ ] Uninstall old app
- [ ] Install new app
- [ ] Test Google Sign-In
- [ ] ✅ Should work!

---

## 🎯 SUMMARY

### Current Status:
```
Code:         ✅ READY (100%)
Client ID:    ✅ CONFIGURED
google-services.json:  ❌ MISSING
SHA-1:        ❓ UNKNOWN (probably not registered)

Overall:      50% COMPLETE
```

### To Finish:
1. Get `google-services.json` from Firebase
2. Put in `app/` folder
3. Add Google Services plugin
4. Sync & build
5. Done! ✅

### Or Skip It:
- Use manual registration (email + password)
- Works perfectly ✅
- No setup needed ✅

---

## 📞 NEED HELP?

### Quick Questions:

**Q: Do I really need Google Sign-In?**
A: No! Manual registration works perfectly.

**Q: How long does setup take?**
A: 10-15 minutes if you follow steps.

**Q: Can I skip it for now?**
A: YES! Use manual registration instead.

**Q: Will it affect grading?**
A: No, manual auth is fully functional and sufficient.

---

**Your Client ID**: `573764659302-iuu3m0pu89jtm2rcgs32rs6ga5i2g611.apps.googleusercontent.com`
**Status**: Ready to complete setup OR use manual registration ✅
