# Firebase Setup Guide - google-services.json

**Status**: 🔒 **google-services.json is NOT included in repository** (for security)
**Action Required**: Download your own `google-services.json` from Firebase Console

---

## ⚠️ PENTING: Why google-services.json is Ignored

### Security Reasons:

```
❌ google-services.json contains:
   - Firebase API keys
   - Project configuration
   - OAuth client IDs
   - Database URLs
   - Storage bucket info

✅ Best Practice:
   - NEVER commit to public repository
   - Each developer downloads their own
   - Added to .gitignore
```

---

## 📝 HOW TO GET google-services.json

### Step 1: Go to Firebase Console

1. Open: https://console.firebase.google.com/
2. Login with Google account
3. You'll see list of projects

### Step 2: Select or Create Project

**Option A: Use Existing Project (Recommended for Team)**
```
- Project Name: lifecaree28-8cc63
- If you're team member, ask project owner to add you
```

**Option B: Create New Project (For Your Own Testing)**
```
1. Click "Add project"
2. Project name: "LifeCare-YourName"
3. Disable Google Analytics (optional)
4. Click "Create project"
```

### Step 3: Add Android App to Firebase

1. In Firebase Console, click **⚙️ gear icon** → **Project settings**
2. Scroll to **"Your apps"** section
3. Click **Android icon** to add Android app
4. Fill in:
   ```
   Package name: com.example.lifecare
   App nickname: LifeCare (optional)
   SHA-1: [See below for how to get]
   ```
5. Click **"Register app"**

### Step 4: Get SHA-1 Fingerprint (Required for Google Sign-In)

**Windows:**
```bash
cd "c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare"
gradlew.bat signingReport
```

**Linux/Mac:**
```bash
./gradlew signingReport
```

**Output will show:**
```
Variant: debug
Config: debug
Store: C:\Users\YourName\.android\debug.keystore
Alias: AndroidDebugKey
SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
     ^^^ COPY THIS ^^^
```

Copy SHA-1 and paste into Firebase.

### Step 5: Download google-services.json

1. After registering app, Firebase will show download button
2. Click **"Download google-services.json"**
3. Save file

### Step 6: Place File in Correct Location

**CRITICAL: File location matters!**

```
✅ CORRECT:
c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare\app\google-services.json

❌ WRONG:
c:\Ubay\Kuliah\Sem 5\PPB\Teori\LifeCare\LifeCare\google-services.json (root folder)
```

**Structure should be:**
```
LifeCare/
├── app/
│   ├── google-services.json  ← HERE!
│   ├── build.gradle.kts
│   └── src/
├── gradle/
└── build.gradle.kts
```

### Step 7: Verify Installation

Run build to verify:
```bash
./gradlew.bat assembleDebug
```

Should see:
```
> Task :app:processDebugGoogleServices
✅ No errors
```

---

## 🔍 TROUBLESHOOTING

### Error: "File google-services.json is missing"

**Cause**: File not in correct location
**Solution**:
```bash
# Check file exists
ls app/google-services.json

# Should output: app/google-services.json
```

### Error: "Default Firebase app is not initialized"

**Cause**: google-services.json not processed
**Solution**:
1. Clean build: `./gradlew.bat clean`
2. Rebuild: `./gradlew.bat assembleDebug`

### Error: "SHA-1 mismatch"

**Cause**: SHA-1 in Firebase doesn't match your debug keystore
**Solution**:
1. Get your SHA-1: `./gradlew.bat signingReport`
2. Add to Firebase Console:
   - Project Settings → Your apps → SHA certificate fingerprints
   - Click "Add fingerprint"
   - Paste your SHA-1
   - Save

---

## ✅ VERIFICATION CHECKLIST

After setup, verify:

- [ ] google-services.json exists in `app/` folder
- [ ] File size ~500-1000 bytes (contains JSON)
- [ ] Build successful: `./gradlew.bat assembleDebug`
- [ ] Package name matches: `com.example.lifecare`
- [ ] SHA-1 registered in Firebase Console
- [ ] Google Sign-In works (if configured)

---

## 📊 FILE CONTENT EXAMPLE

Your `google-services.json` should look like this:

```json
{
  "project_info": {
    "project_number": "123456789012",
    "project_id": "your-project-id",
    "storage_bucket": "your-project-id.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:123456789012:android:xxxxx",
        "android_client_info": {
          "package_name": "com.example.lifecare"
        }
      },
      "oauth_client": [],
      "api_key": [
        {
          "current_key": "AIzaSyXXXXXXXXXXXXXXXXXXXXXX"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": []
        }
      }
    }
  ],
  "configuration_version": "1"
}
```

**⚠️ IMPORTANT**: Your actual keys will be different!

---

## 🔐 SECURITY BEST PRACTICES

### DO:
✅ Keep google-services.json in .gitignore
✅ Download fresh copy from Firebase for each project
✅ Share Firebase Console access instead of sharing file
✅ Use environment-specific configs (dev/prod)

### DON'T:
❌ Commit google-services.json to public repository
❌ Share google-services.json via email/chat
❌ Hardcode API keys in source code
❌ Use production config for development

---

## 🤝 FOR TEAM MEMBERS

If you're working in a team:

### Team Lead:
1. Create Firebase project
2. Add team members to Firebase Console:
   - Project Settings → Users and permissions
   - Add email → Role: Editor
3. Share Firebase project ID

### Team Members:
1. Accept Firebase invite
2. Go to Firebase Console
3. Download your own google-services.json
4. Place in app/ folder
5. Get your SHA-1 and add to Firebase
6. Build and test

---

## 🎓 FOR EDUCATIONAL PROJECT

### Scenario 1: Solo Project
```
✅ Create your own Firebase project
✅ Download google-services.json
✅ Add to app/ folder
✅ Test Google Sign-In
```

### Scenario 2: Group Project
```
✅ One person creates Firebase project
✅ Share Firebase Console access
✅ Each member downloads own google-services.json
✅ Each member registers own SHA-1
```

### Scenario 3: Demo/Submission
```
⚠️ Don't include google-services.json in submission
✅ Include this SETUP_FIREBASE.md
✅ Document in README.md how to setup
✅ Show screenshots of Firebase setup
```

---

## 📝 ALTERNATIVE: Manual Registration

If you don't want to setup Firebase/Google Sign-In:

```
✅ Use Manual Registration (email + password)
✅ Already fully functional
✅ No Firebase configuration needed
✅ Perfect for educational project
```

Manual registration works without google-services.json!

---

## 🔗 USEFUL LINKS

- Firebase Console: https://console.firebase.google.com/
- Firebase Documentation: https://firebase.google.com/docs/android/setup
- Google Sign-In Setup: See GOOGLE_SIGNIN_SETUP.md
- Troubleshooting: See GOOGLE_SIGNIN_TROUBLESHOOT.md

---

## ✅ SUMMARY

**Current Status:**
- google-services.json: ❌ Not in repository (by design)
- .gitignore: ✅ Configured to ignore
- Setup required: ⚠️ Yes (5-10 minutes)
- Alternative: ✅ Use manual registration (no setup)

**To Setup:**
1. Download google-services.json from Firebase
2. Place in app/ folder
3. Add SHA-1 to Firebase Console
4. Build and test

**Skip Setup:**
- Use manual registration (email + password)
- Works without any Firebase configuration

---

**Last Updated**: 26 November 2025
**Security**: ✅ google-services.json properly ignored
**Status**: Ready for development
