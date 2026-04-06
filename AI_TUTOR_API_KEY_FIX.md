# AI Tutor API Key Fix

## ❌ Problem Identified

The API key in `local.properties` is **INVALID**:
```
GEMINI_API_KEY=20889e96f7214928b7d63c44fa7cfd7b
```

This is NOT a valid Gemini API key because:
- ❌ Only 32 characters (should be ~39)
- ❌ Doesn't start with `AIza`
- ❌ Wrong format

## ✅ Solution

You need a **real Gemini API key** from Google.

### Quick Steps:

1. **Get API Key**:
   - Go to: https://makersuite.google.com/app/apikey
   - Sign in with Google account
   - Click "Create API Key"
   - Copy the key (starts with `AIza`)

2. **Update local.properties**:
   ```
   GEMINI_API_KEY=AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM
   ```
   (Replace with your actual key)

3. **Rebuild**:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

4. **Test**: Open AI Tutor and ask a question

## Valid API Key Format

✅ Correct format:
```
AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM
```
- Starts with `AIza`
- About 39 characters long
- Mix of letters, numbers, hyphens

❌ Your current key:
```
20889e96f7214928b7d63c44fa7cfd7b
```
- Doesn't start with `AIza`
- Only 32 characters
- Not a Gemini API key

## Error You're Seeing

```
HTTP 400: {"error": {"code": 400,"message": "API key not valid. Please pass a valid API key."}}
```

This confirms the API key is invalid.

## Free Tier

Google's Gemini API is FREE for:
- 60 requests per minute
- 1,500 requests per day
- Perfect for development and testing

## Important

⚠️ **Never share your API key publicly**
⚠️ **Never commit `local.properties` to Git**
⚠️ **Keep it secure**

## Summary

The AI Tutor is not working because you're using an invalid API key. Get a real Gemini API key from Google AI Studio, add it to `local.properties`, rebuild, and it will work!

See `GET_GEMINI_API_KEY.md` for detailed instructions.
