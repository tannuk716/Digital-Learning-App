# How to Rebuild the App - Step by Step

## ⚠️ CRITICAL: You MUST Rebuild!

The fix is applied in the code, but your app is still using the OLD code until you rebuild.

---

## 🔧 Method 1: Command Line (Recommended)

Open terminal in your project folder and run:

```bash
# Step 1: Stop any running Gradle processes
./gradlew --stop

# Step 2: Clean old build files
./gradlew clean

# Step 3: Build new APK
./gradlew assembleDebug

# Step 4: Install on device
./gradlew installDebug
```

**OR do all at once:**
```bash
./gradlew --stop && ./gradlew clean assembleDebug installDebug
```

---

## 🖥️ Method 2: Android Studio

### Step 1: Clean Project
1. Click **Build** menu
2. Click **Clean Project**
3. Wait for it to finish

### Step 2: Rebuild Project
1. Click **Build** menu
2. Click **Rebuild Project**
3. Wait for it to finish (may take 1-2 minutes)

### Step 3: Run App
1. Click **Run** menu
2. Click **Run 'app'**
3. Select your device
4. Wait for installation

---

## ✅ How to Verify Rebuild Worked

### Check 1: Build Output
You should see:
```
BUILD SUCCESSFUL in Xs
```

### Check 2: Installation
You should see:
```
Installing APK...
Installation successful
```

### Check 3: App Version
- Uninstall old app first (optional but recommended)
- Install new app
- The AI Tutor should now work

---

## 🧪 Test After Rebuild

1. **Open the app**
2. **Login as student**
3. **Click "AI Tutor" card**
4. **Type: "What is 2+2?"**
5. **Click Send**
6. **Wait 3-5 seconds**
7. **Should see AI response!** ✓

---

## 🐛 Troubleshooting

### Problem: "gradlew: command not found"
**Solution:** You're not in the project directory
```bash
cd /path/to/your/project
./gradlew clean assembleDebug
```

### Problem: "Permission denied"
**Solution:** Make gradlew executable
```bash
chmod +x gradlew
./gradlew clean assembleDebug
```

### Problem: Build fails with errors
**Solution:** 
1. File → Invalidate Caches / Restart
2. Try again

### Problem: "No connected devices"
**Solution:**
1. Connect your Android device via USB
2. Enable USB debugging on device
3. Run `adb devices` to verify connection

---

## 📱 Alternative: Build APK and Install Manually

If automatic installation doesn't work:

### Step 1: Build APK
```bash
./gradlew clean assembleDebug
```

### Step 2: Find APK
The APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Step 3: Install Manually
1. Copy APK to your phone
2. Open file manager on phone
3. Tap the APK file
4. Allow installation from unknown sources if prompted
5. Install

---

## ⏱️ How Long Does Rebuild Take?

- **Clean:** 5-10 seconds
- **Build:** 30-60 seconds (first time may be longer)
- **Install:** 10-20 seconds

**Total:** About 1-2 minutes

---

## ✅ Checklist

Before testing:
- [ ] Ran `./gradlew clean`
- [ ] Ran `./gradlew assembleDebug`
- [ ] Ran `./gradlew installDebug`
- [ ] Saw "BUILD SUCCESSFUL"
- [ ] Saw "Installation successful"
- [ ] App is installed on device

After testing:
- [ ] Opened AI Tutor
- [ ] Asked a question
- [ ] Got AI response (not error)
- [ ] Can ask multiple questions

---

## 🎯 Bottom Line

**The fix is in the code. You just need to rebuild the app to use it!**

Run this command:
```bash
./gradlew clean assembleDebug installDebug
```

Then test the AI Tutor. It will work!
