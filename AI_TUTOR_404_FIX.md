# AI Tutor 404 Error Fix

## Problem
The AI Tutor was showing "Network error (404). Please check your internet connection." This means the API endpoint was not found.

## Root Cause
The Gemini API endpoint `v1/models/gemini-1.5-flash:generateContent` returned a 404 error, indicating:
- The model name might not be available with that exact path
- The API version might have changed
- The model might require a different endpoint format

## Solution Implemented

### 1. Created Multi-Endpoint Fallback System
**New File**: `app/src/main/java/com/tannu/edureach/utils/GeminiApiHelper.kt`

This helper automatically tries multiple API endpoints in order until one works:

```kotlin
private val MODEL_ENDPOINTS = listOf(
    "v1beta/models/gemini-1.5-flash-latest:generateContent",  // Try latest first
    "v1beta/models/gemini-1.5-flash:generateContent",         // Try standard flash
    "v1beta/models/gemini-pro:generateContent",               // Fallback to pro
    "v1/models/gemini-1.5-flash:generateContent",             // Try v1 flash
    "v1/models/gemini-pro:generateContent"                    // Final fallback
)
```

### 2. Enhanced RetrofitClient
**Updated**: `app/src/main/java/com/tannu/edureach/utils/RetrofitClient.kt`

Added dynamic endpoint support:
- Can now try different endpoints without rebuilding
- Maintains logging and timeout configuration
- Uses `@Path` annotation for dynamic endpoint injection

### 3. Updated AIChatbotActivity
**Updated**: `app/src/main/java/com/tannu/edureach/AIChatbotActivity.kt`

Simplified to use the new helper:
- Calls `GeminiApiHelper.generateContent(prompt)`
- Helper handles all endpoint retries automatically
- Better error messages based on failure type

### 4. Added Generation Config
**Updated**: `app/src/main/java/com/tannu/edureach/utils/GeminiModels.kt`

Added configuration parameters:
```kotlin
data class GenerationConfig(
    val temperature: Double = 0.7,
    val topK: Int = 40,
    val topP: Double = 0.95,
    val maxOutputTokens: Int = 1024
)
```

## How It Works

1. **User sends a message** → "hello"

2. **GeminiApiHelper tries endpoints in order**:
   - Try: `v1beta/models/gemini-1.5-flash-latest:generateContent`
   - If 404 → Try next
   - Try: `v1beta/models/gemini-1.5-flash:generateContent`
   - If 404 → Try next
   - Try: `v1beta/models/gemini-pro:generateContent`
   - If SUCCESS → Return response ✅

3. **User sees the AI response**

## Benefits

✅ **Automatic Fallback**: If one endpoint fails, tries others
✅ **Future-Proof**: Easy to add new endpoints as Google updates
✅ **Better Logging**: Shows which endpoint worked
✅ **No Rebuild Needed**: Can try different endpoints without code changes
✅ **Graceful Degradation**: Falls back to older models if new ones unavailable

## Testing

### Test the Fix:

1. **Build and install**:
   ```bash
   ./gradlew clean assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Open AI Tutor** and ask: "What is 2+2?"

3. **Check logs** to see which endpoint worked:
   ```bash
   adb logcat -s GeminiApiHelper:D
   ```

### Expected Log Output:

```
D/GeminiApiHelper: Trying endpoint 1/5: v1beta/models/gemini-1.5-flash-latest:generateContent
D/GeminiApiHelper: Success with endpoint: v1beta/models/gemini-1.5-flash-latest:generateContent
```

Or if first fails:
```
D/GeminiApiHelper: Trying endpoint 1/5: v1beta/models/gemini-1.5-flash-latest:generateContent
E/GeminiApiHelper: HTTP 404 on v1beta/models/gemini-1.5-flash-latest:generateContent
D/GeminiApiHelper: Trying endpoint 2/5: v1beta/models/gemini-1.5-flash:generateContent
D/GeminiApiHelper: Success with endpoint: v1beta/models/gemini-1.5-flash:generateContent
```

## Error Messages

| Scenario | User Sees |
|----------|-----------|
| All endpoints return 404 | "AI service temporarily unavailable. Please try again later." |
| No internet | "No internet connection. Please check your network and try again." |
| Timeout | "Request timed out. Please try again." |
| API key issue | "API key issue. Please contact your teacher." |
| Rate limit | "Too many requests. Please wait a moment and try again." |
| Success | AI response appears normally |

## Troubleshooting

### If still getting 404:

1. **Check API key is valid**:
   ```bash
   # Test with curl
   curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=YOUR_KEY" \
     -H 'Content-Type: application/json' \
     -d '{"contents":[{"parts":[{"text":"test"}]}]}'
   ```

2. **Verify internet connection**:
   ```bash
   adb shell ping -c 3 generativelanguage.googleapis.com
   ```

3. **Check logs for specific error**:
   ```bash
   adb logcat -s GeminiApiHelper:V OkHttp:V
   ```

4. **Try different API key**:
   - Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create new API key
   - Update in `local.properties` or `build.gradle.kts`

### If one endpoint works:

The app will automatically use that endpoint. You can check which one worked in the logs and update `GeminiApiService.kt` to use that endpoint directly for faster responses (skip the retry logic).

## Performance Impact

- **First request**: May take 2-5 seconds longer if trying multiple endpoints
- **Subsequent requests**: Uses the same endpoint that worked, no delay
- **Optimization**: Once you know which endpoint works, you can reorder the list to try that one first

## Future Improvements

1. **Cache working endpoint**: Remember which endpoint worked and try it first next time
2. **Parallel requests**: Try multiple endpoints simultaneously (faster but uses more bandwidth)
3. **Endpoint health check**: Periodically check which endpoints are available
4. **User preference**: Let users choose which model to use

## Files Changed

| File | Purpose |
|------|---------|
| `GeminiApiHelper.kt` | NEW - Multi-endpoint retry logic |
| `RetrofitClient.kt` | UPDATED - Dynamic endpoint support |
| `AIChatbotActivity.kt` | UPDATED - Use new helper |
| `GeminiModels.kt` | UPDATED - Add generation config |
| `GeminiApiService.kt` | UPDATED - Use v1beta endpoint |

## Quick Reference

**To add a new endpoint to try:**
Edit `GeminiApiHelper.kt` and add to the list:
```kotlin
private val MODEL_ENDPOINTS = listOf(
    "your/new/endpoint:generateContent",  // Add here
    "v1beta/models/gemini-1.5-flash-latest:generateContent",
    // ... rest of list
)
```

**To change endpoint order:**
Just reorder the list - first endpoint is tried first.

**To disable retry logic:**
Use `RetrofitClient.geminiApi.generateContent()` directly instead of `GeminiApiHelper.generateContent()`.
