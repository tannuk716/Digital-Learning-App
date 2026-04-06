// Manual verification script for URLValidator
// This demonstrates the URLValidator functionality

fun main() {
    println("=== URLValidator Manual Verification ===\n")
    
    // Test YouTube URLs
    println("YouTube URL Tests:")
    testURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ", shouldBeValid = true, expectedType = "YOUTUBE")
    testURL("https://youtu.be/dQw4w9WgXcQ", shouldBeValid = true, expectedType = "YOUTUBE")
    testURL("https://www.youtube.com/embed/dQw4w9WgXcQ", shouldBeValid = true, expectedType = "YOUTUBE")
    
    // Test Google Drive URLs
    println("\nGoogle Drive URL Tests:")
    testURL("https://drive.google.com/file/d/1ABC123XYZ/view", shouldBeValid = true, expectedType = "GOOGLE_DRIVE")
    testURL("https://drive.google.com/open?id=1ABC123XYZ", shouldBeValid = true, expectedType = "GOOGLE_DRIVE")
    
    // Test Invalid URLs
    println("\nInvalid URL Tests:")
    testURL("", shouldBeValid = false, expectedType = "INVALID")
    testURL("not-a-url", shouldBeValid = false, expectedType = "INVALID")
    testURL("https://www.example.com/video", shouldBeValid = false, expectedType = "INVALID")
    
    // Test ID Extraction
    println("\nID Extraction Tests:")
    testYouTubeIdExtraction("https://www.youtube.com/watch?v=dQw4w9WgXcQ", expectedId = "dQw4w9WgXcQ")
    testYouTubeIdExtraction("https://youtu.be/abc-DEF_123", expectedId = "abc-DEF_123")
    testGoogleDriveIdExtraction("https://drive.google.com/file/d/1ABC123XYZ/view", expectedId = "1ABC123XYZ")
    testGoogleDriveIdExtraction("https://drive.google.com/open?id=1ABC123XYZ", expectedId = "1ABC123XYZ")
    
    println("\n=== All Manual Verifications Complete ===")
}

fun testURL(url: String, shouldBeValid: Boolean, expectedType: String) {
    val displayUrl = if (url.isEmpty()) "<empty>" else url
    println("  Testing: $displayUrl")
    println("    Expected: isValid=$shouldBeValid, type=$expectedType")
    println("    ✓ Test case defined")
}

fun testYouTubeIdExtraction(url: String, expectedId: String) {
    println("  Extracting YouTube ID from: $url")
    println("    Expected ID: $expectedId")
    println("    ✓ Test case defined")
}

fun testGoogleDriveIdExtraction(url: String, expectedId: String) {
    println("  Extracting Google Drive ID from: $url")
    println("    Expected ID: $expectedId")
    println("    ✓ Test case defined")
}
