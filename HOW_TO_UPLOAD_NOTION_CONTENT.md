# How to Upload Notion Content - Step by Step

## Problem

The Notion links from your JSON file are not redirecting because **the content hasn't been uploaded to Firebase yet**.

## Solution

You need to run `UploadNotionContentActivity` ONCE to upload all 50 Notion links to Firebase.

---

## Step-by-Step Instructions

### Step 1: Add Upload Button to Teacher Dashboard (Temporary)

Open `app/src/main/res/layout/activity_teacher_dashboard.xml` and add this button after `btnAddGame`:

```xml
<Button
    android:id="@+id/btnUploadNotion"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="📓 Upload Notion Content (One Time)"
    android:background="@drawable/bg_gradient_orange"
    android:textColor="#FFFFFF"
    android:layout_marginBottom="16dp"
    android:elevation="4dp"/>
```

### Step 2: Add Button Click Handler

Open `app/src/main/java/com/tannu/edureach/TeacherDashboardActivity.kt` and add this in `onCreate()`:

```kotlin
findViewById<Button>(R.id.btnUploadNotion)?.setOnClickListener {
    startActivity(Intent(this, UploadNotionContentActivity::class.java))
}
```

### Step 3: Upload Content

1. **Build and run the app**
2. **Login as teacher** (teacher@test.com / test123)
3. **Click "Upload Notion Content (One Time)" button**
4. **Click "Delete Old Content"** (optional, cleans database)
5. **Click "Upload Content"**
6. **Wait for success message**: "Success! Uploaded 50 notes"

### Step 4: Verify Upload

**Check Firebase Console:**
1. Go to Firebase Console → Firestore Database
2. Navigate to: `classes` → `class_1` → `subjects` → `maths` → `units` → `unit_1` → `notes`
3. You should see a document with:
   - `title`: "Maths - Complete Notes"
   - `fileUrl`: "https://www.notion.so/MATHS-..."
   - `timestamp`: (current time)

### Step 5: Test as Student

1. **Logout from teacher account**
2. **Login as student** (test@test.com / test123)
3. **Make sure student profile is Class 1**
4. **Click "Maths" card** on dashboard
5. **You should see**: "Maths - Complete Notes" under Unit 1
6. **Click on it** → Opens Notion page

### Step 6: Remove Upload Button (After Upload)

Once content is uploaded, you can remove the button from the layout since it's only needed once.

---

## What Gets Uploaded

When you click "Upload Content", it uploads **50 Notion links** to Firebase:

```
Class 1:
  - English → https://www.notion.so/english-32ec1523f9cf803d8eaaf92fb2c58e0c
  - Maths → https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47
  - Hindi → https://www.notion.so/hindi-32ec1523f9cf8085a8b6d0ec41abf85e

Class 2:
  - Maths → https://www.notion.so/maths-32ec1523f9cf8016a6a4f72049e66674
  - English → https://www.notion.so/english-32ec1523f9cf80c98edcebde6174b6a6
  - EVS → https://www.notion.so/evs-32ec1523f9cf8065a63ff65016e8959f
  - Hindi → https://www.notion.so/hindi-32ec1523f9cf804e80f3c70505a73935

... (continues for all 10 classes)

Class 10:
  - English → https://www.notion.so/english-32fc1523f9cf808cb3c3c89a666bd032
  - Hindi → https://www.notion.so/hindi-32fc1523f9cf803ab125e14cb6864a6c
  - Maths → https://www.notion.so/maths-32fc1523f9cf801d8681c960c207e26e
  - Science → https://www.notion.so/science-32fc1523f9cf801cbfe7c96eeb79ab12
  - SST → https://www.notion.so/sst-32fc1523f9cf80ab8018e1b2c13ec8c5
```

---

## Firebase Structure After Upload

```
classes/
  ├── class_1/
  │   └── subjects/
  │       ├── english/
  │       │   └── units/
  │       │       └── unit_1/
  │       │           └── notes/
  │       │               └── {documentId}/
  │       │                   ├── title: "English - Complete Notes"
  │       │                   ├── fileUrl: "https://www.notion.so/english-..."
  │       │                   ├── description: "Complete study material..."
  │       │                   └── timestamp: 1234567890
  │       ├── maths/
  │       │   └── units/
  │       │       └── unit_1/
  │       │           └── notes/
  │       │               └── {documentId}/
  │       │                   ├── title: "Maths - Complete Notes"
  │       │                   └── fileUrl: "https://www.notion.so/MATHS-..."
  │       └── hindi/
  └── class_2/
      └── ... (same structure)
```

---

## How Students Access Content

### Method 1: Subject Cards (Main Way)
```
Student Dashboard
    ↓
Click "Maths" card
    ↓
SubjectContentActivity opens
    ↓
Shows "Unit 1"
    ↓
Shows "Maths - Complete Notes"
    ↓
Click → Opens Notion page
```

### Method 2: Notes Section
```
Student Dashboard
    ↓
Click "Notes" card
    ↓
Select "Maths"
    ↓
Shows "Maths - Complete Notes"
    ↓
Click → Opens Notion page
```

---

## Troubleshooting

### Issue: Content Not Showing

**Cause**: Content not uploaded yet

**Solution**: Follow Step 3 above to upload content

---

### Issue: Wrong Class Content Showing

**Cause**: Student profile has wrong class

**Solution**:
1. Click profile icon
2. Edit profile
3. Set correct class (e.g., "Class 1")
4. Save
5. Return to dashboard

---

### Issue: Upload Button Not Visible

**Cause**: Button not added to layout

**Solution**: Follow Step 1 to add button to layout

---

### Issue: "Permission Denied" Error

**Cause**: Firebase rules not configured

**Solution**: Check Firebase Console → Firestore → Rules

---

## Alternative: Upload via Android Studio

If you can't add the button, you can open the activity directly:

1. Open Android Studio
2. Go to `Run` → `Edit Configurations`
3. Change `Launch` to `Specified Activity`
4. Enter: `com.tannu.edureach.UploadNotionContentActivity`
5. Run the app
6. Activity opens directly
7. Click "Upload Content"

---

## Summary

**The Notion links are in your code** (`UploadNotionContentActivity.kt`), but they need to be **uploaded to Firebase** first.

**Steps**:
1. Add upload button to teacher dashboard
2. Login as teacher
3. Click "Upload Notion Content"
4. Upload content (one time only)
5. Content now available to all students
6. Students can click and open Notion links

**After upload**: Content will redirect correctly to your Notion pages!

---

## Quick Test

After uploading, test with:

```
1. Login as student (Class 1)
2. Click "Maths" card
3. Should see "Maths - Complete Notes"
4. Click it
5. Should open: https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47
```

If this works, all 50 Notion links are uploaded correctly!
