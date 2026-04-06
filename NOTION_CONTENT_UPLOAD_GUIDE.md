# Notion Content Upload Guide

## ✅ What Was Done

I've created a system to upload your Notion content links to Firebase as downloadable notes.

### Files Created

1. **notion_content_links.json** - Your Notion links organized by class and subject
2. **NotionContentUploader.kt** - Utility to upload content to Firebase
3. **app/src/main/assets/notion_content_links.json** - JSON file in assets folder

---

## 🚀 How to Upload Content

### Option 1: Add Upload Button to Teacher Dashboard (Recommended)

Add this code to `TeacherDashboardActivity.kt`:

```kotlin
// In onCreate() method, add this button
findViewById<Button>(R.id.btnUploadNotionContent)?.setOnClickListener {
    lifecycleScope.launch {
        try {
            // Show progress
            Toast.makeText(this@TeacherDashboardActivity, "Uploading Notion content...", Toast.LENGTH_SHORT).show()
            
            // Upload content
            val result = NotionContentUploader.uploadNotionContent(this@TeacherDashboardActivity)
            
            result.onSuccess { message ->
                Toast.makeText(this@TeacherDashboardActivity, message, Toast.LENGTH_LONG).show()
            }.onFailure { error ->
                Toast.makeText(this@TeacherDashboardActivity, "Upload failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@TeacherDashboardActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
```

And add this button to `activity_teacher_dashboard.xml`:

```xml
<Button
    android:id="@+id/btnUploadNotionContent"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Upload Notion Content"
    android:backgroundTint="#4CAF50"
    android:textColor="#FFFFFF"
    android:layout_margin="16dp"/>
```

### Option 2: Run from Code (One-Time)

Add this to `TeacherDashboardActivity.kt` in `onCreate()`:

```kotlin
// ONE-TIME UPLOAD - Remove after running once
lifecycleScope.launch {
    val result = NotionContentUploader.uploadNotionContent(this@TeacherDashboardActivity)
    result.onSuccess { message ->
        Log.d("NotionUpload", message)
    }
}
```

---

## 📊 What Gets Uploaded

### Content Structure

For each class and subject, a note is created:

```
Class 1 → English → Unit 1 → "English - Complete Notes" (Notion link)
Class 1 → Maths → Unit 1 → "Maths - Complete Notes" (Notion link)
Class 1 → Hindi → Unit 1 → "Hindi - Complete Notes" (Notion link)
...and so on for all classes
```

### Note Details

Each note contains:
- **Title**: "{Subject} - Complete Notes"
- **Description**: "Complete study material for {Subject}"
- **File URL**: Notion link (downloadable/viewable)
- **Timestamp**: Current time

---

## 🔍 How It Works

### 1. Duplicate Prevention

The uploader checks if a note with the same title and URL already exists before uploading. This prevents duplicates.

### 2. Class-wise Organization

Content is organized by:
- Class (class_1 to class_10)
- Subject (english, hindi, maths, science, evs, sst)
- Unit (unit_1 by default)

### 3. Downloadable Notes

The Notion links are stored as `fileUrl` in the notes, making them:
- ✅ Viewable in the app
- ✅ Downloadable
- ✅ Accessible from Notes section

---

## 📱 How Students Access Notes

### Step 1: Student Opens App
- Login as student
- Go to Dashboard

### Step 2: Navigate to Notes
- Click "Notes" card
- Select subject (e.g., "English")
- See list of notes

### Step 3: View/Download Note
- Click on note (e.g., "English - Complete Notes")
- Notion link opens
- Can view or download content

---

## 🗑️ Delete Old Content (Optional)

If you want to remove all existing notes before uploading new ones:

```kotlin
// WARNING: This deletes ALL notes!
lifecycleScope.launch {
    val result = NotionContentUploader.deleteAllNotes()
    result.onSuccess { message ->
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Then upload new content
        NotionContentUploader.uploadNotionContent(this)
    }
}
```

---

## ✅ Content Mapping

| Class | Subjects Available |
|-------|-------------------|
| Class 1 | English, Maths, Hindi |
| Class 2 | English, Maths, Hindi, EVS |
| Class 3 | English, Maths, Hindi, EVS |
| Class 4 | English, Maths, Hindi, EVS |
| Class 5 | English, Maths, Hindi, EVS |
| Class 6 | English, Maths, Hindi, EVS, SST |
| Class 7 | English, Maths, Hindi, EVS, SST |
| Class 8 | English, Maths, Hindi, Science, SST |
| Class 9 | English, Maths, Hindi, Science, SST |
| Class 10 | English, Maths, Hindi, Science, SST |

---

## 🔧 Customization

### Change Unit

By default, notes are uploaded to `unit_1`. To change:

```kotlin
// In NotionContentUploader.kt, change this line:
val unitId = "unit_1"  // Change to "unit_2" or "unit_3"
```

### Change Title Format

```kotlin
// In NotionContentUploader.kt, change this line:
title = "${subjectId.capitalize()} - Complete Notes"
// To something like:
title = "${subjectId.capitalize()} Study Material"
```

### Add More Metadata

```kotlin
val note = NoteContent(
    title = "${subjectId.capitalize()} - Complete Notes",
    description = "Complete study material for ${subjectId.capitalize()}",
    fileUrl = notionUrl,
    timestamp = System.currentTimeMillis(),
    // Add more fields if your NoteContent model supports them
)
```

---

## 📝 Testing

### Step 1: Upload Content
- Run the upload (using button or code)
- Check logs for "Upload complete!"

### Step 2: Verify in Firebase
1. Go to Firebase Console
2. Firestore Database
3. Navigate to: classes → class_1 → subjects → english → units → unit_1 → notes
4. Should see "English - Complete Notes"

### Step 3: Test in App
1. Login as student (Class 1)
2. Click "Notes"
3. Select "English"
4. Should see "English - Complete Notes"
5. Click to open Notion link

---

## ⚠️ Important Notes

### 1. Network Required
- Upload requires internet connection
- Use real device or fix emulator network

### 2. Firebase Permissions
- Make sure Firebase rules allow writing to notes collection
- Teacher account should have write permissions

### 3. Duplicate Prevention
- The uploader automatically skips duplicates
- Safe to run multiple times

### 4. Notion Links
- Links are stored as-is
- Students need internet to access Notion content
- Consider exporting Notion pages as PDFs for offline access

---

## 🎯 Summary

**What Was Created:**
- ✅ JSON file with all Notion links
- ✅ Upload utility with duplicate prevention
- ✅ Organized by class, subject, and unit
- ✅ Ready to upload to Firebase

**How to Use:**
1. Add upload button to Teacher Dashboard
2. Click button to upload
3. Content appears in Notes section
4. Students can access from their dashboard

**Next Steps:**
1. Add upload button to Teacher Dashboard
2. Rebuild app
3. Login as teacher
4. Click "Upload Notion Content"
5. Wait for "Upload complete!" message
6. Test as student

The Notion content is ready to be uploaded! 🚀
