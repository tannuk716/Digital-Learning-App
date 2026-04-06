# Content Flow Restructure - Implementation Plan

## Current vs Target Structure

### CURRENT (Problematic):
```
Subject Cards → SubjectContentActivity → Shows mixed content (Notion + Teacher uploads)
Notes Section → SubjectNotesActivity → Shows mixed content (Notion + Teacher uploads)
```

### TARGET (Clean Separation):
```
Subject Cards → Direct Notion link (from JSON)
Notes Section → Only teacher-uploaded content
```

## Implementation Steps

### 1. Modify Subject Card Click Behavior
**File:** `StudentDashboardActivity.kt`
**Change:** Subject cards open Notion links directly from JSON

### 2. Create Notion Link Loader
**File:** `utils/NotionLinkLoader.kt` (new)
**Purpose:** Load Notion links from JSON assets

### 3. Modify Notes Section
**File:** `NoteListActivity.kt`
**Change:** Only show teacher-uploaded content (exclude Notion content)

### 4. Remove Upload Button
**File:** `activity_teacher_dashboard.xml`
**Change:** Remove btnUploadNotion

### 5. Fix YouTube Links
**File:** Multiple activities
**Change:** Proper Intent.ACTION_VIEW handling

### 6. Add Download Support
**Files:** SubjectContentActivity, NoteListActivity
**Change:** Add download functionality

## Key Changes Summary

✅ Subject Cards → Notion links (JSON)
✅ Notes Section → Teacher uploads only
✅ Remove upload button
✅ Fix YouTube links
✅ Add download support
✅ Class-based filtering (already done)
✅ Real-time sync (already done)
