# 🔐 Security Setup Guide

## ⚠️ CRITICAL: API Keys Have Been Exposed

**All API keys in this repository have been compromised and MUST be regenerated immediately.**

## 🚨 Immediate Actions Required

### 1. Regenerate ALL API Keys

#### Gemini API Key
1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. **DELETE** the old API key immediately
3. Create a new API key
4. Copy the new key (starts with `AIza...`)

#### Firebase Configuration
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `rurallearningapp-ef79c`
3. Go to Project Settings → General
4. **Regenerate** your Firebase configuration
5. Download the new `google-services.json` file

### 2. Setup Local Configuration Files

#### Create `local.properties`
```properties
# Android SDK location (update to your path)
sdk.dir=/path/to/your/Android/Sdk

# Gemini API Key (get from https://makersuite.google.com/app/apikey)
GEMINI_API_KEY=YOUR_NEW_GEMINI_API_KEY_HERE
```

#### Add `google-services.json`
1. Download your NEW `google-services.json` from Firebase Console
2. Place it in `app/google-services.json`
3. **Never commit this file to Git**

### 3. Verify .gitignore

Ensure these files are in `.gitignore`:
```
local.properties
google-services.json
app/google-services.json
*.env
.env
```

## 📋 Environment Variables Required

| Variable | Description | Where to Get |
|----------|-------------|--------------|
| `GEMINI_API_KEY` | Google Gemini AI API Key | [Google AI Studio](https://makersuite.google.com/app/apikey) |
| `google-services.json` | Firebase configuration | [Firebase Console](https://console.firebase.google.com/) |

## 🔧 Setup Instructions

### Step 1: Clone the Repository
```bash
git clone https://github.com/tannuk716/Digital-Learning-App.git
cd Digital-Learning-App
```

### Step 2: Create Configuration Files

#### Copy example files:
```bash
cp local.properties.example local.properties
cp app/google-services.json.example app/google-services.json
```

#### Edit `local.properties`:
```properties
sdk.dir=/your/android/sdk/path
GEMINI_API_KEY=your_new_gemini_api_key_here
```

#### Edit `app/google-services.json`:
Replace with your actual Firebase configuration from Firebase Console.

### Step 3: Build the Project
```bash
./gradlew clean build
```

## 🛡️ Security Best Practices

### ✅ DO:
- Keep `local.properties` and `google-services.json` in `.gitignore`
- Use environment variables for sensitive data
- Regenerate API keys if accidentally exposed
- Use API key restrictions in Google Cloud Console
- Review commits before pushing

### ❌ DON'T:
- Commit API keys or secrets to Git
- Share `local.properties` or `google-services.json` files
- Hardcode API keys in source code
- Push sensitive data to public repositories

## 🔍 Checking for Exposed Secrets

Before committing, always check:
```bash
# Search for potential API keys
git grep -i "AIza"
git grep -i "api_key"
git grep -i "password"

# Check what will be committed
git diff --cached
```

## 📞 Support

If you accidentally expose secrets:
1. **Immediately** regenerate all exposed keys
2. Update your local configuration
3. Force push the cleaned repository
4. Contact your team lead

## 🔗 Useful Links

- [Google AI Studio](https://makersuite.google.com/app/apikey) - Get Gemini API Key
- [Firebase Console](https://console.firebase.google.com/) - Firebase Configuration
- [Google Cloud Console](https://console.cloud.google.com/) - API Key Management

---

**Last Updated**: April 6, 2026  
**Status**: ⚠️ Repository cleaned - All developers must regenerate keys
