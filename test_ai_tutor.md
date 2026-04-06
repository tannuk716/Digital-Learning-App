# AI Tutor Testing Guide

## Quick Test Steps

### 1. Build and Install
```bash
./gradlew clean assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 2. Enable Logging
Open Android Studio Logcat and filter by "AIChatbot"

### 3. Test Cases

#### Test 1: Normal Question
1. Open AI Tutor from Student Dashboard
2. Type: "What is 2+2?"
3. Click Send
4. **Expected**: Should see "Thinking..." then get a response like "2+2 equals 4"

#### Test 2: Math Question
1. Type: "Explain photosynthesis"
2. Click Send
3. **Expected**: Should get an educational explanation appropriate for the student's class level

#### Test 3: Network Error Simulation
1. Turn off WiFi and Mobile Data
2. Type: "Hello"
3. Click Send
4. **Expected**: Should see "No internet connection. Please check your network and try again."
5. Should see a "Retry" button

#### Test 4: Retry Functionality
1. After getting an error message
2. Turn internet back on
3. Click the "Retry" button
4. **Expected**: Should retry the same question and get a response

### 4. Check Logs

Look for these log entries:
```
D/AIChatbot: Making API call with key: AIzaSyBvHT...
D/OkHttp: --> POST https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=...
D/OkHttp: <-- 200 OK
```

### 5. Common Log Patterns

**Success:**
```
D/AIChatbot: Making API call with key: AIzaSyBvHT...
D/OkHttp: <-- 200 OK
```

**No Internet:**
```
E/AIChatbot: No internet connection
java.net.UnknownHostException: Unable to resolve host...
```

**API Error:**
```
E/AIChatbot: API Error: [error message]
```

**Timeout:**
```
E/AIChatbot: Request timeout
java.net.SocketTimeoutException: timeout
```

## Troubleshooting

### If AI doesn't respond:

1. **Check Logcat** for error messages
2. **Verify API Key**: Look for "Making API call with key: AIzaSy..."
3. **Check Response Code**: Look for "<-- XXX" in logs
4. **Test API Key manually**:
   ```bash
   curl "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM" \
     -H 'Content-Type: application/json' \
     -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
   ```

### If you see "API key issue":

1. The API key validation failed
2. Check `BuildConfig.GEMINI_API_KEY` value
3. Verify key in `local.properties` or `build.gradle.kts`
4. Rebuild the app after changing the key

### If you see "Network error (XXX)":

- **400**: Bad request format - check logs for request body
- **401/403**: Invalid API key or permissions
- **429**: Rate limit exceeded - wait and retry
- **500/503**: Google server issue - try again later

## Expected Behavior

✅ **Working Correctly:**
- Shows "Thinking..." while waiting
- Displays AI response in chat
- Scrolls to show new messages
- Retry button appears on errors
- Clear error messages for different scenarios

❌ **Not Working:**
- No "Thinking..." indicator
- Blank responses
- Generic "Network Failed" message
- No retry option
- App crashes

## Performance Expectations

- **Response Time**: 2-10 seconds depending on question complexity
- **Timeout**: 60 seconds maximum
- **Retry**: Should work immediately after fixing network issue

## API Quota

The free tier Gemini API has limits:
- 60 requests per minute
- 1,500 requests per day

If you hit these limits, you'll see "Too many requests" error.
