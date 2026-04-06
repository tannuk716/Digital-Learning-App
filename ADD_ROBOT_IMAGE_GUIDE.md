# How to Add Robot Image to AI Tutor Card

## Current Status
The layout is configured to use `@drawable/ai_img` which should display in the AI Tutor card.

## If Image Still Not Showing

### Option 1: Use Your Custom Robot Image

1. **Save your robot image file**:
   - Right-click on your image
   - Save as: `ai_img.png`
   - Make sure it's PNG format

2. **Replace the existing file**:
   - Navigate to: `app/src/main/res/drawable/`
   - Replace the existing `ai_img.png` with your new robot image
   - Make sure the filename is exactly: `ai_img.png` (lowercase)

3. **Rebuild the project**:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

4. **Restart the app** on your emulator/device

### Option 2: Check Image File

Run this command to verify the image exists:
```bash
ls -la app/src/main/res/drawable/ai_img.png
```

### Option 3: Use a Different Image Format

If PNG isn't working, try:
1. Convert your image to PNG format using an online converter
2. Make sure the image size is reasonable (recommended: 512x512 pixels or smaller)
3. Make sure the image has a transparent background for best results

### Troubleshooting

If the image still doesn't show:

1. **Check Android Studio Build Output**:
   - Look for any errors related to drawable resources
   - Check if the image file is being included in the build

2. **Invalidate Caches**:
   - File → Invalidate Caches / Restart
   - Choose "Invalidate and Restart"

3. **Check Image File Size**:
   - Very large images might not load properly
   - Recommended max size: 1MB
   - Recommended dimensions: 512x512 pixels

4. **Verify Image Format**:
   - Must be PNG, JPG, or WebP
   - PNG with transparency works best

## Current Layout Configuration

The AI Tutor card now uses a fixed size ImageView (60dp x 60dp) which should display the image properly.

```xml
<ImageView
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:layout_marginTop="4dp"
    android:layout_marginBottom="4dp"
    android:scaleType="fitCenter"
    android:src="@drawable/ai_img"
    android:contentDescription="AI Tutor"/>
```

## After Making Changes

Always remember to:
1. Clean Project
2. Rebuild Project
3. Restart the app
4. If still not working, uninstall the app and reinstall it
