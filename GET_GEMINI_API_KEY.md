# How to Get a Valid Gemini API Key

## Problem
The API key `20889e96f7214928b7d63c44fa7cfd7b` is NOT a valid Gemini API key.

Valid Gemini API keys:
- Start with `AIza`
- Are about 39 characters long
- Example: `AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM`

## How to Get Your Gemini API Key

### Step 1: Go to Google AI Studio
Visit: https://makersuite.google.com/app/apikey

Or: https://aistudio.google.com/app/apikey

### Step 2: Sign In
- Sign in with your Google account
- Accept the terms of service if prompted

### Step 3: Create API Key
1. Click "Create API Key" button
2. Select "Create API key in new project" (or use existing project)
3. Copy the generated API key
4. It will look like: `AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM`

### Step 4: Add to local.properties
1. Open `local.properties` file in your project root
2. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key:
   ```
   GEMINI_API_KEY=AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM
   ```
3. Save the file

### Step 5: Rebuild the App
```
Build → Clean Project
Build → Rebuild Project
```

## Important Notes

### Free Tier Limits
- 60 requests per minute
- 1,500 requests per day
- Free to use for development and testing

### API Key Security
- ⚠️ Never commit `local.properties` to Git
- ⚠️ Never share your API key publicly
- ⚠️ Keep it secret and secure

### If You Don't Have a Google Account
1. Create a Google account at https://accounts.google.com
2. Then follow the steps above

## Alternative: Use Environment Variable

If you prefer, you can also set the API key as an environment variable:

### Windows:
```cmd
setx GEMINI_API_KEY "AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM"
```

### Linux/Mac:
```bash
export GEMINI_API_KEY="AIzaSyBvHTTXpivNaGAvk7c-GeX7snA4Xse3IxM"
```

## Troubleshooting

### Error: "API key not valid"
- Check that your API key starts with `AIza`
- Check that you copied the entire key (no spaces)
- Make sure you saved `local.properties`
- Rebuild the app after changing the key

### Error: "API key not found"
- Make sure `local.properties` exists in project root
- Check the file has `GEMINI_API_KEY=...` line
- Rebuild the app

### Error: "Quota exceeded"
- You've hit the free tier limit
- Wait for the quota to reset (daily/minute limits)
- Or upgrade to a paid plan

## Testing Your API Key

After adding the key and rebuilding:

1. Open the app
2. Go to AI Tutor
3. Ask a simple question like "Hello"
4. If it responds, your API key is working!

## Current Status

❌ Current key in `local.properties`: `20889e96f7214928b7d63c44fa7cfd7b`
   - This is NOT a valid Gemini API key
   - It's only 32 characters (should be ~39)
   - Doesn't start with `AIza`

✅ You need to replace it with a real Gemini API key from Google AI Studio

## Quick Fix

1. Go to: https://makersuite.google.com/app/apikey
2. Create API key
3. Copy it (starts with `AIza`)
4. Paste in `local.properties`:
   ```
   GEMINI_API_KEY=AIzaSy... (your key here)
   ```
5. Rebuild app
6. Test AI Tutor

## Summary

The key you're using (`20889e96f7214928b7d63c44fa7cfd7b`) is not a valid Gemini API key. You need to:
1. Get a real API key from Google AI Studio
2. Replace it in `local.properties`
3. Rebuild the app

The AI Tutor will work once you have a valid API key!
