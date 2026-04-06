#!/bin/bash

echo "==================================="
echo "AI Tutor 404 Fix - Testing Script"
echo "==================================="
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Clean build
echo -e "${YELLOW}Step 1: Cleaning previous build...${NC}"
./gradlew clean
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Clean successful${NC}"
else
    echo -e "${RED}✗ Clean failed${NC}"
    exit 1
fi
echo ""

# Step 2: Build debug APK
echo -e "${YELLOW}Step 2: Building debug APK...${NC}"
./gradlew assembleDebug
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi
echo ""

# Step 3: Install on device
echo -e "${YELLOW}Step 3: Installing on device...${NC}"
adb install -r app/build/outputs/apk/debug/app-debug.apk
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Installation successful${NC}"
else
    echo -e "${RED}✗ Installation failed${NC}"
    echo "Make sure a device is connected: adb devices"
    exit 1
fi
echo ""

# Step 4: Clear logcat
echo -e "${YELLOW}Step 4: Clearing logcat...${NC}"
adb logcat -c
echo -e "${GREEN}✓ Logcat cleared${NC}"
echo ""

# Step 5: Start logging
echo -e "${YELLOW}Step 5: Starting logcat monitoring...${NC}"
echo -e "${GREEN}Watching for AI Tutor logs...${NC}"
echo ""
echo "==================================="
echo "Now open the app and test AI Tutor"
echo "==================================="
echo ""
echo "Look for these log patterns:"
echo "  - 'Trying endpoint X/5' - Shows which endpoint is being tried"
echo "  - 'Success with endpoint' - Shows which endpoint worked"
echo "  - 'HTTP 404' - Shows if an endpoint failed"
echo ""
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor logs
adb logcat -s GeminiApiHelper:D AIChatbot:D OkHttp:D
