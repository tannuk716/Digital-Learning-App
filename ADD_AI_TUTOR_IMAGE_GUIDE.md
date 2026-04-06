# How to Add AI Tutor Robot Image

## The Image
You have a cute robot image with:
- Blue background
- White robot with blue screen face
- Big friendly eyes
- Antenna on top
- Chat bubble icon
- Perfect for AI Tutor!

## Steps to Add the Image

### Step 1: Save the Image
1. Save the robot image from your message
2. Rename it to: `ai_tutor_icon.png`
3. Recommended: Crop it to square (1:1 ratio) if needed

### Step 2: Add to Android Project

**Option A: Using Android Studio (Recommended)**
1. Open your project in Android Studio
2. In the Project view, navigate to: `app/src/main/res/`
3. Right-click on the `drawable` folder
4. Select `Paste` or `New` → `Image Asset`
5. If using Image Asset:
   - Select "Image" as Asset Type
   - Browse and select your `ai_tutor_icon.png`
   - Name it: `ai_tutor_icon`
   - Click "Next" and "Finish"

**Option B: Manual Copy**
1. Navigate to your project folder on your computer
2. Go to: `YourProject/app/src/main/res/drawable/`
3. Copy your `ai_tutor_icon.png` file into this folder
4. The file should be: `app/src/main/res/drawable/ai_tutor_icon.png`

### Step 3: Verify the Layout

The layout is already configured! Check `activity_student_dashboard.xml`:

```xml
<ImageView
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:layout_weight="1"
    android:src="@drawable/ai_tutor_icon"
    android:scaleType="fitCenter"
    android:padding="8dp"
    android:contentDescription="AI Tutor"/>
```

### Step 4: Rebuild the Project

1. In Android Studio, go to: `Build` → `Clean Project`
2. Then: `Build` → `Rebuild Project`
3. Wait for the build to complete

### Step 5: Run and Test

1. Run the app on your emulator or device
2. Go to Student Dashboard
3. Look at the AI Tutor card
4. You should see your cute robot image!

## Image Specifications

### Recommended Settings:
- **Format**: PNG (supports transparency)
- **Size**: 512x512 pixels (or any square size)
- **File name**: `ai_tutor_icon.png`
- **Location**: `app/src/main/res/drawable/`

### The Image Will:
- Fill the center area of the AI Tutor card
- Scale proportionally (fitCenter)
- Have 8dp padding around it
- Display on a blue gradient background
- Look professional and kid-friendly

## Multiple Resolution Support (Optional)

For best quality across all devices, you can provide multiple resolutions:

```
app/src/main/res/
├── drawable-mdpi/ai_tutor_icon.png (48x48)
├── drawable-hdpi/ai_tutor_icon.png (72x72)
├── drawable-xhdpi/ai_tutor_icon.png (96x96)
├── drawable-xxhdpi/ai_tutor_icon.png (144x144)
└── drawable-xxxhdpi/ai_tutor_icon.png (192x192)
```

Or just use one high-resolution image (512x512 or 1024x1024) in the `drawable/` folder and Android will scale it automatically.

## Troubleshooting

### If the image doesn't appear:
1. **Check file name**: Must be exactly `ai_tutor_icon.png` (lowercase, no spaces)
2. **Check location**: Must be in `app/src/main/res/drawable/`
3. **Rebuild**: Clean and rebuild the project
4. **Sync Gradle**: Click "Sync Project with Gradle Files" button
5. **Check file format**: PNG or JPG only (PNG recommended)

### If the image looks stretched or distorted:
- The `scaleType="fitCenter"` ensures it maintains aspect ratio
- Make sure your image is square (same width and height)
- If not square, crop it to 1:1 ratio before adding

### If you get a "Resource not found" error:
1. Make sure the file name has no capital letters
2. Make sure there are no spaces or special characters
3. Only use lowercase letters, numbers, and underscores
4. Example: `ai_tutor_icon.png` ✅
5. Not: `AI Tutor Icon.png` ❌

## Result

After adding the image, your AI Tutor card will show:
- **Title**: "AI Tutor" (white text)
- **Icon**: Your cute robot image (centered, scaled)
- **Description**: "Ask Questions" (white text)
- **Background**: Beautiful blue gradient
- **Overall**: Professional, kid-friendly, and inviting!

## Current Status

✅ Layout updated to use ImageView
✅ Reference set to `@drawable/ai_tutor_icon`
✅ Proper scaling configured (fitCenter)
✅ Padding added (8dp)
⏳ **Waiting for you to add the image file**

Once you add the image file and rebuild, the cute robot will appear in the AI Tutor card!

## Quick Command (if using terminal)

If you have the image on your desktop:

```bash
# Copy image to drawable folder
cp ~/Desktop/ai_tutor_icon.png app/src/main/res/drawable/

# Or if on Windows
copy %USERPROFILE%\Desktop\ai_tutor_icon.png app\src\main\res\drawable\
```

Then rebuild the project in Android Studio.
