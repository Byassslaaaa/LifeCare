# Google Sign-In Setup Guide

**Status**: ⚠️ **NOT CONFIGURED** (Optional Feature)
**Last Updated**: 25 November 2025

---

## ⚠️ PENTING: Google Sign-In Error "No credentials available"

### Apa Itu Error Ini?

Jika Anda melihat error:
```
Google Sign-In gagal: No credentials available
```

Ini adalah **error yang normal dan expected** karena Google Sign-In **belum dikonfigurasi**.

### Kenapa Terjadi?

Google Sign-In memerlukan:
1. ✅ **Code implementation** - SUDAH SELESAI
2. ❌ **Google Cloud Project setup** - BELUM DIKONFIGURASI
3. ❌ **google-services.json file** - BELUM ADA
4. ❌ **SHA-1 certificate fingerprint** - BELUM DIREGISTER

---

## 🎯 SOLUSI: 2 PILIHAN

### Option 1: ✅ **GUNAKAN MANUAL REGISTRATION** (RECOMMENDED)

**Ini adalah cara termudah dan sudah fully functional!**

#### Cara Register Manual:
1. Buka app LifeCare
2. Di Login screen, tap "**Sign up**"
3. Isi form registration:
   - Nama Lengkap
   - Email (contoh: `user@email.com`)
   - Password (minimal 6 karakter)
   - Konfirmasi Password
   - Umur (minimal 13 tahun)
   - Jenis Kelamin
4. Tap "**Buat Akun**"
5. Buat PIN 6 digit untuk keamanan
6. Selesai! ✅

#### Cara Login Manual:
1. Buka app LifeCare
2. Masukkan email dan password
3. Tap "**Login**"
4. Masukkan PIN 6 digit
5. Selesai! ✅

**Benefits:**
- ✅ Tidak perlu konfigurasi tambahan
- ✅ Langsung bisa digunakan
- ✅ Sama amannya (PIN + Encrypted storage)
- ✅ Cocok untuk educational project

---

### Option 2: ⚙️ **CONFIGURE GOOGLE SIGN-IN** (OPTIONAL)

**Hanya jika Anda benar-benar ingin menggunakan Google Sign-In.**

#### Prerequisites:
- Google Account
- Google Cloud Console access
- Android Studio
- Physical Android device OR Emulator dengan Google Play Services

#### Step 1: Create Google Cloud Project

1. Buka [Google Cloud Console](https://console.cloud.google.com/)
2. Create New Project:
   - Project Name: `LifeCare`
   - Click "Create"
3. Enable APIs:
   - Go to "APIs & Services" → "Enable APIs and Services"
   - Search for "**Google Sign-In API**"
   - Click "Enable"

#### Step 2: Configure OAuth Consent Screen

1. Go to "APIs & Services" → "OAuth consent screen"
2. Choose "External" (for testing)
3. Fill in:
   - App name: `LifeCare`
   - User support email: Your email
   - Developer contact: Your email
4. Click "Save and Continue"
5. Skip "Scopes" → Click "Save and Continue"
6. Add Test Users (your Google account)
7. Click "Save and Continue"

#### Step 3: Create OAuth 2.0 Client ID

1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth client ID"
3. Application type: **Android**
4. Name: `LifeCare Android`
5. Package name: `com.example.lifecare`
6. SHA-1 certificate fingerprint:

**Get SHA-1 for Debug:**
```bash
# Windows
cd "c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare"
gradlew.bat signingReport

# Linux/Mac
./gradlew signingReport
```

Look for:
```
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey
SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
```

Copy SHA-1 dan paste ke Google Cloud Console.

7. Click "Create"

#### Step 4: Create Web Client ID

**PENTING:** Google Sign-In di Android juga butuh Web Client ID!

1. Go to "Credentials" → "Create Credentials" → "OAuth client ID"
2. Application type: **Web application**
3. Name: `LifeCare Web`
4. Click "Create"
5. **COPY** the Client ID yang muncul (format: `xxxxx.apps.googleusercontent.com`)

#### Step 5: Update Code dengan Web Client ID

Edit file: `app/src/main/java/com/example/lifecare/auth/GoogleSignInHelper.kt`

Find line with `WEB_CLIENT_ID` dan replace dengan Web Client ID Anda:

```kotlin
private const val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID_HERE.apps.googleusercontent.com"
```

#### Step 6: Download google-services.json

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" → Select existing Google Cloud project `LifeCare`
3. Add Android app:
   - Package name: `com.example.lifecare`
   - App nickname: `LifeCare`
   - SHA-1: (same as before)
4. Download `google-services.json`
5. Copy file ke: `app/google-services.json`

#### Step 7: Update build.gradle

**build.gradle.kts (Project level):**
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
}
```

**build.gradle.kts (App level):**
```kotlin
plugins {
    id("com.google.gms.google-services")
}
```

#### Step 8: Test

1. **Uninstall** app lama jika ada
2. **Clean** build:
   ```bash
   ./gradlew clean
   ```
3. **Build** dan install:
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
4. Buka app
5. Tap "Sign in with Google" atau "Sign up with Google"
6. Pilih Google account
7. Should work! ✅

---

## 🔍 TROUBLESHOOTING

### Error: "No credentials available"
**Cause**: Google Sign-In not configured
**Solution**: Use manual registration OR follow setup steps above

### Error: "Developer Error"
**Cause**: SHA-1 fingerprint mismatch
**Solution**:
1. Run `gradlew signingReport`
2. Get correct SHA-1
3. Update in Google Cloud Console
4. Wait 5 minutes for propagation
5. Try again

### Error: "Sign in failed"
**Cause**: Wrong Web Client ID
**Solution**:
1. Check Web Client ID in GoogleSignInHelper.kt
2. Must match Web Client ID from Google Cloud Console
3. Update and rebuild

### Error: "API not enabled"
**Cause**: Google Sign-In API not enabled
**Solution**:
1. Go to Google Cloud Console
2. Enable "Google Sign-In API"
3. Wait 5 minutes
4. Try again

### Works on emulator but not real device
**Cause**: Different SHA-1 for release vs debug
**Solution**:
1. Get SHA-1 for release keystore
2. Add to Google Cloud Console
3. Rebuild with release key

---

## 📊 COMPARISON: Manual vs Google Sign-In

| Feature | Manual Registration | Google Sign-In |
|---------|-------------------|----------------|
| **Setup Time** | ✅ 0 minutes | ⚠️ 30-60 minutes |
| **Configuration** | ✅ None needed | ⚠️ Multiple steps |
| **Works Immediately** | ✅ Yes | ❌ No (needs config) |
| **Security** | ✅ PIN + Encryption | ✅ PIN + Encryption |
| **User Data** | ✅ Full control | ✅ Full control |
| **Dependencies** | ✅ None | ⚠️ Google Play Services |
| **Best For** | ✅ Educational project | ⚠️ Production app |

---

## 💡 RECOMMENDATION

### Untuk Educational Project (Tugas Kuliah):
**✅ GUNAKAN MANUAL REGISTRATION**

Reasons:
- Tidak perlu setup kompleks
- Fokus ke functionality, bukan configuration
- Sama amannya dengan Google Sign-In
- Lebih mudah untuk demo dan testing
- Tidak perlu Google Cloud account

### Untuk Production App:
**⚙️ CONFIGURE GOOGLE SIGN-IN**

Reasons:
- Better user experience (one-tap login)
- Users familiar dengan "Sign in with Google"
- Reduced password fatigue
- Google handles security

---

## 🎯 KESIMPULAN

### Current Status:
- ✅ **Code**: Fully implemented dan tested
- ❌ **Configuration**: Not configured (optional)
- ✅ **Manual Auth**: Fully functional dan recommended

### What Works Now:
- ✅ Manual registration dengan email/password
- ✅ PIN creation dan verification
- ✅ Encrypted data storage
- ✅ All health tracking features
- ✅ Profile management
- ✅ Logout dan clear data

### What Needs Configuration:
- ⚠️ Google Sign-In (optional feature)

---

## 📞 NEED HELP?

### Quick Start (No Configuration):
1. Ignore Google Sign-In buttons
2. Use "Sign up" → Fill form → Create account
3. Create PIN
4. Start using app! ✅

### Want Google Sign-In:
1. Follow "Option 2" steps above
2. Or ask for help dengan error yang spesifik

---

**Version**: 1.1.0
**Last Updated**: 25 November 2025
**Status**:
- Manual Auth: ✅ READY
- Google Sign-In: ⚠️ NEEDS CONFIGURATION (OPTIONAL)

---

**REMINDER**: Untuk educational project, **manual registration sudah lebih dari cukup**! Google Sign-In adalah optional feature yang memerlukan konfigurasi tambahan.
