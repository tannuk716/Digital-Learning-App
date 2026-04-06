# Quick Fix Summary - Notion Links Not Redirecting

## Problem
Notion links not redirecting because content is NOT in Firebase yet.

## What I Fixed
1. ✅ Fixed intent parameter bug in `StudentDashboardActivity.kt` (was using "URL", now uses "WEB_URL")
2. ✅ Fixed intent parameter bug in `SubjectContentActivity.kt` (was using "URL", now uses "WEB_URL")

## What You Need to Do (3 Steps)

### Step 1: Rebuild App
```bash
./gradlew clean build
```

### Step 2: Upload Notion Content
1. Run the app
2. Login as teacher: `teacher@test.com` / `test123`
3. Click "📓 Upload Notion Content (One Time)" button
4. Click "Upload Content"
5. Wait for: "Success! Uploaded 50 notes"

### Step 3: Test as Student
1. Logout
2. Login as student: `test@test.com` / `test123`
3. Make sure profile shows "Class 1"
4. Click "Maths" card
5. Click "Maths - Complete Notes"
6. Should open: https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47

## Why This Happens

Your 50 Notion links are hardcoded in `UploadNotionContentActivity.kt` but they need to be uploaded to Firebase first. Once uploaded, they'll be accessible to all students.

## What Gets Uploaded

- Class 1: 3 subjects (English, Maths, Hindi)
- Class 2: 4 subjects (Maths, English, EVS, Hindi)
- Class 3-5: 4 subjects each (English, Hindi, Maths, EVS)
- Class 6-7: 5 subjects each (English, Hindi, Maths, EVS, SST)
- Class 8-10: 5 subjects each (English, Hindi, Maths, Science, SST)

**Total: 50 Notion links**

## After Upload

✅ All Notion links will work correctly
✅ Students can access content by class
✅ Teachers can view and delete content
✅ Links redirect to correct Notion pages

---

**See `NOTION_LINKS_FIX_GUIDE.md` for detailed instructions and troubleshooting.**
