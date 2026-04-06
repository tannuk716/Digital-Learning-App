# Class 10 SST Unit 2 & 3 Troubleshooting Guide

## Issue Reported
Class 10 SST Unit 2 and Unit 3 are not opening.

## URLs in System
- **Unit 1**: `https://drive.google.com/uc?export=download&id=1YH8PTk3UkCOeaka8dItfjIUFtV-dKKqC` ✅
- **Unit 2**: `https://drive.google.com/uc?export=download&id=1YqiPeba_vF9dyLDGyvDGrr_FrRHkUTpp` ❓
- **Unit 3**: `https://drive.google.com/uc?export=download&id=13qKBxAfGz3-VxP06G5rVuifZdQBADYfc` ❓

## Fixes Applied

### 1. Added Detailed Logging ✅
- `Class10UnitListActivity.kt` - Logs PDF opening attempts
- `EducationalWebActivity.kt` - Logs URL conversion and loading

### 2. Enhanced Error Handling ✅
- Better error messages showing exact failure reason
- Catches and logs all exceptions
- Shows user-friendly error messages

### 3. Google Drive URL Conversion ✅
- Automatically converts URLs to direct download format
- Handles all Google Drive URL variations

## How to Debug

### Step 1: Check Logcat After Rebuild

After rebuilding, when you try to open Unit 2 or 3, check Android Studio Logcat for:

```
Class10Unit: ========================================
Class10Unit: Opening PDF: Unit 2
Class10Unit: URL: https://drive.google.com/uc?export=download&id=...
Class10Unit: ========================================
Class10Unit: Opening online via EducationalWebActivity
```

Then look for:

```
EducationalWeb: ========================================
EducationalWeb: Loading: Unit 2
EducationalWeb: Original URL: ...
EducationalWeb: Converted URL: ...
EducationalWeb: Using Google Docs viewer: ...
EducationalWeb: Final URL: ...
EducationalWeb: ========================================
```

### Step 2: Test URLs Manually

Test the URLs in your browser:

**Unit 2 Direct Download**:
```
https://drive.google.com/uc?export=download&id=1YqiPeba_vF9dyLDGyvDGrr_FrRHkUTpp
```

**Unit 3 Direct Download**:
```
https://drive.google.com/uc?export=download&id=13qKBxAfGz3-VxP06G5rVuifZdQBADYfc
```

**Unit 2 Google Docs Viewer**:
```
https://docs.google.com/gview?embedded=true&url=https://drive.google.com/uc?export=download&id=1YqiPeba_vF9dyLDGyvDGrr_FrRHkUTpp
```

**Unit 3 Google Docs Viewer**:
```
https://docs.google.com/gview?embedded=true&url=https://drive.google.com/uc?export=download&id=13qKBxAfGz3-VxP06G5rVuifZdQBADYfc
```

### Step 3: Check Google Drive File Permissions

1. Go to Google Drive
2. Find the files with IDs:
   - `1YqiPeba_vF9dyLDGyvDGrr_FrRHkUTpp` (Unit 2)
   - `13qKBxAfGz3-VxP06G5rVuifZdQBADYfc` (Unit 3)
3. Right-click → Share
4. Make sure "Anyone with the link" can view
5. Copy the link and verify it matches

## Possible Issues

### Issue 1: File Permissions
**Symptom**: URL loads but shows "Access Denied" or "Need Permission"
**Solution**: 
1. Open Google Drive
2. Find the file
3. Right-click → Share
4. Change to "Anyone with the link can view"
5. Save

### Issue 2: File Deleted or Moved
**Symptom**: URL shows "File not found" or 404 error
**Solution**:
1. Verify file still exists in Google Drive
2. Get new file ID if file was moved
3. Update Class10ContentProvider.kt with new ID

### Issue 3: Large File Size
**Symptom**: File starts loading but never completes
**Solution**:
1. Check file size in Google Drive
2. If > 100MB, consider compressing the PDF
3. Or split into smaller files

### Issue 4: Network Issues
**Symptom**: "Unable to connect" or timeout errors
**Solution**:
1. Check internet connection
2. Try on different network (WiFi vs Mobile data)
3. Check if Google Drive is accessible

### Issue 5: WebView Issues
**Symptom**: Blank screen or "Page not available"
**Solution**:
1. Clear app data and cache
2. Uninstall and reinstall app
3. Update Android System WebView from Play Store

## Testing Checklist

After rebuilding, test:

- [ ] Class 10 SST Unit 1 opens ✅
- [ ] Class 10 SST Unit 2 opens ❓
- [ ] Class 10 SST Unit 3 opens ❓
- [ ] Download button works for all units
- [ ] Error messages are clear if something fails
- [ ] Check Logcat for detailed error logs

## Quick Fixes

### If Unit 2 & 3 Still Don't Work:

1. **Test URLs in browser first** - If they don't work in browser, the files have permission issues

2. **Check Logcat** - Look for the exact error message

3. **Try Unit 1** - If Unit 1 works but 2 & 3 don't, it's likely a file permission issue

4. **Replace URLs** - If files are inaccessible, upload new files and update the IDs in `Class10ContentProvider.kt`

## Files Modified

1. `app/src/main/java/com/tannu/edureach/Class10UnitListActivity.kt` - Added logging
2. `app/src/main/java/com/tannu/edureach/Class1UnitListActivity.kt` - Added logging
3. `app/src/main/java/com/tannu/edureach/utils/EducationalWebActivity.kt` - Added logging
4. `app/src/main/java/com/tannu/edureach/utils/GoogleDriveUrlHelper.kt` - URL conversion

## Next Steps

1. **Rebuild the app**:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

2. **Test Class 10 SST**:
   - Open Class 10
   - Go to SST subject
   - Try opening Unit 1, 2, and 3

3. **Check Logcat** for detailed error messages

4. **Report back** with:
   - What error message appears (if any)
   - What you see in Logcat
   - Whether Unit 1 works but 2 & 3 don't

## Expected Behavior

When working correctly:
1. Click on Unit 2 or 3
2. See loading indicator
3. PDF opens in Google Docs viewer
4. Can scroll through the PDF
5. Can download if needed

## Contact Information

If issue persists after rebuild, provide:
- Screenshot of error message
- Logcat output (filter by "Class10Unit" and "EducationalWeb")
- Whether other classes/subjects work fine
- Whether Unit 1 works but 2 & 3 don't
