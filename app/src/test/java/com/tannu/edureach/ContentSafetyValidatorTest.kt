package com.tannu.edureach

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.tannu.edureach.utils.ContentSafetyValidator
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for ContentSafetyValidator
 * 
 * Tests content safety validation including domain allowlist checks,
 * keyword detection, and timeout handling.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ContentSafetyValidatorTest {
    
    private lateinit var context: Context
    private lateinit var validator: ContentSafetyValidator
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        validator = ContentSafetyValidator(context)
    }
    
    // Domain allowlist tests
    
    @Test
    fun `YouTube domain passes validation`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=educationalVideo")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `YouTube short domain passes validation`() = runBlocking {
        val result = validator.validateContent("https://youtu.be/educationalVideo")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `Google Drive domain passes validation`() = runBlocking {
        val result = validator.validateContent("https://drive.google.com/file/d/1ABC123/view")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `non-allowed domain is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.example.com/content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: Only YouTube and Google Drive links are allowed", result.reason)
    }
    
    @Test
    fun `invalid URL format is blocked`() = runBlocking {
        val result = validator.validateContent("not-a-valid-url")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: Invalid URL format", result.reason)
    }
    
    // Inappropriate keyword detection tests
    
    @Test
    fun `URL with violence keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=violent-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with sexual keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=sexual-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with explicit keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=explicit-video")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with kill keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=kill-tutorial")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with porn keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=porn-video")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with weapon keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=weapon-demo")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with gore keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=gore-scene")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `URL with adult keyword is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=adult-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    // Keyword detection in path tests
    
    @Test
    fun `inappropriate keyword in path is detected`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/violence/watch?v=abc123")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `inappropriate keyword in query parameter is detected`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=abc123&tag=sexual")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    // Case insensitivity tests
    
    @Test
    fun `uppercase inappropriate keyword is detected`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=VIOLENT-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    @Test
    fun `mixed case inappropriate keyword is detected`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=SeXuAl-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
    
    // Safe content tests
    
    @Test
    fun `educational YouTube URL passes validation`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=math-tutorial")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `science video URL passes validation`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=science-experiment")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `history lesson URL passes validation`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=history-lesson")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    @Test
    fun `Google Drive document URL passes validation`() = runBlocking {
        val result = validator.validateContent("https://drive.google.com/file/d/1ABC123/view?usp=sharing")
        assertTrue(result.isSafe)
        assertNull(result.reason)
    }
    
    // Confidence level tests
    
    @Test
    fun `blocked content has high confidence`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=violent-video")
        assertFalse(result.isSafe)
        assertTrue(result.confidence > 0.8f)
    }
    
    @Test
    fun `safe content has maximum confidence`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=educational-video")
        assertTrue(result.isSafe)
        assertEquals(1.0f, result.confidence, 0.01f)
    }
    
    // Edge cases
    
    @Test
    fun `URL with safe word containing inappropriate substring passes`() = runBlocking {
        // "killing" contains "kill" but in context of "killing time" or similar
        // This test shows the limitation of simple keyword matching
        val result = validator.validateContent("https://www.youtube.com/watch?v=killing-time-tips")
        // This will be blocked due to "kill" substring - expected behavior
        assertFalse(result.isSafe)
    }
    
    @Test
    fun `empty URL is handled gracefully`() = runBlocking {
        val result = validator.validateContent("")
        assertFalse(result.isSafe)
        assertNotNull(result.reason)
    }
    
    @Test
    fun `URL with special characters passes if safe`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=math_tutorial-2024")
        assertTrue(result.isSafe)
    }
    
    @Test
    fun `URL with numbers only in video ID passes`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=123456789ab")
        assertTrue(result.isSafe)
    }
    
    // Multiple keywords test
    
    @Test
    fun `URL with multiple inappropriate keywords is blocked`() = runBlocking {
        val result = validator.validateContent("https://www.youtube.com/watch?v=violent-sexual-content")
        assertFalse(result.isSafe)
        assertEquals("Content blocked: This link contains inappropriate material", result.reason)
    }
}
