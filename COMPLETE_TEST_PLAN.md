# Complete Test Plan - Notion Content Upload & Redirection

## Pre-Test Setup

### 1. Rebuild the App
```bash
./gradlew clean build
```

### 2. Install on Device/Emulator
```bash
./gradlew installDebug
```

---

## Test Phase 1: Upload Notion Content (Teacher)

### Test 1.1: Login as Teacher
- [ ] Open app
- [ ] Enter email: `teacher@test.com`
- [ ] Enter password: `test123`
- [ ] Click "Login"
- [ ] **Expected:** Teacher dashboard opens

### Test 1.2: Verify Upload Button Exists
- [ ] On teacher dashboard, scroll down
- [ ] **Expected:** See button "📓 Upload Notion Content (One Time)"
- [ ] Button has orange background
- [ ] Button is clickable

### Test 1.3: Open Upload Activity
- [ ] Click "📓 Upload Notion Content (One Time)" button
- [ ] **Expected:** UploadNotionContentActivity opens
- [ ] See two buttons:
  - "Delete Old Content"
  - "Upload Content"
- [ ] See progress bar (hidden)
- [ ] See status text

### Test 1.4: Delete Old Content (Optional)
- [ ] Click "Delete Old Content" button
- [ ] **Expected:** 
  - Progress bar appears
  - Status text shows "Deleting ALL old content..."
  - After completion: "Deleted X items!"
  - Toast message: "Deleted X items successfully"
  - Buttons re-enabled

### Test 1.5: Upload Notion Content
- [ ] Click "Upload Content" button
- [ ] **Expected:**
  - Progress bar appears
  - Status text shows "Uploading Notion content..."
  - Status updates: "Uploaded 1 notes...", "Uploaded 2 notes...", etc.
  - After completion: "Upload complete! Uploaded 50 notes"
  - Toast message: "Success! Uploaded 50 notes"
  - Buttons re-enabled

### Test 1.6: Verify Upload in Firebase Console
- [ ] Open Firebase Console
- [ ] Go to Firestore Database
- [ ] Navigate to: `classes` → `class_1` → `subjects` → `maths` → `units` → `unit_1` → `notes`
- [ ] **Expected:** See document with:
  - `title`: "Maths - Complete Notes"
  - `description`: "Complete study material for Maths"
  - `fileUrl`: "https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47?pvs=21"
  - `timestamp`: (recent timestamp)

### Test 1.7: Verify Content in Teacher Dashboard
- [ ] Go back to app
- [ ] Press back button to return to teacher dashboard
- [ ] Scroll to "Your Uploaded Content (Class-wise)"
- [ ] **Expected:** See 50 notes organized by class
- [ ] Each item shows:
  - Type: "Note"
  - Title: "{Subject} - Complete Notes"
  - Location: "Class X / {subject} / Unit 1"
  - Description: "Complete study material..."
  - "📖 Open" button
  - "🗑️ Delete" button

### Test 1.8: Open Content from Teacher Dashboard
- [ ] Find "Maths - Complete Notes" for Class 1
- [ ] Click "📖 Open" button
- [ ] **Expected:**
  - EducationalWebActivity opens
  - Title shows "Maths - Complete Notes"
  - WebView loads Notion page
  - URL: https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47
  - Notion content displays correctly

---

## Test Phase 2: Access Content as Student (Class 1)

### Test 2.1: Login as Student
- [ ] Logout from teacher account
- [ ] Enter email: `test@test.com`
- [ ] Enter password: `test123`
- [ ] Click "Login"
- [ ] **Expected:** Student dashboard opens

### Test 2.2: Verify Student Profile
- [ ] Click profile icon (avatar)
- [ ] **Expected:** Profile shows:
  - Name: "Student" (or custom name)
  - Class: "Class 1"
- [ ] If class is wrong:
  - Click "Edit Profile"
  - Change class to "Class 1"
  - Click "Save"
  - Return to dashboard

### Test 2.3: Access via Subject Card (Maths)
- [ ] On student dashboard, click "Maths" card
- [ ] **Expected:** SubjectContentActivity opens
- [ ] Title shows "Maths Content"
- [ ] See "Unit 1" header
- [ ] See "Maths - Complete Notes" item
- [ ] Item shows:
  - Icon: 📄
  - Title: "Maths - Complete Notes"
  - Description: "Complete study material for Maths"

### Test 2.4: Open Maths Note
- [ ] Click "Maths - Complete Notes"
- [ ] **Expected:**
  - EducationalWebActivity opens
  - Title shows "Maths - Complete Notes"
  - WebView loads Notion page
  - URL: https://www.notion.so/MATHS-32ec1523f9cf804d9e0ffd34d0d19e47
  - Notion content displays correctly
  - Can scroll and interact with Notion page

### Test 2.5: Access via Subject Card (English)
- [ ] Go back to dashboard
- [ ] Click "English" card
- [ ] **Expected:** SubjectContentActivity opens
- [ ] See "Unit 1" header
- [ ] See "English - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens Notion page: https://www.notion.so/english-32ec1523f9cf803d8eaaf92fb2c58e0c

### Test 2.6: Access via Subject Card (Hindi)
- [ ] Go back to dashboard
- [ ] Click "Hindi" card
- [ ] **Expected:** SubjectContentActivity opens
- [ ] See "Unit 1" header
- [ ] See "Hindi - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens Notion page: https://www.notion.so/hindi-32ec1523f9cf8085a8b6d0ec41abf85e

### Test 2.7: Access via Notes Section (Maths)
- [ ] Go back to dashboard
- [ ] Click "Notes" card
- [ ] **Expected:** SubjectNotesActivity opens
- [ ] See 6 subject cards: Maths, English, Hindi, Science, EVS, SST
- [ ] Click "Maths" card
- [ ] **Expected:** NoteListActivity opens
- [ ] Title shows "Maths Notes"
- [ ] See "Maths - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens same Notion page as before

### Test 2.8: Access via Notes Section (English)
- [ ] Go back to SubjectNotesActivity
- [ ] Click "English" card
- [ ] **Expected:** NoteListActivity opens
- [ ] See "English - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens Notion page correctly

### Test 2.9: Check Recently Added Section
- [ ] Go back to dashboard
- [ ] Scroll to "Recently Added" section
- [ ] **Expected:** 
  - If uploaded within last 24 hours: See some notes
  - Each item shows title and subject
  - Click any note → Opens correct Notion page
- [ ] If empty: Normal (content older than 24 hours)

---

## Test Phase 3: Access Content as Student (Class 10)

### Test 3.1: Change Student Class
- [ ] Click profile icon
- [ ] Click "Edit Profile"
- [ ] Change class to "Class 10"
- [ ] Click "Save"
- [ ] Return to dashboard
- [ ] **Expected:** Dashboard shows "Class 10"

### Test 3.2: Access Science Content
- [ ] Click "Science" card
- [ ] **Expected:** SubjectContentActivity opens
- [ ] See "Unit 1" header
- [ ] See "Science - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens Notion page: https://www.notion.so/science-32fc1523f9cf801cbfe7c96eeb79ab12

### Test 3.3: Access SST Content
- [ ] Go back to dashboard
- [ ] Click "Notes" card
- [ ] Click "SST" card
- [ ] **Expected:** NoteListActivity opens
- [ ] See "Sst - Complete Notes"
- [ ] Click note
- [ ] **Expected:** Opens Notion page: https://www.notion.so/sst-32fc1523f9cf80ab8018e1b2c13ec8c5

### Test 3.4: Verify Other Subjects
- [ ] Test Maths, English, Hindi for Class 10
- [ ] **Expected:** All open correct Notion pages

---

## Test Phase 4: Teacher Content Management

### Test 4.1: Login as Teacher Again
- [ ] Logout from student account
- [ ] Login as teacher: `teacher@test.com` / `test123`
- [ ] **Expected:** Teacher dashboard opens

### Test 4.2: View All Content
- [ ] Scroll to "Your Uploaded Content (Class-wise)"
- [ ] **Expected:** See all 50 notes
- [ ] Content organized by:
  - Class 1: 3 notes (English, Maths, Hindi)
  - Class 2: 4 notes (Maths, English, EVS, Hindi)
  - Class 3-5: 4 notes each
  - Class 6-7: 5 notes each
  - Class 8-10: 5 notes each

### Test 4.3: Open Random Content
- [ ] Pick any note from any class
- [ ] Click "📖 Open" button
- [ ] **Expected:** Opens correct Notion page
- [ ] Go back and try 2-3 more random notes
- [ ] **Expected:** All open correct pages

### Test 4.4: Delete Content
- [ ] Find "English - Complete Notes" for Class 1
- [ ] Click "🗑️ Delete" button
- [ ] **Expected:** Confirmation dialog appears
- [ ] Message: "Delete 'English - Complete Notes'? This will remove it from all students in Class 1."
- [ ] Click "Cancel"
- [ ] **Expected:** Dialog closes, content still there

### Test 4.5: Confirm Delete
- [ ] Click "🗑️ Delete" button again
- [ ] Click "Delete" in dialog
- [ ] **Expected:**
  - Toast: "Content deleted"
  - Content disappears from list
  - List refreshes automatically

### Test 4.6: Verify Delete Reflected for Students
- [ ] Logout from teacher account
- [ ] Login as student (Class 1)
- [ ] Click "English" card
- [ ] **Expected:** 
  - "English - Complete Notes" is GONE
  - Empty state shows OR no content in Unit 1
- [ ] This confirms deletion works across all students

---

## Test Phase 5: Edge Cases & Error Handling

### Test 5.1: No Internet Connection
- [ ] Turn off WiFi/Data
- [ ] Try to open any Notion link
- [ ] **Expected:** 
  - WebView shows "No internet connection" error
  - Or blank page with error message

### Test 5.2: Invalid Notion URL
- [ ] (This requires manually editing Firebase)
- [ ] Change a fileUrl to invalid URL
- [ ] Try to open that note
- [ ] **Expected:** WebView shows error or blank page

### Test 5.3: Empty Content
- [ ] Delete all content from a subject
- [ ] As student, click that subject card
- [ ] **Expected:** Empty state message shows
- [ ] "No content available yet"

### Test 5.4: Duplicate Upload Prevention
- [ ] As teacher, go to UploadNotionContentActivity
- [ ] Click "Upload Content" again (without deleting)
- [ ] **Expected:** 
  - Upload completes quickly
  - No duplicates created (duplicate detection works)
  - Firebase still has only 50 notes (not 100)

---

## Test Phase 6: Cross-Class Verification

### Test 6.1: Class 2 Content
- [ ] Login as student
- [ ] Set class to "Class 2"
- [ ] Verify subjects: Maths, English, EVS, Hindi
- [ ] Open each subject
- [ ] **Expected:** All 4 Notion links work

### Test 6.2: Class 5 Content
- [ ] Change class to "Class 5"
- [ ] Verify subjects: English, Hindi, Maths, EVS
- [ ] Open each subject
- [ ] **Expected:** All 4 Notion links work

### Test 6.3: Class 7 Content
- [ ] Change class to "Class 7"
- [ ] Verify subjects: English, Hindi, EVS, Maths, SST
- [ ] Open each subject
- [ ] **Expected:** All 5 Notion links work

---

## Success Criteria

### All Tests Pass If:
✅ Upload completes with "Success! Uploaded 50 notes"
✅ Firebase shows 50 documents across all classes
✅ Teacher can view all 50 notes in dashboard
✅ Teacher can open any note → Correct Notion page loads
✅ Teacher can delete notes → Deletion reflects for students
✅ Student (Class 1) can access 3 subjects via subject cards
✅ Student (Class 1) can access 3 subjects via notes section
✅ Student (Class 10) can access 5 subjects
✅ All Notion URLs open correctly in WebView
✅ No duplicate content appears
✅ Empty states show when no content available
✅ Profile changes reflect immediately in dashboard

---

## Common Issues & Solutions

### Issue: "Success! Uploaded 50 notes" but content not showing

**Solution:**
1. Check Firebase Console - verify documents exist
2. Check student class matches uploaded content class
3. Rebuild app and try again

### Issue: Clicking note does nothing

**Solution:**
1. Check logcat for errors
2. Verify fileUrl field in Firebase is not empty
3. Verify internet connection

### Issue: Wrong Notion page opens

**Solution:**
1. Check Firebase Console - verify fileUrl is correct
2. If wrong, delete and re-upload content

### Issue: Duplicate content showing

**Solution:**
1. Click "Delete Old Content" in UploadNotionContentActivity
2. Then click "Upload Content"

---

## Test Summary Report Template

```
Date: ___________
Tester: ___________
Device: ___________
Android Version: ___________

Phase 1 (Upload): ☐ Pass ☐ Fail
Phase 2 (Student Class 1): ☐ Pass ☐ Fail
Phase 3 (Student Class 10): ☐ Pass ☐ Fail
Phase 4 (Teacher Management): ☐ Pass ☐ Fail
Phase 5 (Edge Cases): ☐ Pass ☐ Fail
Phase 6 (Cross-Class): ☐ Pass ☐ Fail

Issues Found:
1. ___________
2. ___________
3. ___________

Overall Result: ☐ Pass ☐ Fail

Notes:
___________
___________
```

---

**Ready to test? Start with Phase 1!**
