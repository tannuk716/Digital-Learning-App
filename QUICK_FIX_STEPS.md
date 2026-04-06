# Quick Fix Steps - AI Tutor 404 Error

## 🔴 Problem
"AI model not available. Please try again later."

## ✅ Solution
Changed API endpoint from `gemini-1.5-flash` to `gemini-pro`

---

## 📋 3 Simple Steps to Fix

### Step 1: Rebuild the App ⚙️
```bash
./gradlew clean assembleDebug installDebug
```

**OR in Android Studio:**
- Build → Clean Project
- Build → Rebuild Project  
- Run → Run 'app'

### Step 2: Test AI Tutor 🤖
1. Open app
2. Login as student
3. Click "AI Tutor"
4. Ask: "What is 2+2?"
5. Wait 3-5 seconds

### Step 3: Verify It Works ✓
- Should get AI response
- No more "model not available" error
- Can ask multiple questions

---

## 🔧 If Still Not Working

### Quick Test: Which Endpoint Works?
```bash
bash test_gemini_endpoint.sh
```

This tests 4 different endpoints and shows which one works with your API key.

### Check Google Cloud Console
1. Go to https://console.cloud.google.com/
2. APIs & Services → Library
3. Search "Generative Language API"
4. Make sure it's ENABLED ✓

### Try Alternative Endpoints
If `gemini-pro` doesn't work, edit this file:
```
app/src/main/java/com/tannu/edureach/utils/GeminiApiService.kt
```

Try these in order:

**Option 1:**
```kotlin
@POST("v1beta/models/gemini-pro:generateContent")
```

**Option 2:**
```kotlin
@POST("v1beta/models/gemini-1.5-flash:generateContent")
```

**Option 3:**
```kotlin
@POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
```

After changing, rebuild the app!

---

## 📊 What Changed

### Before (Not Working) ❌
```kotlin
@POST("v1beta/models/gemini-1.5-flash:generateContent")
```

### After (Should Work) ✅
```kotlin
@POST("v1/models/gemini-pro:generateContent")
```

---

## 🐛 Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| 404 | Model not found | Try alternative endpoints |
| 403 | Permission denied | Enable API in Google Cloud Console |
| 429 | Too many requests | Wait 5 minutes, try again |
| Network error | No internet | Check WiFi/mobile data |

---

## ✅ Success Checklist

- [ ] Rebuilt app completely
- [ ] Installed fresh APK
- [ ] Tested AI Tutor
- [ ] Got response to "What is 2+2?"
- [ ] Can ask multiple questions
- [ ] No more 404 errors

---

## 🎯 Bottom Line

1. **Rebuild the app** (most important!)
2. **Test AI Tutor** with simple question
3. **If still failing**, run test script to find working endpoint

The fix is applied, you just need to rebuild!

```bash
./gradlew clean assembleDebug installDebug
```
