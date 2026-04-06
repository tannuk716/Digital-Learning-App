# Test Guide - Recently Added Class Filter Fix

## Quick Test (5 minutes)

### Test 1: Class 1 Student
1. Login as student: `test@test.com` / `test123`
2. Verify profile shows "Class 1"
3. Scroll to "Recently Added (Last 24h)" section
4. **Expected:** All items show "Class 1 | {subject} | {unit}"
5. **Expected:** NO items from Class 2, 3, 4, etc.

### Test 2: Change to Class 10
1. Click profile icon
2. Click "Edit Profile"
3. Change class to "Class 10"
4. Click "Save"
5. Return to dashboard
6. Check "Recently Added" section
7. **Expected:** All items now show "Class 10 | {subject} | {unit}"
8. **Expected:** Class 1 content is GONE

### Test 3: Real-Time Update
1. Keep student dashboard open (Class 1)
2. Login as teacher in another device/browser
3. Upload new content for Class 1
4. **Expected:** Student dashboard updates automatically
5. **Expected:** New content appears at top of "Recently Added"

### Test 4: Cross-Class Isolation
1. Student A (Class 1) on dashboard
2. Teacher uploads content for Class 2
3. **Expected:** Student A does NOT see Class 2 content
4. **Expected:** Only Class 1 content visible

## Detailed Test Plan

### Prerequisites
- App rebuilt and installed
- Firebase has content for multiple classes
- At least one student account
- At least one teacher account

### Test Case 1: Class 1 Content Filtering

**Objective:** Verify Class 1 student only sees Class 1 content

**Steps:**
1. Login as student: `test@test.com` / `test123`
2. Verify dashboard shows "Class 1"
3. Scroll to "Recently Added (Last 24h)" section
4. Check each item in the list

**Expected Results:**
- ✅ All items show "Class 1" in path
- ✅ Format: "Class 1 | {subject} | {unit}"
- ✅ No items from other classes
- ✅ Content sorted by latest timestamp
- ✅ Only content from last 24 hours

**Pass Criteria:**
- All items have classId = "class_1"
- No cross-class content visible

---

### Test Case 2: Class 10 Content Filtering

**Objective:** Verify Class 10 student only sees Class 10 content

**Steps:**
1. Login as student
2. Click profile icon
3. Click "Edit Profile"
4. Change "Class" to "Class 10"
5. Click "Save"
6. Return to dashboard
7. Scroll to "Recently Added" section
8. Check each item in the list

**Expected Results:**
- ✅ All items show "Class 10" in path
- ✅ Format: "Class 10 | {subject} | {unit}"
- ✅ Class 1 content is GONE
- ✅ Only Class 10 content visible
- ✅ Content sorted by latest timestamp

**Pass Criteria:**
- All items have classId = "class_10"
- No Class 1 content visible
- Dashboard updated automatically

---

### Test Case 3: Real-Time Updates

**Objective:** Verify content updates instantly when teacher uploads

**Setup:**
- Device A: Student (Class 1) on dashboard
- Device B: Teacher logged in

**Steps:**
1. Device A: Student opens dashboard (Class 1)
2. Device A: Note current content in "Recently Added"
3. Device B: Teacher uploads new content for Class 1
   - Example: "Hindi - Complete Notes"
   - Class: Class 1
   - Subject: Hindi
   - Unit: Unit 1
4. Device A: Watch "Recently Added" section

**Expected Results:**
- ✅ New content appears automatically (no refresh needed)
- ✅ New item appears at top of list
- ✅ Shows: "Note: Hindi - Complete Notes"
- ✅ Shows: "Class 1 | Hindi | Unit 1"
- ✅ Shows current timestamp
- ✅ No app restart needed

**Pass Criteria:**
- Content appears within 1-2 seconds
- No manual refresh required
- Correct class, subject, unit displayed

---

### Test Case 4: Cross-Class Isolation

**Objective:** Verify students don't see content from other classes

**Setup:**
- Device A: Student (Class 1) on dashboard
- Device B: Teacher logged in

**Steps:**
1. Device A: Student opens dashboard (Class 1)
2. Device A: Note current content count
3. Device B: Teacher uploads content for Class 2
   - Example: "Science - Complete Notes"
   - Class: Class 2
   - Subject: Science
4. Device A: Watch "Recently Added" section

**Expected Results:**
- ✅ Student dashboard does NOT update
- ✅ Class 2 content does NOT appear
- ✅ Only Class 1 content visible
- ✅ Content count unchanged

**Pass Criteria:**
- No Class 2 content visible to Class 1 student
- Complete isolation between classes

---

### Test Case 5: Multiple Class Changes

**Objective:** Verify content updates correctly when changing classes multiple times

**Steps:**
1. Login as student (Class 1)
2. Note content in "Recently Added"
3. Change to Class 5
4. Note content changed to Class 5
5. Change to Class 10
6. Note content changed to Class 10
7. Change back to Class 1
8. Note content changed back to Class 1

**Expected Results:**
- ✅ Each class change shows correct content
- ✅ No mixed content from multiple classes
- ✅ Content updates automatically each time
- ✅ No stale data from previous class

**Pass Criteria:**
- Content always matches current class
- No caching issues
- Clean transitions between classes

---

### Test Case 6: Empty State

**Objective:** Verify empty state when no recent content

**Setup:**
- Student in a class with no content uploaded in last 24 hours

**Steps:**
1. Login as student (Class 5)
2. Scroll to "Recently Added" section

**Expected Results:**
- ✅ Empty state message shows
- ✅ RecyclerView is hidden
- ✅ Message: "No recent uploads" or similar
- ✅ No content from other classes visible

**Pass Criteria:**
- Empty state displays correctly
- No cross-class content shown

---

### Test Case 7: 24-Hour Filter

**Objective:** Verify only content from last 24 hours is shown

**Setup:**
- Content uploaded more than 24 hours ago
- Content uploaded within last 24 hours

**Steps:**
1. Login as student (Class 1)
2. Check "Recently Added" section
3. Verify timestamps of all items

**Expected Results:**
- ✅ Only content from last 24 hours visible
- ✅ Older content not shown
- ✅ All timestamps within 24 hours

**Pass Criteria:**
- All items have timestamp > (now - 24 hours)
- No old content visible

---

### Test Case 8: Content Click

**Objective:** Verify clicking content opens correctly

**Steps:**
1. Login as student (Class 1)
2. Scroll to "Recently Added"
3. Click any content item

**Expected Results:**
- ✅ Content opens in EducationalWebActivity
- ✅ Correct URL loaded
- ✅ Title displayed correctly
- ✅ WebView loads Notion page

**Pass Criteria:**
- Content opens without errors
- Correct URL loaded
- No crashes

---

## Test Results Template

```
Date: ___________
Tester: ___________
Device: ___________

Test Case 1 (Class 1 Filtering): ☐ Pass ☐ Fail
Notes: ___________

Test Case 2 (Class 10 Filtering): ☐ Pass ☐ Fail
Notes: ___________

Test Case 3 (Real-Time Updates): ☐ Pass ☐ Fail
Notes: ___________

Test Case 4 (Cross-Class Isolation): ☐ Pass ☐ Fail
Notes: ___________

Test Case 5 (Multiple Class Changes): ☐ Pass ☐ Fail
Notes: ___________

Test Case 6 (Empty State): ☐ Pass ☐ Fail
Notes: ___________

Test Case 7 (24-Hour Filter): ☐ Pass ☐ Fail
Notes: ___________

Test Case 8 (Content Click): ☐ Pass ☐ Fail
Notes: ___________

Overall Result: ☐ Pass ☐ Fail

Issues Found:
1. ___________
2. ___________
3. ___________
```

## Common Issues & Solutions

### Issue: Content from other classes still showing

**Possible Causes:**
- App not rebuilt after fix
- Firebase cache

**Solution:**
1. Rebuild app: `./gradlew clean build`
2. Uninstall old app
3. Install new app
4. Clear app data if needed

### Issue: Content not updating in real-time

**Possible Causes:**
- No internet connection
- Firebase listener not attached

**Solution:**
1. Check internet connection
2. Check logcat for errors
3. Restart app

### Issue: Empty state not showing

**Possible Causes:**
- Content exists but older than 24 hours
- Wrong class selected

**Solution:**
1. Verify student's class in profile
2. Check Firebase for content timestamps
3. Upload new content to test

### Issue: Content not changing when class changes

**Possible Causes:**
- Profile not saved correctly
- App not detecting class change

**Solution:**
1. Verify profile saved in Firebase
2. Check logcat for class change detection
3. Restart app

## Success Criteria

All tests pass if:

✅ Class 1 student sees ONLY Class 1 content
✅ Class 10 student sees ONLY Class 10 content
✅ Content updates in real-time when teacher uploads
✅ No cross-class data leakage
✅ Content changes automatically when student changes class
✅ Empty state shows when no recent content
✅ Only content from last 24 hours visible
✅ Clicking content opens correctly

---

**Ready to test? Start with Test Case 1!**
