# Implementation Plan: EduReach Dashboard Improvements

## Overview

This implementation plan addresses nine critical improvement areas for the EduReach Android educational app: footer navigation fixes, link-based content uploads with safety validation, AI tutor error handling, subject-organized quiz and notes displays, text visibility improvements, instant language switching, and overall application stability. All tasks maintain backward compatibility with existing Firebase structures and user accounts.

## Tasks

- [ ] 1. Create utility components for URL validation and content safety
  - [x] 1.1 Create URLValidator utility class
    - Implement `URLValidator.kt` in `utils` package with validation methods
    - Add regex patterns for YouTube URLs (youtube.com/watch?v=, youtu.be/, youtube.com/embed/)
    - Add regex patterns for Google Drive URLs (drive.google.com/file/d/, drive.google.com/open?id=)
    - Implement `validateURL()` method returning ValidationResult with isValid, urlType, and errorMessage
    - Implement `extractYouTubeVideoId()` and `extractGoogleDriveFileId()` helper methods
    - _Requirements: 2.3, 2.4_
  
  - [ ]* 1.2 Write property test for URL validation
    - **Property 4: URL Validation Correctly Identifies Valid URLs**
    - **Validates: Requirements 2.3**
  
  - [x] 1.3 Create ContentSafetyValidator utility class
    - Implement `ContentSafetyValidator.kt` in `utils` package
    - Add suspend function `validateContent(url: String): SafetyResult`
    - Implement URL pattern analysis against inappropriate site patterns
    - Implement keyword detection for explicit content in URL paths
    - Add domain allowlist check (youtube.com, drive.google.com)
    - Implement 10-second timeout handling with warning result
    - Add `logBlockedSubmission()` method for audit logging
    - _Requirements: 3.1, 3.2, 3.3, 3.5_
  
  - [ ]* 1.4 Write property test for content safety validation
    - **Property 8: Content Safety Validator Is Invoked**
    - **Property 9: Unsafe Content Is Blocked**
    - **Validates: Requirements 3.1, 3.2**
  
  - [x] 1.5 Create ErrorHandler utility object
    - Implement `ErrorHandler.kt` in `utils` package
    - Add `handleGeminiError()` method with context and retry callback
    - Add `handleNetworkError()` method for network failures
    - Add `handleFirebaseError()` method for Firebase exceptions
    - Add `logError()` method for centralized error logging
    - Return user-friendly error messages for each error type
    - _Requirements: 4.6, 9.5_
  
  - [ ]* 1.6 Write unit tests for ErrorHandler
    - Test error message generation for different exception types
    - Test logging functionality
    - _Requirements: 9.5_

- [ ] 2. Enhance LanguageManager for instant language switching
  - [x] 2.1 Add instant language switching methods to LanguageManager
    - Open existing `LanguageManager.kt` in `utils` package
    - Add `applyLocaleAndRecreate(activity: Activity, languageCode: String)` method
    - Add `attachBaseContext(context: Context): Context` method for activity context wrapping
    - Ensure `setLocale()` updates SharedPreferences immediately
    - Ensure `getLocale()` reads from SharedPreferences with default fallback
    - _Requirements: 8.1, 8.2, 8.3_
  
  - [ ]* 2.2 Write property test for language persistence
    - **Property 33: Language Preference Persistence Round-Trip**
    - **Validates: Requirements 8.3**
  
  - [x] 2.3 Update LanguageSelectionActivity to use instant switching
    - Modify language selection button click listeners to call `applyLocaleAndRecreate()`
    - Remove any app restart prompts or delays
    - Test that UI refreshes immediately after language selection
    - _Requirements: 8.1, 8.2, 8.4_

- [x] 3. Checkpoint - Verify utility components
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 4. Modify AddLearningContentActivity for link-based uploads
  - [x] 4.1 Update AddLearningContentActivity UI and logic
    - Remove file upload button and file picker code from layout and activity
    - Add EditText for URL input with hint "Enter YouTube or Google Drive link"
    - Update submit button click listener to validate URL using URLValidator
    - Display error message "Invalid URL format. Please enter a valid YouTube or Google Drive link" for invalid URLs
    - Add progress indicator for content safety validation
    - _Requirements: 2.1, 2.2, 2.4_
  
  - [x] 4.2 Integrate ContentSafetyValidator into upload flow
    - Add suspend function `validateAndUploadContent(url: String, title: String, desc: String)`
    - Call `ContentSafetyValidator.validateContent()` before Firestore upload
    - Display "Content blocked: This link contains inappropriate material" if validation fails
    - Display "Unable to verify content safety. Please review manually before submitting" on timeout
    - Proceed with Firestore upload only if validation passes
    - _Requirements: 3.1, 3.2, 3.3, 3.4_
  
  - [x] 4.3 Update Firestore upload logic for URL-based content
    - Set `isYoutube` flag to true for YouTube URLs when creating VideoContent
    - Store Google Drive URLs in `fileUrl` field for NoteContent
    - Maintain existing Firestore collection structure (/content/{classId}/subjects/{subjectId}/units/{unitId}/)
    - Add timestamp and generate unique ID for each upload
    - _Requirements: 2.5, 2.6_
  
  - [ ]* 4.4 Write integration test for content upload flow
    - Test complete flow: URL validation → safety check → Firestore upload
    - Test blocked content prevents upload
    - _Requirements: 2.3, 3.1, 3.4_

- [ ] 5. Enhance AIChatbotActivity with error handling
  - [-] 5.1 Add API key validation to AIChatbotActivity
    - Add `validateGeminiApiKey()` method in onCreate()
    - Check if BuildConfig.GEMINI_API_KEY is null, empty, or placeholder
    - Display "AI Tutor is temporarily unavailable. Please contact your teacher" if invalid
    - Disable send button if API key is invalid
    - _Requirements: 4.1, 4.2_
  
  - [ ] 5.2 Implement retry mechanism for AI Tutor
    - Modify `simulateAIResponseWithRetry()` to accept query and retryCount parameters
    - Wrap API call in try-catch with timeout (30 seconds)
    - On failure, display retry button with preserved query text
    - Increment retryCount on each retry attempt
    - After 3 failed retries, display "I'm having trouble connecting right now. Please try again later or ask your teacher"
    - _Requirements: 4.3, 4.4, 4.5_
  
  - [ ] 5.3 Add comprehensive error handling to AI Tutor
    - Use ErrorHandler.handleGeminiError() for all exceptions
    - Handle TimeoutCancellationException separately with timeout message
    - Handle network errors with user-friendly messages
    - Log all errors with ErrorHandler.logError()
    - Ensure activity never crashes on API failures
    - _Requirements: 4.6, 9.5_
  
  - [ ]* 5.4 Write integration test for AI Tutor error scenarios
    - Test invalid API key handling
    - Test retry mechanism
    - Test timeout handling
    - _Requirements: 4.2, 4.3, 4.5_

- [ ] 6. Checkpoint - Verify content upload and AI tutor improvements
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 7. Implement subject-organized quiz display with progress tracking
  - [ ] 7.1 Create QuizProgress data model
    - Add `QuizProgress` data class to `Models.kt`
    - Include fields: userId, quizId, status (not_started/in_progress/completed), pointsEarned, completedAt, lastAttemptAt
    - Add default values for all fields
    - _Requirements: 5.3, 5.4_
  
  - [ ] 7.2 Update PracticeHubActivity for subject-organized display
    - Modify layout to show subjects as primary navigation (tabs or spinner)
    - Filter quizzes by selected subject using subjectId
    - Display quiz list with title and completion status for each quiz
    - Query QuizProgress collection to get status and points for current user
    - Display "completed", "in-progress", or "not-started" status for each quiz
    - Display pointsEarned for completed quizzes
    - _Requirements: 5.1, 5.2, 5.3, 5.4_
  
  - [ ] 7.3 Implement total points calculation and display
    - Query all QuizProgress documents for current user where status="completed"
    - Calculate sum of pointsEarned across all completed quizzes
    - Display total points in PracticeHubActivity header or summary section
    - _Requirements: 5.6_
  
  - [ ] 7.4 Update QuizResultActivity to save progress
    - After quiz completion, create/update QuizProgress document in Firestore
    - Set status="completed", pointsEarned=score, completedAt=timestamp
    - Save to path: /users/{userId}/quiz_progress/{quizId}
    - Update Rewards_System with earned points
    - _Requirements: 5.5_
  
  - [ ]* 7.5 Write property test for quiz points calculation
    - **Property 22: Total Points Equals Sum of Quiz Points**
    - **Validates: Requirements 5.6**

- [ ] 8. Implement subject-organized notes display with download
  - [ ] 8.1 Update NotesActivity for subject-organized display
    - Modify layout to show subjects as primary navigation (tabs or spinner)
    - Filter notes by selected subject using subjectId
    - Display notes list with title and description for each note
    - Query Firestore: /content/{classId}/subjects/{subjectId}/units/{unitId}/notes/
    - _Requirements: 6.1, 6.2_
  
  - [ ] 8.2 Add note viewer functionality
    - Implement click listener for each note item
    - Open note content in EducationalWebActivity or dedicated viewer
    - Handle null/empty URLs gracefully without crashing
    - _Requirements: 6.3_
  
  - [ ] 8.3 Implement note download functionality
    - Add download button to each note item in the list
    - Implement `downloadNote(noteUrl: String, fileName: String)` method
    - Check storage permissions using `checkStoragePermission()`
    - Request permissions if not granted
    - Use DownloadManager to download file to external storage
    - Display "Note downloaded successfully" on success
    - Display "Download failed. Please check your storage permissions and try again" on failure
    - _Requirements: 6.4, 6.5, 6.6_
  
  - [ ]* 8.4 Write unit tests for download functionality
    - Test permission checking
    - Test download success and failure scenarios
    - _Requirements: 6.5, 6.6_

- [ ] 9. Fix footer navigation across all activities
  - [ ] 9.1 Standardize footer navigation button IDs
    - Update all footer layouts to use consistent IDs: btnNavHome, btnNavProgress, btnNavProfile, btnNavLogout
    - Ensure all buttons use modern Material icons
    - Verify proper alignment and visibility on mobile screens
    - _Requirements: 1.4, 1.5_
  
  - [ ] 9.2 Fix footer navigation in StudentDashboardActivity
    - Update button IDs to match standard naming
    - Add null checks before setting click listeners
    - Implement proper navigation for Home, Progress, Profile buttons
    - Implement logout with FirebaseAuth.signOut() and navigation to LoginActivity
    - Wrap all click listeners in try-catch to prevent crashes
    - _Requirements: 1.1, 1.2, 1.3_
  
  - [ ] 9.3 Fix footer navigation in TeacherDashboardActivity
    - Update button IDs to match standard naming
    - Add null checks before setting click listeners
    - Implement proper navigation for Home, Progress, Profile buttons
    - Implement logout with FirebaseAuth.signOut() and navigation to LoginActivity
    - Wrap all click listeners in try-catch to prevent crashes
    - _Requirements: 1.1, 1.2, 1.3_
  
  - [ ]* 9.4 Write property tests for footer navigation
    - **Property 1: Footer Navigation Executes Without Errors**
    - **Property 2: Logout Clears Authentication State**
    - **Property 3: Footer Navigation Buttons Are Visible**
    - **Validates: Requirements 1.1, 1.2, 1.3, 1.5**

- [ ] 10. Checkpoint - Verify quiz, notes, and navigation improvements
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 11. Improve text visibility and contrast
  - [ ] 11.1 Define WCAG-compliant color resources
    - Update `colors.xml` with text_primary_light (#212121), text_secondary_light (#757575)
    - Add text_primary_dark (#FFFFFF), text_secondary_dark (#B0B0B0)
    - Add background_light (#FFFFFF), background_dark (#121212)
    - Ensure all color combinations meet 4.5:1 contrast ratio
    - _Requirements: 7.1, 7.2, 7.3_
  
  - [ ] 11.2 Update text colors in all layouts
    - Update StudentDashboardActivity layout to use new color resources
    - Update TeacherDashboardActivity layout to use new color resources
    - Update all other activity layouts (AIChatbotActivity, NotesActivity, PracticeHubActivity, etc.)
    - Ensure minimum text sizes: 14sp for body text, 12sp for secondary text
    - Test on both light and dark themes
    - _Requirements: 7.2, 7.3, 7.4, 7.5_
  
  - [ ]* 11.3 Write property tests for text visibility
    - **Property 28: Dark Theme Uses Light Text**
    - **Property 29: Light Theme Uses Dark Text**
    - **Property 30: Text Size Meets Minimum Requirements**
    - **Validates: Requirements 7.2, 7.3, 7.5**

- [ ] 12. Add comprehensive null safety and error handling
  - [ ] 12.1 Add null safety to all Firebase data access
    - Review all Firestore queries and add null checks with default values
    - Use Elvis operator (?:) for all getString(), getLong(), getBoolean() calls
    - Add document.exists() checks before accessing data
    - Handle missing intent extras with default values or early returns
    - _Requirements: 9.6_
  
  - [ ] 12.2 Add timeout handling to all network operations
    - Wrap all Firebase operations in try-catch blocks
    - Add 30-second timeout to Gemini API calls using withTimeout()
    - Add 10-second timeout to content validation
    - Handle TimeoutCancellationException with user-friendly messages
    - _Requirements: 9.7_
  
  - [ ] 12.3 Add error handling to all activities
    - Wrap critical operations in try-catch blocks
    - Use ErrorHandler for all exceptions
    - Display user-friendly error messages instead of crashing
    - Log all errors with ErrorHandler.logError()
    - Test that no NullPointerException can crash the app
    - _Requirements: 9.3, 9.5, 9.6_
  
  - [ ]* 12.4 Write property tests for error handling
    - **Property 40: Null Inputs Don't Cause Crashes**
    - **Property 41: Network Operations Handle Timeouts**
    - **Validates: Requirements 9.6, 9.7**

- [ ] 13. Verify backward compatibility
  - [ ] 13.1 Test existing Firebase Authentication flows
    - Verify login with existing user accounts works
    - Verify registration creates accounts with same structure
    - Verify logout clears authentication state
    - Ensure no changes to FirebaseAuth usage
    - _Requirements: 9.1, 9.4_
  
  - [ ] 13.2 Test existing Firestore data structures
    - Verify all existing queries return expected data
    - Verify new uploads use same collection paths
    - Verify existing content displays correctly
    - Test with existing user accounts and data
    - _Requirements: 9.2_
  
  - [ ]* 13.3 Write property tests for backward compatibility
    - **Property 36: Firebase Auth Flows Remain Functional**
    - **Property 37: Firestore Queries Return Expected Data**
    - **Property 38: Existing Users Can Login**
    - **Validates: Requirements 9.1, 9.2, 9.4**

- [ ] 14. Final checkpoint and integration testing
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key milestones
- Property tests validate universal correctness properties from the design document
- Unit tests validate specific examples and edge cases
- All implementations maintain backward compatibility with existing Firebase structures
- The implementation uses Kotlin with existing Android SDK, Firebase SDK, and Gemini API
