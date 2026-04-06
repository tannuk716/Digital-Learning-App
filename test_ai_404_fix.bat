@echo off
echo ===================================
echo AI Tutor 404 Fix - Testing Script
echo ===================================
echo.

REM Step 1: Clean build
echo Step 1: Cleaning previous build...
call gradlew.bat clean
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Clean failed
    exit /b 1
)
echo [OK] Clean successful
echo.

REM Step 2: Build debug APK
echo Step 2: Building debug APK...
call gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed
    exit /b 1
)
echo [OK] Build successful
echo.

REM Step 3: Install on device
echo Step 3: Installing on device...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Installation failed
    echo Make sure a device is connected: adb devices
    exit /b 1
)
echo [OK] Installation successful
echo.

REM Step 4: Clear logcat
echo Step 4: Clearing logcat...
adb logcat -c
echo [OK] Logcat cleared
echo.

REM Step 5: Start logging
echo Step 5: Starting logcat monitoring...
echo [OK] Watching for AI Tutor logs...
echo.
echo ===================================
echo Now open the app and test AI Tutor
echo ===================================
echo.
echo Look for these log patterns:
echo   - 'Trying endpoint X/5' - Shows which endpoint is being tried
echo   - 'Success with endpoint' - Shows which endpoint worked
echo   - 'HTTP 404' - Shows if an endpoint failed
echo.
echo Press Ctrl+C to stop monitoring
echo.

REM Monitor logs
adb logcat -s GeminiApiHelper:D AIChatbot:D OkHttp:D
