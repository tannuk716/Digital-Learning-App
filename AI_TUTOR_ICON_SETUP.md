# AI Tutor Icon Replacement Guide

## What Was Changed

The AI Tutor card in the Student Dashboard has been updated to use an ImageView instead of an emoji (🤖).

### Layout Changes
**File**: `app/src/main/res/layout/activity_student_dashboard.xml`

Changed from:
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="0dp"
    android:layout_weight="1"
    android:text="🤖"
    android:textSize="50sp"
    android:gravity="center"/>
```

To:
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

## How to Add the Image

### Step 1: Save the Robot Image

1. Save the cute robot image you provided
2. Rename it to: `ai_tutor_icon.png` (or `.jpg`)
3. Recommended size: 512x512 pixels or similar square dimensions

### Step 2: Add to Project

**Option A: Using Android Studio**
1. Right-click on `app/src/main/res/drawable/` folder
2. Select `New` → `Image Asset` or just paste the file
3. Name it: `ai_tutor_icon`
4. Click Finish

**Option B: Manual Copy**
1. Navigate to your project folder
2. Go to: `app/src/main/res/drawable/`
3. Copy your image file there
4. Rename to: `ai_tutor_icon.png`

### Step 3: Verify

The layout is already configured to use `@drawable/ai_tutor_icon`, so once you add the image file, it will automatically appear in the AI Tutor card.

## Image Specifications

### Recommended:
- **Format**: PNG (with transparency) or JPG
- **Size**: 512x512 pixels (square)
- **File name**: `ai_tutor_icon.png`
- **Location**: `app/src/main/res/drawable/`

### The Image Will:
- Fill the center area of the AI Tutor card
- Scale to fit while maintaining aspect ratio
- Have 8dp padding around it
- Display on a blue gradient background

## Result

After adding the image, the AI Tutor card will show:
- Title: "AI Tutor" (from strings.xml)
- Icon: Your cute robot image (centered)
- Description: "Ask Questions" (from strings.xml)
- Background: Blue gradient

## Troubleshooting

### If the image doesn't appear:
1. Check the file name is exactly: `ai_tutor_icon.png` (or `.jpg`)
2. Verify it's in: `app/src/main/res/drawable/`
3. Clean and rebuild the project: `Build` → `Clean Project` → `Rebuild Project`
4. Sync Gradle files

### If the image looks stretched:
- The `scaleType="fitCenter"` ensures it maintains aspect ratio
- The padding provides space around the image
- If needed, you can adjust the padding value in the layout

## Alternative: Multiple Resolutions

For best quality across all devices, you can provide multiple resolutions:

```
app/src/main/res/
├── drawable-mdpi/ai_tutor_icon.png (48x48)
├── drawable-hdpi/ai_tutor_icon.png (72x72)
├── drawable-xhdpi/ai_tutor_icon.png (96x96)
├── drawable-xxhdpi/ai_tutor_icon.png (144x144)
└── drawable-xxxhdpi/ai_tutor_icon.png (192x192)
```

Or just put one high-resolution image (512x512) in `drawable/` and Android will scale it automatically.

## Status

✅ Layout updated to use ImageView
✅ Reference set to `@drawable/ai_tutor_icon`
✅ Proper scaling and padding configured
⏳ Waiting for image file to be added

Once you add the image file, the cute robot will appear in place of the 🤖 emoji!
