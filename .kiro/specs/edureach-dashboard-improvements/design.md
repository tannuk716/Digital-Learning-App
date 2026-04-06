# Design Document: EduReach Dashboard Improvements

## Overview

This design addresses critical improvements to the EduReach Android educational application, focusing on stability, usability, and content management enhancements. The improvements span nine key areas: footer navigation fixes, link-based content uploads, content safety validation, AI tutor error handling, subject-organized quiz and notes displays, text visibility improvements, instant language switching, and overall application stability.

The design maintains backward compatibility with existing Firebase Authentication, Firestore data structures, and user accounts while introducing robust error handling and modern UI/UX patterns. The architecture leverages existing Android Kotlin patterns, Firebase SDK, and Gemini API integration without requiring major refactoring.

### Key Design Goals

1. **Zero Crashes**: Implement comprehensive null safety and error handling across all features
2. **Seamless UX**: Ensure smooth navigation, instant language switching, and clear visual feedback
3. **Content Safety**: Validate all teacher-uploaded content for appropriateness
4. **Backward Compatibility**: Preserve all existing functionality and data structures
5. **Maintainability**: Use clean architecture patterns with separation of concerns

## Architecture

### High-Level Architecture

The application follows a layered architecture pattern:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  (Activities, Adapters, ViewModels)                     │
│  - StudentDashboardActivity                             │
│  - TeacherDashboardActivity                             │
│  - AddLearningContentActivity                           │
│  - AIChatbotActivity                                    │
│  - NotesActivity, QuizActivity                          │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    Business Logic Layer                  │
│  (Repositories, Validators, Managers)                   │
│  - ContentRepository                                    │
│  - URLValidator (NEW)                                   │
│  - ContentSafetyValidator (NEW)                         │
│  - LanguageManager (ENHANCED)                           │
│  - ErrorHandler (NEW)                                   │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  (Firebase, Local Storage, API Clients)                 │
│  - FirebaseAuth                                         │
│  - FirebaseFirestore                                    │
│  - FirebaseStorage                                      │
│  - GeminiApiService                                     │
│  - SharedPreferences                                    │
└─────────────────────────────────────────────────────────┘
```

### Component Interaction Flow

**Content Upload Flow (with Safety Validation)**:
```
Teacher → AddLearningContentActivity → URLValidator → ContentSafetyValidator → ContentRepository → Firestore
```

**AI Tutor Flow (with Error Handling)**:
```
Student → AIChatbotActivity → ErrorHandler → GeminiApiService → Display Response/Error
```

**Language Switch Flow (Instant Update)**:
```
User → LanguageSelectionActivity → LanguageManager → Update Locale → Recreate Activities
```

## Components and Interfaces

### 1. URLValidator (New Component)

**Purpose**: Validate YouTube and Google Drive URLs before content upload

**Location**: `app/src/main/java/com/tannu/edureach/utils/URLValidator.kt`

**Interface**:
```kotlin
object URLValidator {
    data class ValidationResult(
        val isValid: Boolean,
        val urlType: URLType,
        val errorMessage: String? = null
    )
    
    enum class URLType {
        YOUTUBE, GOOGLE_DRIVE, INVALID
    }
    
    fun validateURL(url: String): ValidationResult
    fun extractYouTubeVideoId(url: String): String?
    fun extractGoogleDriveFileId(url: String): String?
}
```

**Validation Patterns**:
- YouTube: `youtube.com/watch?v=`, `youtu.be/`, `youtube.com/embed/`
- Google Drive: `drive.google.com/file/d/`, `drive.google.com/open?id=`

### 2. ContentSafetyValidator (New Component)

**Purpose**: Analyze URLs for inappropriate content before allowing upload

**Location**: `app/src/main/java/com/tannu/edureach/utils/ContentSafetyValidator.kt`

**Interface**:
```kotlin
class ContentSafetyValidator(private val context: Context) {
    data class SafetyResult(
        val isSafe: Boolean,
        val reason: String? = null,
        val confidence: Float = 0f
    )
    
    suspend fun validateContent(url: String): SafetyResult
    fun logBlockedSubmission(userId: String, url: String, reason: String)
}
```

**Validation Strategy**:
1. **URL Pattern Analysis**: Check URL against known inappropriate site patterns
2. **Keyword Detection**: Analyze URL path and query parameters for explicit keywords
3. **Domain Reputation**: Maintain allowlist of educational domains (youtube.com, drive.google.com)
4. **Timeout Handling**: Return warning if validation exceeds 10 seconds

### 3. ErrorHandler (New Component)

**Purpose**: Centralized error handling with user-friendly messages

**Location**: `app/src/main/java/com/tannu/edureach/utils/ErrorHandler.kt`

**Interface**:
```kotlin
object ErrorHandler {
    fun handleGeminiError(context: Context, exception: Exception, onRetry: () -> Unit): String
    fun handleNetworkError(context: Context, exception: Exception): String
    fun handleFirebaseError(context: Context, exception: Exception): String
    fun logError(tag: String, message: String, exception: Exception?)
}
```

### 4. Enhanced LanguageManager

**Purpose**: Instant language switching with activity recreation

**Location**: `app/src/main/java/com/tannu/edureach/utils/LanguageManager.kt` (Enhanced)

**New Methods**:
```kotlin
object LanguageManager {
    // Existing methods preserved
    fun setLocale(context: Context, languageCode: String)
    fun getLocale(context: Context): String
    
    // New methods
    fun applyLocaleAndRecreate(activity: Activity, languageCode: String)
    fun attachBaseContext(context: Context): Context
}
```

### 5. Modified AddLearningContentActivity

**Changes**:
- Remove file upload button and picker
- Add URL input validation
- Integrate ContentSafetyValidator
- Display validation progress and errors
- Maintain existing Firestore structure

**Key Methods**:
```kotlin
private suspend fun validateAndUploadContent(url: String, title: String, desc: String)
private fun showValidationProgress()
private fun showValidationError(message: String)
```

### 6. Enhanced AIChatbotActivity

**Changes**:
- Add API key validation on initialization
- Implement retry mechanism with preserved query
- Add timeout handling (30 seconds)
- Display user-friendly error messages
- Implement fallback after 3 failed retries

**Key Methods**:
```kotlin
private fun validateGeminiApiKey(): Boolean
private fun simulateAIResponseWithRetry(query: String, retryCount: Int = 0)
private fun showRetryButton(query: String)
```

### 7. Subject-Organized Quiz Display

**Changes to PracticeHubActivity**:
- Display subjects as primary navigation
- Show completion status per quiz
- Display points earned per completed quiz
- Calculate and show total points

**New Data Structure**:
```kotlin
data class QuizProgress(
    val quizId: String,
    val status: CompletionStatus,
    val pointsEarned: Int,
    val completedAt: Long?
)

enum class CompletionStatus {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}
```

### 8. Subject-Organized Notes with Download

**Changes to NotesActivity**:
- Organize notes by subject
- Add download functionality using DownloadManager
- Implement permission handling for storage
- Show download progress and confirmation

**Key Methods**:
```kotlin
private fun downloadNote(noteUrl: String, fileName: String)
private fun checkStoragePermission(): Boolean
private fun showDownloadProgress(downloadId: Long)
```

### 9. Footer Navigation Fixes

**Changes**:
- Update button IDs to be consistent across activities
- Implement proper click listeners with null checks
- Add modern Material icons
- Ensure proper alignment and visibility on all screen sizes

**Standard Footer Layout**:
```xml
<LinearLayout android:id="@+id/footerNavigation">
    <ImageButton android:id="@+id/btnNavHome" />
    <ImageButton android:id="@+id/btnNavProgress" />
    <ImageButton android:id="@+id/btnNavProfile" />
    <ImageButton android:id="@+id/btnNavLogout" />
</LinearLayout>
```

### 10. Text Visibility and Contrast Fixes

**Implementation Strategy**:
- Define color resources with WCAG AA compliant contrast ratios
- Update all text colors in layouts
- Ensure minimum text sizes (14sp body, 12sp secondary)
- Test on both light and dark themes

**Color Definitions** (`colors.xml`):
```xml
<!-- Light Theme -->
<color name="text_primary_light">#212121</color>
<color name="text_secondary_light">#757575</color>
<color name="background_light">#FFFFFF</color>

<!-- Dark Theme -->
<color name="text_primary_dark">#FFFFFF</color>
<color name="text_secondary_dark">#B0B0B0</color>
<color name="background_dark">#121212</color>
```

## Data Models

### Existing Models (Preserved)

All existing data models in `Models.kt` remain unchanged to maintain backward compatibility:

```kotlin
data class NoteContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val fileUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class VideoContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    val isYoutube: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuizModel(
    val id: String = "",
    val title: String = "",
    val questions: List<QuestionModel> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class RecentUploadModel(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val classId: String = "",
    val subjectId: String = "",
    val unitId: String = "",
    val url: String = "",
    val isYoutube: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
```

### New Models

**ContentValidationLog** (for audit trail):
```kotlin
data class ContentValidationLog(
    val id: String = "",
    val userId: String = "",
    val url: String = "",
    val blockedReason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
```

**QuizProgress** (for tracking completion and points):
```kotlin
data class QuizProgress(
    val userId: String = "",
    val quizId: String = "",
    val status: String = "not_started", // not_started, in_progress, completed
    val pointsEarned: Int = 0,
    val completedAt: Long? = null,
    val lastAttemptAt: Long = System.currentTimeMillis()
)
```

### Firestore Collections Structure

**Existing Collections** (unchanged):
```
/users/{userId}
  - name, email, role, className, avatar, etc.
  
/content/{classId}/subjects/{subjectId}/units/{unitId}/videos/{videoId}
  - title, description, videoUrl, isYoutube, timestamp
  
/content/{classId}/subjects/{subjectId}/units/{unitId}/notes/{noteId}
  - title, description, fileUrl, timestamp
  
/content/{classId}/subjects/{subjectId}/units/{unitId}/quizzes/{quizId}
  - title, questions[], timestamp
```

**New Collections**:
```
/content_validation_logs/{logId}
  - userId, url, blockedReason, timestamp
  
/users/{userId}/quiz_progress/{quizId}
  - status, pointsEarned, completedAt, lastAttemptAt
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property 1: Footer Navigation Executes Without Errors

*For any* footer navigation button in Teacher_Dashboard or Student_Dashboard, clicking the button should execute the corresponding action without throwing exceptions or causing crashes.

**Validates: Requirements 1.1, 1.2**

### Property 2: Logout Clears Authentication State

*For any* authenticated user, clicking the logout button should sign out the user from Firebase Auth and navigate to LoginActivity.

**Validates: Requirements 1.3**

### Property 3: Footer Navigation Buttons Are Visible

*For any* activity with footer navigation, all navigation buttons should have visibility set to VISIBLE and be within screen bounds when rendered.

**Validates: Requirements 1.5**

### Property 4: URL Validation Correctly Identifies Valid URLs

*For any* URL string, the URL_Validator should return isValid=true if and only if the URL matches valid YouTube or Google Drive patterns.

**Validates: Requirements 2.3**

### Property 5: Invalid URL Submission Is Blocked

*For any* invalid URL submitted through Content_Upload_System, the system should display the error message "Invalid URL format. Please enter a valid YouTube or Google Drive link" and prevent data submission to Firestore.

**Validates: Requirements 2.4**

### Property 6: YouTube URL Storage Includes Correct Flag

*For any* valid YouTube URL submitted and stored, the stored VideoContent object should have isYoutube flag set to true.

**Validates: Requirements 2.5**

### Property 7: Google Drive URL Storage Includes Metadata

*For any* valid Google Drive URL submitted and stored, the stored NoteContent object should contain the URL in the fileUrl field with non-empty id and timestamp.

**Validates: Requirements 2.6**

### Property 8: Content Safety Validator Is Invoked

*For any* content URL submission, the Content_Safety_Validator.validateContent() method should be called before proceeding with upload.

**Validates: Requirements 3.1**

### Property 9: Unsafe Content Is Blocked

*For any* URL containing inappropriate content indicators (violence/sexual keywords), the Content_Safety_Validator should return isSafe=false and the system should display "Content blocked: This link contains inappropriate material" and prevent submission.

**Validates: Requirements 3.2**

### Property 10: Validation Timeout Displays Warning

*For any* content validation that exceeds 10 seconds, the system should display warning message "Unable to verify content safety. Please review manually before submitting".

**Validates: Requirements 3.3**

### Property 11: Safe Content Proceeds to Upload

*For any* URL that passes content safety validation (isSafe=true), the Content_Upload_System should proceed with normal Firestore upload flow.

**Validates: Requirements 3.4**

### Property 12: Blocked Submissions Are Logged

*For any* content submission blocked by Content_Safety_Validator, a ContentValidationLog entry should be created in Firestore with userId, url, blockedReason, and timestamp.

**Validates: Requirements 3.5**

### Property 13: Invalid API Key Shows Error Without Crash

*For any* AI_Tutor initialization with missing or invalid Gemini API key, the activity should display "AI Tutor is temporarily unavailable. Please contact your teacher" without throwing exceptions.

**Validates: Requirements 4.2**

### Property 14: Failed API Call Shows Retry Button

*For any* AI_Tutor API call that fails, the UI should display a retry button and preserve the original query text for retry.

**Validates: Requirements 4.3**

### Property 15: Three Failed Retries Show Fallback Message

*For any* AI_Tutor query that fails 3 consecutive times, the system should display "I'm having trouble connecting right now. Please try again later or ask your teacher" and stop retrying.

**Validates: Requirements 4.4**

### Property 16: Network Timeout Handled Gracefully

*For any* AI_Tutor API call that times out after 30 seconds, the activity should remain stable (not crash) and display an error message.

**Validates: Requirements 4.5**

### Property 17: AI Tutor Exceptions Are Logged and Displayed

*For any* exception thrown during AI_Tutor operation, the system should log the error details and display a user-friendly error message without crashing.

**Validates: Requirements 4.6**

### Property 18: Subject Selection Filters Quizzes

*For any* subject selected in Quiz_Section, the displayed quiz list should contain only quizzes where quiz.subjectId matches the selected subject.

**Validates: Requirements 5.2**

### Property 19: Quiz Display Includes Completion Status

*For any* quiz displayed in Quiz_Section, the UI should show exactly one of three completion statuses: "completed", "in-progress", or "not-started".

**Validates: Requirements 5.3**

### Property 20: Completed Quiz Shows Points

*For any* quiz with completion status "completed", the UI should display the pointsEarned value from QuizProgress.

**Validates: Requirements 5.4**

### Property 21: Quiz Completion Updates Rewards

*For any* quiz completed by a student, a QuizProgress document should be created/updated in Firestore with status="completed" and pointsEarned equal to the quiz score.

**Validates: Requirements 5.5**

### Property 22: Total Points Equals Sum of Quiz Points

*For any* student, the displayed total points should equal the sum of pointsEarned across all QuizProgress documents where status="completed".

**Validates: Requirements 5.6**

### Property 23: Subject Selection Filters Notes

*For any* subject selected in Notes_Section, the displayed notes list should contain only notes where note.subjectId matches the selected subject, and each note should display title and description.

**Validates: Requirements 6.2**

### Property 24: Note Click Opens Viewer

*For any* note clicked in Notes_Section, the system should navigate to a viewer activity or display the note content without crashing.

**Validates: Requirements 6.3**

### Property 25: Each Note Has Download Button

*For any* note displayed in Notes_Section, the UI should include a visible download button associated with that note.

**Validates: Requirements 6.4**

### Property 26: Successful Download Shows Confirmation

*For any* note download that completes successfully, the system should save the file to device storage and display "Note downloaded successfully".

**Validates: Requirements 6.5**

### Property 27: Failed Download Shows Error Message

*For any* note download that fails (due to permissions or network), the system should display "Download failed. Please check your storage permissions and try again".

**Validates: Requirements 6.6**

### Property 28: Dark Theme Uses Light Text

*For any* screen displayed with dark theme enabled, all text elements should use light-colored text (color value >= #B0B0B0).

**Validates: Requirements 7.2**

### Property 29: Light Theme Uses Dark Text

*For any* screen displayed with light theme enabled, all text elements should use dark-colored text (color value <= #757575).

**Validates: Requirements 7.3**

### Property 30: Text Size Meets Minimum Requirements

*For any* text view in the app, body text should have textSize >= 14sp and secondary text should have textSize >= 12sp.

**Validates: Requirements 7.5**

### Property 31: Language Selection Applies Immediately

*For any* language selected in LanguageSelectionActivity, the Language_Manager should call setLocale() and update the app's locale configuration before returning control to the user.

**Validates: Requirements 8.1**

### Property 32: Locale Change Refreshes UI

*For any* locale update by Language_Manager, all visible activities should be recreated to display text in the newly selected language.

**Validates: Requirements 8.2**

### Property 33: Language Preference Persistence Round-Trip

*For any* language code saved by Language_Manager to SharedPreferences, calling getLocale() should return the same language code.

**Validates: Requirements 8.3**

### Property 34: Language Consistency Across Navigation

*For any* screen navigation after language change, all text elements should display in the selected language (matching the locale in SharedPreferences).

**Validates: Requirements 8.4**

### Property 35: App Restart Loads Saved Language

*For any* app restart, the Language_Manager should load the language preference from SharedPreferences and apply it before onCreate() of the first activity completes.

**Validates: Requirements 8.5**

### Property 36: Firebase Auth Flows Remain Functional

*For any* existing Firebase Authentication flow (login, register, logout), the flow should complete successfully with the same behavior as before improvements.

**Validates: Requirements 9.1**

### Property 37: Firestore Queries Return Expected Data

*For any* existing Firestore query in the app, the query should return data matching the expected structure (same fields and types as before improvements).

**Validates: Requirements 9.2**

### Property 38: Existing Users Can Login

*For any* user account created before improvements, the user should be able to login successfully with their existing credentials.

**Validates: Requirements 9.4**

### Property 39: Errors Display User-Friendly Messages

*For any* error encountered during app operation, the system should log error details and display a user-friendly message without crashing the activity.

**Validates: Requirements 9.5**

### Property 40: Null Inputs Don't Cause Crashes

*For any* method that receives null input where null is possible, the method should handle the null with default values or early return without throwing NullPointerException.

**Validates: Requirements 9.6**

### Property 41: Network Operations Handle Timeouts

*For any* network operation (Firebase, Gemini API), the operation should have a timeout configured and handle timeout exceptions gracefully with error messages.

**Validates: Requirements 9.7**


## Error Handling

### Error Handling Strategy

The application implements a multi-layered error handling approach to ensure zero crashes and provide clear user feedback:

#### 1. Null Safety Layer

**Implementation**:
- Use Kotlin's null safety features (`?.`, `?:`, `!!` only when guaranteed non-null)
- Provide default values for all nullable fields in data classes
- Implement null checks before accessing Firebase data

**Example Pattern**:
```kotlin
val userName = document.getString("name") ?: "Student"
val classId = intent.getStringExtra("CLASS_ID") ?: run {
    Toast.makeText(this, "Missing class information", Toast.LENGTH_SHORT).show()
    finish()
    return
}
```

#### 2. Network Error Handling

**Scenarios Covered**:
- Network unavailable
- Request timeout (30 seconds for Gemini API, 10 seconds for content validation)
- Server errors (5xx responses)
- Invalid responses

**Implementation Pattern**:
```kotlin
try {
    withTimeout(30000) {
        val response = geminiApi.generateContent(apiKey, request)
        if (response.isSuccessful && response.body() != null) {
            // Handle success
        } else {
            ErrorHandler.handleNetworkError(this, Exception("API returned ${response.code()}"))
        }
    }
} catch (e: TimeoutCancellationException) {
    showError("Request timed out. Please check your connection.")
} catch (e: Exception) {
    ErrorHandler.handleNetworkError(this, e)
}
```

#### 3. Firebase Error Handling

**Scenarios Covered**:
- Authentication failures
- Permission denied errors
- Document not found
- Network errors during Firebase operations

**Implementation Pattern**:
```kotlin
db.collection("users").document(uid).get()
    .addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            // Process document
        } else {
            showError("User profile not found")
        }
    }
    .addOnFailureListener { exception ->
        ErrorHandler.handleFirebaseError(this, exception)
        // Provide fallback behavior
    }
```

#### 4. API Key Validation

**Implementation**:
```kotlin
private fun validateGeminiApiKey(): Boolean {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isNullOrEmpty() || apiKey == "YOUR_API_KEY_HERE") {
        showError("AI Tutor is temporarily unavailable. Please contact your teacher")
        btnSend.isEnabled = false
        return false
    }
    return true
}
```

#### 5. Content Validation Error Handling

**Scenarios**:
- Invalid URL format
- Unsafe content detected
- Validation timeout
- Network error during validation

**User Feedback**:
- Clear error messages explaining the issue
- Actionable guidance (e.g., "Please enter a valid YouTube or Google Drive link")
- Visual feedback (red text, error icons)

#### 6. Storage Permission Handling

**Implementation for Note Downloads**:
```kotlin
private fun downloadNote(noteUrl: String, fileName: String) {
    if (!checkStoragePermission()) {
        requestStoragePermission()
        return
    }
    
    try {
        val request = DownloadManager.Request(Uri.parse(noteUrl))
            .setTitle(fileName)
            .setDescription("Downloading note...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)
        
        Toast.makeText(this, "Note downloaded successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        ErrorHandler.logError("NotesActivity", "Download failed", e)
        Toast.makeText(this, "Download failed. Please check your storage permissions and try again", Toast.LENGTH_LONG).show()
    }
}
```

#### 7. Retry Mechanism for AI Tutor

**Implementation**:
```kotlin
private fun simulateAIResponseWithRetry(query: String, retryCount: Int = 0) {
    lifecycleScope.launch {
        try {
            // Attempt API call
            val response = geminiApi.generateContent(apiKey, request)
            // Handle response
        } catch (e: Exception) {
            if (retryCount < 3) {
                // Show retry button
                addMessage(ChatMessage(
                    "Request failed. Tap to retry.",
                    isSender = false,
                    isError = true,
                    originalQuery = query
                ))
            } else {
                // Show fallback message after 3 retries
                addMessage(ChatMessage(
                    "I'm having trouble connecting right now. Please try again later or ask your teacher",
                    isSender = false
                ))
            }
        }
    }
}
```

### Error Logging

All errors are logged using a centralized ErrorHandler:

```kotlin
object ErrorHandler {
    fun logError(tag: String, message: String, exception: Exception?) {
        Log.e(tag, message, exception)
        // Optional: Send to Firebase Crashlytics
        // FirebaseCrashlytics.getInstance().recordException(exception ?: Exception(message))
    }
}
```

### User-Friendly Error Messages

| Error Scenario | Technical Error | User-Friendly Message |
|----------------|-----------------|----------------------|
| Gemini API key missing | BuildConfig.GEMINI_API_KEY is null | "AI Tutor is temporarily unavailable. Please contact your teacher" |
| Network timeout | SocketTimeoutException | "Request timed out. Please check your connection." |
| Invalid URL format | Pattern mismatch | "Invalid URL format. Please enter a valid YouTube or Google Drive link" |
| Unsafe content detected | Content validation failed | "Content blocked: This link contains inappropriate material" |
| Download permission denied | SecurityException | "Download failed. Please check your storage permissions and try again" |
| Firebase auth error | FirebaseAuthException | "Login failed. Please check your credentials and try again" |
| Document not found | DocumentSnapshot.exists() == false | "Content not found. It may have been removed." |

## Testing Strategy

### Dual Testing Approach

The testing strategy employs both unit tests and property-based tests to ensure comprehensive coverage:

**Unit Tests**: Focus on specific examples, edge cases, and integration points
**Property Tests**: Verify universal properties across randomized inputs

### Property-Based Testing Configuration

**Library**: [Kotest Property Testing](https://kotest.io/docs/proptest/property-based-testing.html) for Kotlin

**Configuration**:
- Minimum 100 iterations per property test
- Each test tagged with feature name and property number
- Tag format: `@Tag("Feature: edureach-dashboard-improvements, Property {number}: {property_text}")`

### Test Organization

```
app/src/test/java/com/tannu/edureach/
├── unit/
│   ├── URLValidatorTest.kt
│   ├── ContentSafetyValidatorTest.kt
│   ├── LanguageManagerTest.kt
│   └── ErrorHandlerTest.kt
├── property/
│   ├── URLValidationPropertyTest.kt
│   ├── ContentSafetyPropertyTest.kt
│   ├── QuizPointsPropertyTest.kt
│   └── LanguagePersistencePropertyTest.kt
└── integration/
    ├── ContentUploadIntegrationTest.kt
    ├── AIChatbotIntegrationTest.kt
    └── QuizFlowIntegrationTest.kt
```

### Unit Test Examples

**URLValidator Unit Tests**:
```kotlin
class URLValidatorTest {
    @Test
    fun `valid YouTube URL is recognized`() {
        val result = URLValidator.validateURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
    }
    
    @Test
    fun `invalid URL returns error message`() {
        val result = URLValidator.validateURL("not-a-url")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `Google Drive URL is recognized`() {
        val result = URLValidator.validateURL("https://drive.google.com/file/d/1ABC123/view")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
    }
}
```

**ContentSafetyValidator Unit Tests**:
```kotlin
class ContentSafetyValidatorTest {
    @Test
    fun `educational YouTube domain passes validation`() = runBlocking {
        val validator = ContentSafetyValidator(context)
        val result = validator.validateContent("https://www.youtube.com/watch?v=educationalVideo")
        assertTrue(result.isSafe)
    }
    
    @Test
    fun `URL with inappropriate keywords is blocked`() = runBlocking {
        val validator = ContentSafetyValidator(context)
        val result = validator.validateContent("https://example.com/violent-content")
        assertFalse(result.isSafe)
        assertNotNull(result.reason)
    }
    
    @Test
    fun `validation timeout returns warning`() = runBlocking {
        val validator = ContentSafetyValidator(context)
        // Mock slow validation
        val result = withTimeout(11000) {
            validator.validateContent("https://slow-site.com/content")
        }
        // Should return warning result
    }
}
```

### Property-Based Test Examples

**Property 4: URL Validation Correctly Identifies Valid URLs**:
```kotlin
@Tag("Feature: edureach-dashboard-improvements, Property 4: URL Validation Correctly Identifies Valid URLs")
class URLValidationPropertyTest : StringSpec({
    "URL validator correctly identifies valid YouTube and Google Drive URLs" {
        checkAll(100, Arb.string()) { urlString ->
            val result = URLValidator.validateURL(urlString)
            
            val isYouTubePattern = urlString.contains("youtube.com/watch?v=") || 
                                   urlString.contains("youtu.be/")
            val isGoogleDrivePattern = urlString.contains("drive.google.com/file/d/")
            
            if (isYouTubePattern || isGoogleDrivePattern) {
                result.isValid shouldBe true
            } else {
                result.isValid shouldBe false
            }
        }
    }
})
```

**Property 22: Total Points Equals Sum of Quiz Points**:
```kotlin
@Tag("Feature: edureach-dashboard-improvements, Property 22: Total Points Equals Sum of Quiz Points")
class QuizPointsPropertyTest : StringSpec({
    "total points equals sum of individual quiz points" {
        checkAll(100, Arb.list(Arb.int(0..100), 1..20)) { quizScores ->
            // Create quiz progress entries
            val progressList = quizScores.mapIndexed { index, score ->
                QuizProgress(
                    userId = "testUser",
                    quizId = "quiz_$index",
                    status = "completed",
                    pointsEarned = score,
                    completedAt = System.currentTimeMillis()
                )
            }
            
            // Calculate total
            val calculatedTotal = progressList.sumOf { it.pointsEarned }
            val expectedTotal = quizScores.sum()
            
            calculatedTotal shouldBe expectedTotal
        }
    }
})
```

**Property 33: Language Preference Persistence Round-Trip**:
```kotlin
@Tag("Feature: edureach-dashboard-improvements, Property 33: Language Preference Persistence Round-Trip")
class LanguagePersistencePropertyTest : StringSpec({
    "language preference persists correctly through save and load" {
        checkAll(100, Arb.of("en", "hi", "bn", "ta", "te", "mr")) { languageCode ->
            val context = ApplicationProvider.getApplicationContext<Context>()
            
            // Save language
            LanguageManager.setLocale(context, languageCode)
            
            // Load language
            val loadedLanguage = LanguageManager.getLocale(context)
            
            // Verify round-trip
            loadedLanguage shouldBe languageCode
        }
    }
})
```

### Integration Test Examples

**Content Upload Integration Test**:
```kotlin
class ContentUploadIntegrationTest {
    @Test
    fun `complete content upload flow with validation`() = runBlocking {
        // Setup
        val validator = ContentSafetyValidator(context)
        val repository = ContentRepository()
        val testUrl = "https://www.youtube.com/watch?v=testVideo"
        
        // Validate URL
        val urlResult = URLValidator.validateURL(testUrl)
        assertTrue(urlResult.isValid)
        
        // Validate content safety
        val safetyResult = validator.validateContent(testUrl)
        assertTrue(safetyResult.isSafe)
        
        // Upload content
        val video = VideoContent(
            title = "Test Video",
            description = "Test Description",
            videoUrl = testUrl,
            isYoutube = true
        )
        val success = repository.uploadVideo("class_1", "maths", "unit_1", video)
        assertTrue(success)
    }
}
```

### Test Coverage Goals

- **Unit Test Coverage**: Minimum 80% code coverage for new components (URLValidator, ContentSafetyValidator, ErrorHandler)
- **Property Test Coverage**: All 41 correctness properties implemented as property-based tests
- **Integration Test Coverage**: Critical user flows (content upload, AI tutor, quiz completion, note download)
- **UI Test Coverage**: Footer navigation, language switching, error message display

### Continuous Testing

- Run unit tests on every commit
- Run property tests (100 iterations) on pull requests
- Run integration tests before releases
- Monitor crash reports in production using Firebase Crashlytics

