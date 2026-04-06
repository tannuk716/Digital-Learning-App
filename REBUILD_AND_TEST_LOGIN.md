# How to Rebuild and Test Login - Step by Step

## ⚠️ IMPORTANT: You MUST Rebuild!

The development bypass is in the code, but your installed app is still using the OLD code. You need to rebuild!

---

## 🚀 Step-by-Step Instructions

### Step 1: Stop the App
1. Close the app on your emulator/device
2. In Android Studio, click the red square "Stop" button

### Step 2: Clean Build
```bash
./gradlew clean
```

Wait for it to finish (should say "BUILD SUCCESSFUL")

### Step 3: Build Fresh APK
```bash
./gradlew assembleDebug
```

Wait for it to finish (may take 1-2 minutes)

### Step 4: Install New APK
```bash
./gradlew installDebug
```

Wait for "Installation successful"

### Step 5: Open the App
- Open the app on your emulator/device
- You should see the login screen

### Step 6: Test Development Login

**For Student:**
- Email: `test@test.com`
- Password: `test123`
- Click Login
- Should see: "Development Mode: Bypassing login"
- Should go to Student Dashboard

**For Teacher:**
- Email: `teacher@test.com`
- Password: `test123`
- Click Login
- Should see: "Development Mode: Teacher login"
- Should go to Teacher Dashboard

---

## 🔧 Alternative: Use Android Studio

If command line doesn't work:

### Step 1: Clean Project
1. In Android Studio menu: **Build** → **Clean Project**
2. Wait for it to finish

### Step 2: Rebuild Project
1. In Android Studio menu: **Build** → **Rebuild Project**
2. Wait for it to finish (may take 1-2 minutes)
3. Check bottom panel for "BUILD SUCCESSFUL"

### Step 3: Run App
1. Click the green "Run" button (▶️)
2. Select your emulator/device
3. Wait for app to install and launch

### Step 4: Test Login
- Email: `test@test.com`
- Password: `test123`
- Click Login
- Should work!

---

## ✅ What Should Happen

### When You Enter test@test.com / test123:

1. You type the credentials
2. Click Login button
3. See toast message: "Development Mode: Bypassing login"
4. App goes directly to Student Dashboard
5. NO network error!
6. NO Firebase call!

### When You Enter teacher@test.com / test123:

1. You type the credentials
2. Click Login button
3. See toast message: "Development Mode: Teacher login"
4. App goes directly to Teacher Dashboard
5. NO network error!

---

## 🐛 Troubleshooting

### Problem: Still Getting Network Error

**Cause:** You didn't rebuild the app, or the old APK is still installed

**Solution:**
```bash
# Uninstall old app first
adb uninstall com.tannu.edureach

# Then install fresh
./gradlew clean assembleDebug installDebug
```

### Problem: "Development Mode" message doesn't appear

**Cause:** Old APK is still running

**Solution:**
1. Force stop the app
2. Uninstall the app
3. Rebuild and install fresh

### Problem: Build fails

**Cause:** Gradle cache issue

**Solution:**
```bash
# Clean Gradle cache
./gradlew clean --no-daemon

# Rebuild
./gradlew assembleDebug
```

### Problem: Can't find gradlew

**Cause:** Not in project directory

**Solution:**
```bash
# Navigate to project directory
cd /path/to/your/project

# Then run
./gradlew clean assembleDebug installDebug
```

---

## 📝 Verification Checklist

Before testing, verify:

- [ ] Ran `./gradlew clean`
- [ ] Saw "BUILD SUCCESSFUL"
- [ ] Ran `./gradlew assembleDebug`
- [ ] Saw "BUILD SUCCESSFUL"
- [ ] Ran `./gradlew installDebug`
- [ ] Saw "Installation successful"
- [ ] Opened the app
- [ ] Entered `test@test.com` / `test123`
- [ ] Clicked Login
- [ ] Saw "Development Mode" message
- [ ] Went to dashboard

---

## 🎯 Quick Commands

**All in one:**
```bash
./gradlew clean assembleDebug installDebug && adb shell am start -n com.tannu.edureach/.SplashActivity
```

This will:
1. Clean the project
2. Build fresh APK
3. Install on device
4. Launch the app

---

## 💡 Why This Works

The development bypass checks the email BEFORE calling Firebase:

```kotlin
if (email == "test@test.com" && password == "test123") {
    // Skip Firebase completely
    // Go directly to dashboard
    return  // Exit before Firebase call
}

// This code only runs if NOT using test credentials
auth.signInWithEmailAndPassword(email, password)
```

So when you use `test@test.com`, Firebase is never called, and no network is needed!

---

## ✅ Expected Result

After rebuilding and testing with `test@test.com` / `test123`:

1. ✅ No network error
2. ✅ See "Development Mode: Bypassing login" message
3. ✅ Go directly to Student Dashboard
4. ✅ Can test all features
5. ✅ No Firebase needed

---

## 🚨 If Still Not Working

If you've rebuilt and still get network error:

1. **Check you're using the right credentials:**
   - Email: `test@test.com` (NOT test@gmail.com or anything else)
   - Password: `test123` (exactly, case-sensitive)

2. **Check the toast message:**
   - Should see "Development Mode: Bypassing login"
   - If you don't see this, the old APK is still running

3. **Force uninstall and reinstall:**
   ```bash
   adb uninstall com.tannu.edureach
   ./gradlew installDebug
   ```

4. **Check Logcat:**
   ```bash
   adb logcat | grep "Development Mode"
   ```
   Should see the log message when you login

---

## 📞 Summary

**The Fix:** Development bypass is in the code

**The Problem:** You need to rebuild for it to work

**The Solution:**
```bash
./gradlew clean assembleDebug installDebug
```

**Then Test:**
- Email: `test@test.com`
- Password: `test123`
- Should work!

**Rebuild the app now and try again!** 🚀
