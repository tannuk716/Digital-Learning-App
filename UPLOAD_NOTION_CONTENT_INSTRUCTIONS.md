# Upload Notion Content - Simple Instructions

## ✅ What Was Created

I've created a simple upload activity that will:
1. Delete all old/duplicate content
2. Upload your new Notion content
3. Make it visible in student Notes section

## 🚀 How to Use (3 Simple Steps)

### Step 1: Rebuild the App
```bash
./gradlew clean assembleDebug installDebug
```

### Step 2: Open Upload Activity
1. Login as teacher (use `teacher@test.com` / `test123`)
2. You'll see a button "Upload Notion Content" in teacher dashboard
3. Click it

OR directly open the activity:
```bash
adb shell am start -n com.tannu.edureach/.UploadNotionContentActivity
```

### Step 3: Upload Content
1. Click "Step 1: Delete Old Content" button
2. Wait for "Deleted X notes" message
3. Click "Step 2: Upload New Content" button
4. Wait for "Success! Uploaded X notes" message
5. Done!

## 📊 What Gets Uploaded

### Content Structure
```
Class 1:
  - English → Unit 1 → "English - Complete Notes" (Notion link)
  - Maths → Unit 1 → "Maths - Complete Notes" (Notion link)
  - Hindi → Unit 1 → "Hindi - Complete Notes" (Notion link)

Class 2:
  - English, Maths, Hindi, EVS → Unit 1 → Notes

...and so on for all 10 classes
```

### Total Content
- **Classes**: 1-10
- **Subjects**: English, Hindi, Maths, Science, EVS, SST
- **Total Notes**: ~45 notes

## 📱 How Students See It

### Step 1: Student Logs In
- Login as student (Class 1)

### Step 2: Go to Notes
- Click "Notes" card in dashboard
- Select subject (e.g., "English")

### Step 3: See Uploaded Content
- Will see "English - Complete Notes"
- Click to open Notion link
- Can view/download content

## ✅ Features

- ✅ **Delete Old Content** - Removes all previous notes first
- ✅ **Upload New Content** - Uploads your Notion links
- ✅ **Class-wise Organization** - Organized by class and subject
- ✅ **Unit-wise** - All uploaded to unit_1
- ✅ **Downloadable** - Notion links are accessible
- ✅ **No Duplicates** - Old content deleted first

## 🔧 Troubleshooting

### Problem: Button not visible in Teacher Dashboard
**Solution:** Add button to teacher dashboard layout:

```xml
<Button
    android:id="@+id/btnUploadNotion"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Upload Notion Content"
    android:backgroundTint="#2196F3"/>
```

### Problem: Network error during upload
**Solution:** Use real device with internet connection

### Problem: Upload fails
**Solution:** 
1. Check internet connection
2. Make sure you're logged in as teacher
3. Check Firebase permissions

## 📝 What Happens

### When You Click "Delete Old Content":
1. Scans all classes (1-10)
2. Scans all subjects
3. Scans all units (1-3)
4. Deletes all notes found
5. Shows "Deleted X notes"

### When You Click "Upload New Content":
1. Reads Notion links from code
2. Creates note for each subject
3. Uploads to Firebase
4. Shows "Uploaded X notes"

## ✅ Verification

### Check in Firebase Console:
1. Go to Firebase Console
2. Firestore Database
3. Navigate to: `classes` → `class_1` → `subjects` → `english` → `units` → `unit_1` → `notes`
4. Should see "English - Complete Notes"

### Check in App:
1. Login as student (Class 1)
2. Click "Notes"
3. Select "English"
4. Should see "English - Complete Notes"
5. Click to open Notion link

## 🎯 Summary

**What to Do:**
1. Rebuild app
2. Open UploadNotionContentActivity
3. Click "Delete Old Content"
4. Click "Upload New Content"
5. Done!

**Result:**
- All old content deleted
- New Notion content uploaded
- Visible in student Notes section
- Organized by class and subject

**Time Required:** 2-3 minutes

The upload activity is ready to use! Just rebuild and run it! 🚀
