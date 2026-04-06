# Debug Guide: Notes Not Showing in Subject

## Issue
Teacher uploaded a PDF for Class 2 English, but it's not appearing in Notes → English.

## Debug Steps

### Step 1: Check Logcat Output
I've added debug logging to the app. After rebuilding, check Logcat for these messages:

```
Filter by tag: NoteList
```

Look for:
```
D/NoteList: Loading content for classId=class_2, subjectId=english
D/NoteList: Found X notes in unit_Y
D/NoteList:   - [Title] ([URL])
D/NoteList: Total notes before filtering: X
D/NoteList: Total notes after filtering: X
D/NoteList: Total videos: X
D/NoteList: Total content items to display: X
```

### Step 2: Verify Firebase Data Structure

The content should be stored at this path:
```
classes/class_2/subjects/english/units/unit_X/notes/{noteId}
```

Check Firebase Console:
1. Go to Firestore Database
2. Navigate to: `classes` → `class_2` → `subjects` → `english` → `units`
3. Check which unit (unit_1, unit_2, etc.) contains the note
4. Verify the note document has these fields:
   - `title`: String
   - `description`: String
   - `fileUrl`: String (the PDF URL)
   - `timestamp`: Number

### Step 3: Common Issues & Solutions

#### Issue 1: Wrong Subject ID
**Problem**: Content uploaded with subject ID "English" instead of "english"

**Check**: In Firebase, the subject document name should be lowercase: `english` not `English`

**Solution**: When uploading, ensure the subject is converted to lowercase:
```kotlin
val subjectId = subjectName.lowercase().replace(" ", "_")
```

#### Issue 2: Wrong Class ID
**Problem**: Content uploaded to class_1 instead of class_2

**Check**: Verify the class path in Firebase matches the student's class

**Solution**: Ensure teacher selects correct class when uploading

#### Issue 3: Content in Wrong Collection
**Problem**: PDF uploaded to "videos" collection instead of "notes"

**Check**: In Firebase, verify the PDF is in the `notes` subcollection, not `videos`

**Solution**: When uploading PDFs, ensure they go to the notes collection

#### Issue 4: Empty or Missing Fields
**Problem**: The note document exists but has empty title or fileUrl

**Check**: Verify all required fields have values:
```
title: "English PDF"  ✓
fileUrl: "https://..."  ✓
description: "..."  ✓
```

**Solution**: Re-upload with all fields filled

#### Issue 5: Notion URL Filter
**Problem**: The PDF URL contains "notion.so" and is being filtered out

**Check**: Look at the fileUrl - does it contain "notion.so"?

**Solution**: If it's a legitimate teacher upload (not Notion content), the URL shouldn't contain "notion.so"

### Step 4: Manual Firebase Query Test

To test if the data exists, you can manually query Firebase:

1. Open Firebase Console
2. Go to Firestore
3. Navigate to: `classes/class_2/subjects/english/units/unit_1/notes`
4. Check if any documents exist
5. If yes, verify the document structure

### Step 5: Check Upload Activity

Verify the upload was successful:

1. Check `AddLearningContentActivity.kt`
2. Ensure the upload logic is:
   ```kotlin
   val classId = "class_2"
   val subjectId = "english"
   val unitId = "unit_X"
   
   repository.uploadNote(classId, subjectId, unitId, note)
   ```

### Step 6: Verify Student Class

Ensure the student is actually in Class 2:

1. Check Firebase: `users/{userId}/className`
2. Should be: `"Class 2"` (with capital C and space)
3. This gets converted to `class_2` in the code

## Expected Logcat Output (Working)

```
D/NoteList: Loading content for classId=class_2, subjectId=english
D/NoteList: Found 1 notes in unit_1
D/NoteList:   - English PDF (https://drive.google.com/...)
D/NoteList: Total notes before filtering: 1
D/NoteList: Total notes after filtering: 1
D/NoteList: Total videos: 0
D/NoteList: Total content items to display: 1
```

## Expected Logcat Output (Not Working)

```
D/NoteList: Loading content for classId=class_2, subjectId=english
D/NoteList: Total notes before filtering: 0
D/NoteList: Total notes after filtering: 0
D/NoteList: Total videos: 0
D/NoteList: Total content items to display: 0
```

This means no data was found in Firebase at that path.

## Quick Fix Checklist

- [ ] Rebuild the app with debug logging
- [ ] Open the app and navigate to Notes → English
- [ ] Check Logcat for debug messages
- [ ] Verify Firebase path: `classes/class_2/subjects/english/units/unit_X/notes`
- [ ] Check document fields: title, fileUrl, description, timestamp
- [ ] Verify subject ID is lowercase: "english" not "English"
- [ ] Verify class ID format: "class_2" not "Class 2"
- [ ] Ensure PDF is in "notes" collection, not "videos"
- [ ] Check fileUrl doesn't contain "notion.so"

## Testing Upload

To test if upload works correctly:

1. Login as teacher
2. Go to Add Content
3. Select:
   - Class: Class 2
   - Subject: English
   - Unit: Unit 1
   - Type: Note (not Video)
4. Enter:
   - Title: "Test English PDF"
   - URL: [Google Drive PDF link]
   - Description: "Test description"
5. Click Upload
6. Check Firebase Console immediately
7. Navigate to: `classes/class_2/subjects/english/units/unit_1/notes`
8. Verify new document appears

## Firebase Console Direct Link

```
https://console.firebase.google.com/project/[YOUR_PROJECT_ID]/firestore/data/~2Fclasses~2Fclass_2~2Fsubjects~2Fenglish~2Funits~2Funit_1~2Fnotes
```

Replace `[YOUR_PROJECT_ID]` with your actual Firebase project ID.

## If Still Not Working

If content still doesn't show after verifying all above:

1. **Clear app data**: Settings → Apps → EduReach → Clear Data
2. **Reinstall app**: Uninstall and reinstall
3. **Check Firebase Rules**: Ensure read permissions are set correctly
4. **Check internet connection**: Ensure device has internet access
5. **Check Firebase quota**: Ensure you haven't exceeded free tier limits

## Firebase Security Rules

Ensure your Firestore rules allow reading:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /classes/{classId}/subjects/{subjectId}/units/{unitId}/notes/{noteId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

---

**Next Steps**: 
1. Rebuild the app
2. Navigate to Notes → English
3. Check Logcat output
4. Share the log messages to identify the exact issue
