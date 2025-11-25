# Google Sign-In Troubleshooting Guide

**Status**: ⚠️ Google Sign-In belum bisa digunakan
**Reason**: SHA-1 fingerprint belum diregister di Firebase Console
**Solution**: Ikuti panduan di bawah ini (10-15 menit)

---

## 🔍 DIAGNOSIS

### Current Status:
```
✅ Code: Implemented correctly
✅ google-services.json: Installed in app/ folder
✅ Firebase Project: lifecaree28-8cc63 (configured)
✅ Gradle Plugin: Configured (v4.4.4)
✅ Build: Successful
❌ SHA-1 Fingerprint: NOT REGISTERED in Firebase ← THIS IS THE PROBLEM!
```

### Error You're Seeing:
When you tap "Sign in with Google" or "Sign up with Google", you likely see:
- "No credentials available"
- OR nothing happens
- OR Google Sign-In dialog doesn't appear

### Why It's Happening:
Firebase needs to know your app's **SHA-1 fingerprint** to authorize Google Sign-In. Without it, Firebase rejects the request.

---

## ✅ SOLUTION: Register SHA-1 in Firebase Console

### Your SHA-1 Fingerprint:
```
B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA
```

---

## 📝 STEP-BY-STEP FIX

### Step 1: Open Firebase Console

1. Go to: https://console.firebase.google.com/
2. **Login** with your Google account (the one you used to create Firebase project)
3. You should see your projects listed

### Step 2: Select Your Project

1. Click on project: **lifecaree28-8cc63**
2. You should see the Firebase dashboard

### Step 3: Go to Project Settings

1. Click the **⚙️ gear icon** (top left, next to "Project Overview")
2. Select **"Project settings"**
3. Scroll down to **"Your apps"** section
4. You should see an Android app with package: `com.example.lifecare`

### Step 4: Add SHA-1 Fingerprint

1. In the **"Your apps"** section, click on your Android app
2. Scroll down to **"SHA certificate fingerprints"** section
3. Click **"Add fingerprint"** button
4. **Paste this SHA-1**:
   ```
   B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA
   ```
5. Click **"Save"**
6. You should see the fingerprint added to the list

### Step 5: Download Updated google-services.json (Optional but Recommended)

1. After adding SHA-1, Firebase may generate an updated `google-services.json`
2. Scroll to bottom of the page
3. Click **"Download google-services.json"**
4. **Replace** the existing file in your project:
   ```
   c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare\app\google-services.json
   ```

### Step 6: Wait for Propagation

**IMPORTANT**: Changes take 5-10 minutes to propagate!

☕ **Take a 10-minute break** while Firebase updates its servers.

### Step 7: Uninstall Old App

**CRITICAL**: You MUST uninstall the old app!

```bash
adb -s RRCT5027WGY uninstall com.example.lifecare
```

Or manually:
1. Go to Settings → Apps
2. Find "LifeCare"
3. Tap "Uninstall"

### Step 8: Rebuild & Install App

```bash
cd "c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare"
./gradlew.bat clean assembleDebug
adb -s RRCT5027WGY install app/build/outputs/apk/debug/app-debug.apk
```

### Step 9: Test Google Sign-In

1. Open LifeCare app
2. Tap **"Sign in with Google"** or **"Sign up with Google"**
3. **Google account picker should appear!** ✅
4. Select your Google account
5. Should successfully login/register ✅

---

## 🔧 ALTERNATIVE: Manual Commands

If you want to do everything via command line:

```bash
# 1. Get SHA-1 (already have it)
cd "c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare"
./gradlew.bat signingReport
# Copy: B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA

# 2. Add to Firebase (manual step - use web console)
# Go to: https://console.firebase.google.com/project/lifecaree28-8cc63/settings/general
# Add SHA-1 fingerprint

# 3. Wait 10 minutes

# 4. Uninstall old app
adb -s RRCT5027WGY uninstall com.example.lifecare

# 5. Rebuild
./gradlew.bat clean assembleDebug

# 6. Install new
adb -s RRCT5027WGY install app/build/outputs/apk/debug/app-debug.apk

# 7. Test!
```

---

## 🎯 VERIFICATION CHECKLIST

After completing all steps, verify:

- [ ] SHA-1 visible in Firebase Console → Project Settings → Your apps → SHA certificate fingerprints
- [ ] Waited at least 10 minutes after adding SHA-1
- [ ] Old app uninstalled from device
- [ ] New app installed (built AFTER adding SHA-1)
- [ ] Tap "Sign in with Google" → Google account picker appears ✅
- [ ] Select account → Successfully creates/logins account ✅

---

## ❌ COMMON MISTAKES

### Mistake 1: Not Waiting
**Problem**: Testing immediately after adding SHA-1
**Solution**: Wait 10 minutes for Firebase to propagate changes

### Mistake 2: Not Uninstalling Old App
**Problem**: Old app still using old configuration
**Solution**: MUST uninstall first, then install new build

### Mistake 3: Wrong SHA-1
**Problem**: Using release SHA-1 instead of debug SHA-1
**Solution**: Make sure you're using the SHA-1 from `debug.keystore`:
```
B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA
```

### Mistake 4: Wrong Firebase Project
**Problem**: Adding SHA-1 to wrong project
**Solution**: Make sure you're in project `lifecaree28-8cc63`

### Mistake 5: Not Rebuilding App
**Problem**: Using old APK
**Solution**: Always rebuild after Firebase changes: `./gradlew.bat clean assembleDebug`

---

## 🔍 STILL NOT WORKING?

### Check 1: Verify SHA-1 in Firebase

1. Go to Firebase Console
2. Project: lifecaree28-8cc63
3. Settings → Your apps → Android app
4. SHA certificate fingerprints section
5. Should see: `B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA`

### Check 2: Verify Package Name

Firebase project package name MUST match app package name:
- **Expected**: `com.example.lifecare`
- **Check in**: `app/build.gradle.kts` → `applicationId`

### Check 3: Check Logcat for Errors

Run app and trigger Google Sign-In, then check logs:
```bash
adb -s RRCT5027WGY logcat -d | grep -i "lifecare\|google\|sign\|credential"
```

Look for errors like:
- "Developer console is not set up correctly"
- "API not enabled"
- "SHA-1 mismatch"

### Check 4: Verify Google Play Services

Make sure device has Google Play Services:
```bash
adb -s RRCT5027WGY shell pm list packages | grep google
```

Should see: `com.google.android.gms`

### Check 5: Enable Google Sign-In API

1. Go to: https://console.cloud.google.com/
2. Select project linked to Firebase
3. APIs & Services → Library
4. Search: "Google Sign-In API"
5. Click "Enable" (if not already enabled)

---

## 📊 TROUBLESHOOTING FLOWCHART

```
Start: Google Sign-In not working
  ↓
Q: Did you add SHA-1 to Firebase?
  NO → Go to Step 4 above ✅
  YES ↓

Q: Did you wait 10+ minutes?
  NO → Wait, then continue ⏰
  YES ↓

Q: Did you uninstall old app?
  NO → Uninstall it now! 🗑️
  YES ↓

Q: Did you rebuild after SHA-1 change?
  NO → Rebuild: ./gradlew.bat clean assembleDebug 🔨
  YES ↓

Q: Is package name correct (com.example.lifecare)?
  NO → Fix package name in Firebase
  YES ↓

Q: Is SHA-1 exactly this one?
  B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA
  NO → Use correct SHA-1 ☝️
  YES ↓

Still not working? Check logcat for specific error 📱
```

---

## 🎓 FOR EDUCATIONAL PROJECT

### Do You REALLY Need Google Sign-In?

**Short Answer: NO!**

Manual registration (email + password) is **fully functional** and sufficient for your educational project.

### Comparison:

| Feature | Manual Registration | Google Sign-In |
|---------|-------------------|----------------|
| **Works Now** | ✅ Yes | ❌ Needs setup |
| **Setup Time** | ✅ 0 minutes | ⚠️ 10-15 minutes |
| **Good for Demo** | ✅ Yes | ✅ Yes (if configured) |
| **Good for Grading** | ✅ Absolutely | ✅ Yes (but not required) |
| **Authentication** | ✅ Full (email+password+PIN) | ✅ Full (Google+PIN) |
| **Security** | ✅ AES256 encrypted | ✅ AES256 encrypted |

### Recommendation:

**For educational project**: ✅ **Use manual registration** - it's simpler and works perfectly!

**For production app**: ⚙️ **Add Google Sign-In** - better UX, one-tap login

---

## 💡 SUMMARY

### The Problem:
Google Sign-In fails because SHA-1 fingerprint is not registered in Firebase Console.

### The Solution:
1. Add SHA-1 to Firebase (5 minutes)
2. Wait 10 minutes
3. Uninstall old app
4. Rebuild & install new app
5. Test - should work! ✅

### Alternative:
Use manual registration (email + password) - **works perfectly without any setup**!

---

## 📞 QUICK REFERENCE

**Your Project Info:**
- Firebase Project: `lifecaree28-8cc63`
- Package Name: `com.example.lifecare`
- SHA-1 Debug: `B9:3E:E3:DB:01:A6:B4:CA:E8:4C:73:24:0A:2E:9A:D8:19:85:25:FA`
- Device ID: `RRCT5027WGY`

**Firebase Console:**
- URL: https://console.firebase.google.com/project/lifecaree28-8cc63
- Settings: https://console.firebase.google.com/project/lifecaree28-8cc63/settings/general

**Commands:**
```bash
# Build
./gradlew.bat clean assembleDebug

# Uninstall
adb -s RRCT5027WGY uninstall com.example.lifecare

# Install
adb -s RRCT5027WGY install app/build/outputs/apk/debug/app-debug.apk

# Check logs
adb -s RRCT5027WGY logcat | grep -i lifecare
```

---

**Status After Fix**: ✅ Google Sign-In should work!
**Estimated Time**: 10-15 minutes total
**Difficulty**: Easy (just follow steps)

**Good luck!** 🚀
