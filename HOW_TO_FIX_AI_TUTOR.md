# How to Fix AI Tutor - Simple Steps

## The Problem
AI Tutor shows "No internet connection" error.

## The Solution
Your API key is already in the code, but the app needs to be rebuilt to use it.

## Quick Fix (3 Steps)

### Step 1: Run the Rebuild Script

**On Windows:**
```bash
rebuild_and_install.bat
```

**On Mac/Linux:**
```bash
chmod +x rebuild_and_install.sh
./rebuild_and_install.sh
```

This will:
- Clean old build
- Rebuild with your API key
- Install on your device

### Step 2: Test AI Tutor

1. Open the app
2. Go to Student Dashboard
3. Click "AI Tutor"
4. Type: "What is 2+2?"
5. Wait 5-10 seconds
6. You should see the AI response!

### Step 3: If Still Not Working

Check the logs:
```bash
adb logcat -s GeminiApiHelper:D AIChatbot:D
```

Look for:
- "API Key length: 39" ✓ (means key is loaded)
- "Trying endpoint..." ✓ (means trying to connect)
- "Success with endpoint..." ✓ (means it worked!)

## What Was Fixed

1. ✅ Your API key: `AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM`
2. ✅ Network security configuration added
3. ✅ Better error handling
4. ✅ Automatic endpoint retry
5. ✅ Improved logging

## Manual Build (if script doesn't work)

```bash
# Clean
./gradlew clean

# Build
./gradlew assembleDebug

# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Test Your API Key (Optional)

Want to verify your API key works before rebuilding?

**Windows:**
```bash
test_gemini_api.bat
```

**Mac/Linux:**
```bash
chmod +x test_gemini_api.sh
./test_gemini_api.sh
```

## Troubleshooting

### "No internet connection" still appears

**Cause**: App wasn't rebuilt or old version still installed

**Fix**:
```bash
# Uninstall old app
adb uninstall com.tannu.edureach

# Rebuild and install
rebuild_and_install.bat
```

### "API key issue" appears

**Cause**: API key not loaded or invalid

**Fix**:
1. Check `app/build.gradle.kts` has your key
2. Rebuild: `./gradlew clean assembleDebug`
3. Test key with `test_gemini_api.bat`

### "Request timed out" appears

**Cause**: Slow internet or server busy

**Fix**:
- Check internet speed
- Try again in a few seconds
- The app will retry automatically

### App crashes when opening AI Tutor

**Cause**: Build error or missing dependency

**Fix**:
```bash
./gradlew clean
./gradlew assembleDebug --stacktrace
```

Check the error message and share it.

## Success Checklist

- [ ] Ran rebuild script
- [ ] App installed successfully
- [ ] Opened AI Tutor
- [ ] Sent a message
- [ ] Got AI response

## Still Need Help?

1. Run this command and share the output:
   ```bash
   adb logcat -s GeminiApiHelper:V AIChatbot:V > ai_tutor_log.txt
   ```

2. Open AI Tutor and send a message

3. Stop the log (Ctrl+C)

4. Share the `ai_tutor_log.txt` file

## Files You Need

All files are ready in your project:
- ✅ `rebuild_and_install.bat` - Rebuild script (Windows)
- ✅ `rebuild_and_install.sh` - Rebuild script (Mac/Linux)
- ✅ `test_gemini_api.bat` - Test API key (Windows)
- ✅ `test_gemini_api.sh` - Test API key (Mac/Linux)
- ✅ `FINAL_AI_TUTOR_FIX.md` - Detailed documentation

## That's It!

Just run `rebuild_and_install.bat` and your AI Tutor should work! 🎉
