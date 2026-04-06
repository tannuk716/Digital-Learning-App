#!/bin/bash

echo "========================================"
echo "AI Tutor Fix - Rebuild and Install"
echo "========================================"
echo ""
echo "This will:"
echo "1. Clean previous build"
echo "2. Rebuild the app with your API key"
echo "3. Install on your device"
echo ""
read -p "Press Enter to continue..."

echo ""
echo "[1/3] Cleaning previous build..."
./gradlew clean
if [ $? -ne 0 ]; then
    echo "[ERROR] Clean failed!"
    exit 1
fi
echo "[OK] Clean successful"
echo ""

echo "[2/3] Building app with API key..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo "[ERROR] Build failed!"
    exit 1
fi
echo "[OK] Build successful"
echo ""

echo "[3/3] Installing on device..."
adb install -r app/build/outputs/apk/debug/app-debug.apk
if [ $? -ne 0 ]; then
    echo "[ERROR] Installation failed!"
    echo ""
    echo "Make sure:"
    echo "- Device is connected (check with: adb devices)"
    echo "- USB debugging is enabled"
    echo "- Device is unlocked"
    exit 1
fi
echo "[OK] Installation successful"
echo ""

echo "========================================"
echo "SUCCESS! App installed with API key"
echo "========================================"
echo ""
echo "Now test the AI Tutor:"
echo "1. Open the app on your device"
echo "2. Go to Student Dashboard"
echo "3. Click AI Tutor"
echo "4. Ask: 'What is 2+2?'"
echo "5. You should get a response!"
echo ""
echo "If still not working, run: adb logcat -s GeminiApiHelper:D"
echo ""
