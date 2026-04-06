# Notion Content Flow Diagram

## Current State (Before Upload)

```
┌─────────────────────────────────────────────────────────────┐
│  UploadNotionContentActivity.kt                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  private val notionContent = mapOf(                   │  │
│  │    "class_1" to mapOf(                                │  │
│  │      "english" to "https://notion.so/english-..."     │  │
│  │      "maths" to "https://notion.so/MATHS-..."         │  │
│  │      "hindi" to "https://notion.so/hindi-..."         │  │
│  │    ),                                                  │  │
│  │    "class_2" to mapOf(...),                           │  │
│  │    ... (50 total links)                               │  │
│  │  )                                                     │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Status: HARDCODED IN CODE ❌                                │
│  Location: NOT IN FIREBASE ❌                                │
│  Accessible to Students: NO ❌                               │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ USER NEEDS TO CLICK
                            │ "Upload Content" BUTTON
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  Firebase Firestore Database                                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  classes/                                             │  │
│  │    ├── class_1/                                       │  │
│  │    │   └── subjects/                                  │  │
│  │    │       ├── english/                               │  │
│  │    │       │   └── units/                             │  │
│  │    │       │       └── unit_1/                        │  │
│  │    │       │           └── notes/                     │  │
│  │    │       │               └── {id}/                  │  │
│  │    │       │                   ├── title              │  │
│  │    │       │                   ├── fileUrl ← EMPTY ❌ │  │
│  │    │       │                   └── timestamp          │  │
│  │    │       ├── maths/                                 │  │
│  │    │       │   └── ... (EMPTY) ❌                     │  │
│  │    │       └── hindi/                                 │  │
│  │    │           └── ... (EMPTY) ❌                     │  │
│  │    └── class_2/ ... class_10/ (ALL EMPTY) ❌          │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Status: EMPTY DATABASE ❌                                   │
│  Students Try to Access: SEE NOTHING ❌                      │
└─────────────────────────────────────────────────────────────┘
```

## After Upload (What Happens)

```
┌─────────────────────────────────────────────────────────────┐
│  STEP 1: Teacher Clicks "Upload Content" Button             │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  UploadNotionContentActivity.uploadAllContent()             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  for ((classId, subjects) in notionContent) {         │  │
│  │    for ((subjectId, notionUrl) in subjects) {         │  │
│  │      val note = NoteContent(                          │  │
│  │        title = "Maths - Complete Notes",              │  │
│  │        fileUrl = notionUrl,  ← NOTION URL             │  │
│  │        timestamp = now                                │  │
│  │      )                                                 │  │
│  │      db.collection("classes").document(classId)       │  │
│  │        .collection("subjects").document(subjectId)    │  │
│  │        .collection("units").document("unit_1")        │  │
│  │        .collection("notes").add(note)                 │  │
│  │    }                                                   │  │
│  │  }                                                     │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Result: "Success! Uploaded 50 notes" ✅                     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  Firebase Firestore Database (AFTER UPLOAD)                 │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  classes/                                             │  │
│  │    ├── class_1/                                       │  │
│  │    │   └── subjects/                                  │  │
│  │    │       ├── english/                               │  │
│  │    │       │   └── units/                             │  │
│  │    │       │       └── unit_1/                        │  │
│  │    │       │           └── notes/                     │  │
│  │    │       │               └── abc123/                │  │
│  │    │       │                   ├── title: "English..."│  │
│  │    │       │                   ├── fileUrl: "https:// │  │
│  │    │       │                   │   notion.so/english" │  │
│  │    │       │                   └── timestamp: 1234... │  │
│  │    │       ├── maths/                                 │  │
│  │    │       │   └── units/                             │  │
│  │    │       │       └── unit_1/                        │  │
│  │    │       │           └── notes/                     │  │
│  │    │       │               └── def456/                │  │
│  │    │       │                   ├── title: "Maths..."  │  │
│  │    │       │                   ├── fileUrl: "https:// │  │
│  │    │       │                   │   notion.so/MATHS"   │  │
│  │    │       │                   └── timestamp: 1234... │  │
│  │    │       └── hindi/                                 │  │
│  │    │           └── ... (POPULATED) ✅                 │  │
│  │    ├── class_2/ ... class_10/ (ALL POPULATED) ✅      │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Status: 50 NOTES IN DATABASE ✅                             │
│  Students Can Access: YES ✅                                 │
└─────────────────────────────────────────────────────────────┘
```

## Student Access Flow (After Upload)

```
┌─────────────────────────────────────────────────────────────┐
│  Student Dashboard                                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  [Maths Card] [English Card] [Hindi Card] [Science]  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Student clicks "Maths" card
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  SubjectContentActivity                                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Maths Content                                        │  │
│  │  ─────────────────────────────────────────────────    │  │
│  │  Unit 1                                               │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │ 📄 Maths - Complete Notes                       │  │  │
│  │  │    Complete study material for Maths            │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Loads from Firebase:                                       │
│  classes/class_1/subjects/maths/units/unit_1/notes/         │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Student clicks note
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  NoteListActivity.openNote()                                │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  val intent = Intent(                                 │  │
│  │    this,                                              │  │
│  │    EducationalWebActivity::class.java                 │  │
│  │  )                                                     │  │
│  │  intent.putExtra("WEB_URL", note.fileUrl) ← FIXED ✅  │  │
│  │  intent.putExtra("WEB_TITLE", note.title)            │  │
│  │  startActivity(intent)                                │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  EducationalWebActivity                                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  val url = intent.getStringExtra("WEB_URL")          │  │
│  │  // url = "https://notion.so/MATHS-32ec1523..."      │  │
│  │                                                        │  │
│  │  webView.loadUrl(url)                                 │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │  ┌─────────────────────────────────────────┐    │  │  │
│  │  │  │  Notion Page Loads Successfully ✅       │    │  │  │
│  │  │  │  ─────────────────────────────────────   │    │  │  │
│  │  │  │  # Maths - Complete Notes               │    │  │  │
│  │  │  │                                          │    │  │  │
│  │  │  │  Chapter 1: Numbers                      │    │  │  │
│  │  │  │  Chapter 2: Algebra                      │    │  │  │
│  │  │  │  Chapter 3: Geometry                     │    │  │  │
│  │  │  │  ...                                     │    │  │  │
│  │  │  └─────────────────────────────────────────┘    │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Result: NOTION PAGE OPENS CORRECTLY ✅                      │
└─────────────────────────────────────────────────────────────┘
```

## Teacher Management Flow

```
┌─────────────────────────────────────────────────────────────┐
│  Teacher Dashboard                                          │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Your Uploaded Content (Class-wise)                   │  │
│  │  ─────────────────────────────────────────────────    │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │ Note                                            │  │  │
│  │  │ Maths - Complete Notes                          │  │  │
│  │  │ Class 1 / maths / Unit 1                        │  │  │
│  │  │ Complete study material for Maths               │  │  │
│  │  │ [📖 Open] [🗑️ Delete]                           │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │ Note                                            │  │  │
│  │  │ English - Complete Notes                        │  │  │
│  │  │ Class 1 / english / Unit 1                      │  │  │
│  │  │ [📖 Open] [🗑️ Delete]                           │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  │  ... (48 more notes)                                  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Loads from Firebase: ALL classes, subjects, units          │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Teacher clicks "📖 Open"
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  EducationalWebActivity                                     │
│  Opens Notion page ✅                                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Teacher clicks "🗑️ Delete"
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  Confirmation Dialog                                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Delete "Maths - Complete Notes"?                     │  │
│  │                                                        │  │
│  │  This will remove it from all students in Class 1.    │  │
│  │                                                        │  │
│  │  [Cancel] [Delete]                                    │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ Teacher clicks "Delete"
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  TeacherDashboardActivity.performDelete()                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  db.collection("classes").document(classId)           │  │
│  │    .collection("subjects").document(subjectId)        │  │
│  │    .collection("units").document(unitId)              │  │
│  │    .collection("notes").document(id)                  │  │
│  │    .delete()                                          │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Result: Content deleted from Firebase ✅                    │
│  UI refreshes automatically ✅                               │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│  Effect on Students                                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Student Dashboard (Class 1)                          │  │
│  │  Clicks "Maths" card                                  │  │
│  │  ─────────────────────────────────────────────────    │  │
│  │  Unit 1                                               │  │
│  │  (Empty - note was deleted) ✅                        │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  Deletion reflects immediately for all students ✅           │
└─────────────────────────────────────────────────────────────┘
```

## Bug Fix: Intent Parameter

### Before Fix ❌

```kotlin
// StudentDashboardActivity.kt
private fun openRecentContent(content: RecentUploadModel) {
    val intent = Intent(this, EducationalWebActivity::class.java)
    intent.putExtra("URL", url)  // ❌ WRONG PARAMETER NAME
    startActivity(intent)
}

// EducationalWebActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    val url = intent.getStringExtra("WEB_URL")  // Expects "WEB_URL"
    // url = null ❌ (because we sent "URL" not "WEB_URL")
    // Falls back to default: "https://en.wikipedia.org/wiki/Education"
    webView.loadUrl(url)  // Opens Wikipedia instead of Notion ❌
}
```

### After Fix ✅

```kotlin
// StudentDashboardActivity.kt
private fun openRecentContent(content: RecentUploadModel) {
    val intent = Intent(this, EducationalWebActivity::class.java)
    intent.putExtra("WEB_URL", url)  // ✅ CORRECT PARAMETER NAME
    intent.putExtra("WEB_TITLE", content.title)  // ✅ ADDED TITLE
    startActivity(intent)
}

// EducationalWebActivity.kt
override fun onCreate(savedInstanceState: Bundle?) {
    val url = intent.getStringExtra("WEB_URL")  // Expects "WEB_URL"
    // url = "https://notion.so/MATHS-..." ✅ (correct URL received)
    webView.loadUrl(url)  // Opens Notion page ✅
}
```

## Summary

### Problem:
1. ❌ Notion links hardcoded in code, NOT in Firebase
2. ❌ Wrong intent parameter ("URL" instead of "WEB_URL")

### Solution:
1. ✅ Fixed intent parameter bugs
2. ✅ User needs to click "Upload Content" button (ONE TIME)
3. ✅ After upload, all 50 Notion links work perfectly

### Result:
- ✅ Students can access content by class
- ✅ Content organized by subject and unit
- ✅ Clicking notes opens correct Notion pages
- ✅ Teachers can view and delete content
- ✅ Deletion reflects across all students

---

**Next Step: Run app → Login as teacher → Click "Upload Content" button!**
