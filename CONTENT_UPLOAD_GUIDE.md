# Content Upload Guide - From Notion to Firebase

## Overview
This guide will help you upload all educational content from your Notion page to Firebase, organized by Class → Subject → Unit.

## Step 1: Export Content from Notion

Since I cannot access your Notion page directly, you need to:

1. **Open your Notion page**: https://www.notion.so/class-1-english-hindi-maths-32ec1523f9cf808db513da0c446ecdd4

2. **Copy the content** and organize it in the JSON format below

3. **Create the JSON file**: `app/src/main/assets/content_data.json`

## Step 2: JSON Format

Create a file `app/src/main/assets/content_data.json` with this structure:

```json
{
  "content": [
    {
      "classId": "class_1",
      "className": "Class 1",
      "subjects": [
        {
          "subjectId": "english",
          "subjectName": "English",
          "units": [
            {
              "unitId": "unit_1",
              "unitName": "The Alphabet",
              "videos": [
                {
                  "title": "ABC Song",
                  "description": "Learn the alphabet",
                  "url": "https://www.youtube.com/watch?v=EXAMPLE",
                  "isYoutube": true
                }
              ],
              "notes": [
                {
                  "title": "Alphabet Chart",
                  "description": "A-Z with pictures",
                  "url": "https://drive.google.com/file/d/EXAMPLE"
                }
              ],
              "quizzes": [
                {
                  "title": "Alphabet Quiz",
                  "questions": [
                    {
                      "text": "What comes after A?",
                      "options": ["B", "C", "D", "E"],
                      "correctIndex": 0,
                      "explanation": "B comes after A"
                    }
                  ]
                }
              ]
            }
          ]
        },
        {
          "subjectId": "hindi",
          "subjectName": "Hindi",
          "units": [
            {
              "unitId": "unit_1",
              "unitName": "वर्णमाला",
              "videos": [],
              "notes": [],
              "quizzes": []
            }
          ]
        },
        {
          "subjectId": "maths",
          "subjectName": "Maths",
          "units": [
            {
              "unitId": "unit_1",
              "unitName": "Numbers 1-10",
              "videos": [],
              "notes": [],
              "quizzes": []
            }
          ]
        },
        {
          "subjectId": "science",
          "subjectName": "Science",
          "units": [
            {
              "unitId": "unit_1",
              "unitName": "My Body",
              "videos": [],
              "notes": [],
              "quizzes": []
            }
          ]
        }
      ]
    },
    {
      "classId": "class_2",
      "className": "Class 2",
      "subjects": []
    }
  ]
}
```

## Step 3: Add Upload Activity to Manifest

Add this to `app/src/main/AndroidManifest.xml`:

```xml
<activity
    android:name=".content_uploader.ContentUploadActivity"
    android:exported="false"
    android:label="Content Upload Tool" />
```

## Step 4: Move Files to Correct Location

Move the created files to the correct locations:

```bash
# Move Kotlin files
mv content_uploader/ContentUploadHelper.kt app/src/main/java/com/tannu/edureach/content_uploader/
mv content_uploader/ContentUploadActivity.kt app/src/main/java/com/tannu/edureach/content_uploader/

# Move layout file
mv content_uploader/activity_content_upload.xml app/src/main/res/layout/

# Move template
mv content_uploader/content_data_template.json app/src/main/assets/
```

## Step 5: Create Assets Folder

```bash
mkdir -p app/src/main/assets
```

## Step 6: Add Launch Button (Optional)

Add a button in TeacherDashboardActivity to launch the upload tool:

```kotlin
findViewById<Button>(R.id.btnContentUpload).setOnClickListener {
    startActivity(Intent(this, ContentUploadActivity::class.java))
}
```

## Step 7: Upload Content

1. **Build and install the app**:
   ```bash
   ./gradlew assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Open the Content Upload Tool** in the app

3. **Click "Upload Content from JSON"**

4. **Wait for completion** - You'll see:
   - Total items
   - Success count
   - Failed count

5. **Click "Remove Duplicate Content"** to clean up any duplicates

## Content Organization Structure

```
Firebase Structure:
classes/
  ├── class_1/
  │   ├── subjects/
  │   │   ├── english/
  │   │   │   ├── units/
  │   │   │   │   ├── unit_1/
  │   │   │   │   │   ├── videos/
  │   │   │   │   │   ├── notes/
  │   │   │   │   │   └── quizzes/
  │   │   │   │   ├── unit_2/
  │   │   │   │   └── ...
  │   │   ├── hindi/
  │   │   ├── maths/
  │   │   └── science/
  ├── class_2/
  └── ...
```

## Example: Class 1 English Content

```json
{
  "classId": "class_1",
  "subjects": [
    {
      "subjectId": "english",
      "units": [
        {
          "unitId": "unit_1",
          "unitName": "The Alphabet (A-Z)",
          "videos": [
            {
              "title": "ABC Song for Kids",
              "description": "Learn alphabet with fun animation",
              "url": "https://www.youtube.com/watch?v=abc123",
              "isYoutube": true
            }
          ],
          "notes": [
            {
              "title": "Alphabet Worksheet",
              "description": "Practice writing A-Z",
              "url": "https://drive.google.com/file/d/xyz789"
            }
          ],
          "quizzes": [
            {
              "title": "Alphabet Recognition Quiz",
              "questions": [
                {
                  "text": "Which letter comes after B?",
                  "options": ["A", "C", "D", "E"],
                  "correctIndex": 1,
                  "explanation": "C comes after B in the alphabet"
                },
                {
                  "text": "What is the first letter of the alphabet?",
                  "options": ["B", "A", "C", "D"],
                  "correctIndex": 1,
                  "explanation": "A is the first letter"
                }
              ]
            }
          ]
        },
        {
          "unitId": "unit_2",
          "unitName": "Phonics Basics",
          "videos": [],
          "notes": [],
          "quizzes": []
        }
      ]
    }
  ]
}
```

## Tips for Organizing Content

### Class IDs
- `class_1` for Class 1
- `class_2` for Class 2
- etc.

### Subject IDs
- `english` for English
- `hindi` for Hindi
- `maths` for Maths
- `science` for Science

### Unit IDs
- `unit_1`, `unit_2`, `unit_3`, etc.
- Up to 6 units per subject

### Video URLs
- **YouTube**: Use full URL like `https://www.youtube.com/watch?v=VIDEO_ID`
- **Google Drive**: Use shareable link
- Set `isYoutube: true` for YouTube videos

### Note URLs
- **Google Drive**: Share the file and use the link
- **Google Docs**: Use shareable link
- **PDFs**: Upload to Drive and share

## Duplicate Removal

The system automatically removes duplicates based on:
- **Videos**: Same title + same URL
- **Notes**: Same title + same URL
- **Quizzes**: Same title

Click "Remove Duplicate Content" after uploading to clean up.

## Troubleshooting

### "File not found: content_data.json"
- Make sure the file is in `app/src/main/assets/`
- Rebuild the app

### "Upload failed"
- Check internet connection
- Verify Firebase is configured
- Check logcat for specific errors:
  ```bash
  adb logcat -s ContentUpload:E
  ```

### "Some items failed"
- Check the status text for details
- Common issues:
  - Invalid URLs
  - Missing required fields
  - Network timeout

### Verify Upload
Check Firebase Console:
1. Go to Firestore
2. Navigate to `classes` → `class_1` → `subjects` → `english` → `units` → `unit_1`
3. You should see `videos`, `notes`, and `quizzes` collections

## Monitoring Upload Progress

Watch logs in real-time:
```bash
adb logcat -s ContentUpload:D ContentUploadHelper:D
```

You'll see:
- Items being uploaded
- Success/failure for each item
- Final summary

## After Upload

1. **Test in the app**:
   - Open Student Dashboard
   - Click on a subject
   - Verify content appears

2. **Remove duplicates**:
   - Click "Remove Duplicate Content"
   - Wait for completion

3. **Verify in Firebase Console**:
   - Check all classes have content
   - Verify structure is correct

## Need Help?

If you need help organizing your Notion content into JSON format:
1. Share the structure of your Notion page
2. I can create a specific JSON template for your content
3. Or create a script to help convert it

## Quick Start Checklist

- [ ] Create `app/src/main/assets/` folder
- [ ] Create `content_data.json` with your content
- [ ] Move Kotlin files to correct package
- [ ] Move layout file to res/layout
- [ ] Add activity to AndroidManifest.xml
- [ ] Build and install app
- [ ] Open Content Upload Tool
- [ ] Click "Upload Content"
- [ ] Click "Remove Duplicates"
- [ ] Verify in Firebase Console
- [ ] Test in Student Dashboard

Your content will be perfectly organized and ready for students to access!
