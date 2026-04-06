package com.tannu.edureach

import com.tannu.edureach.utils.URLValidator
import com.tannu.edureach.utils.URLValidator.URLType
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for URLValidator
 * 
 * Tests validation of YouTube and Google Drive URLs,
 * as well as extraction of video IDs and file IDs.
 */
class URLValidatorTest {
    
    // YouTube URL validation tests
    
    @Test
    fun `valid YouTube watch URL is recognized`() {
        val result = URLValidator.validateURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun `valid YouTube short URL is recognized`() {
        val result = URLValidator.validateURL("https://youtu.be/dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun `valid YouTube embed URL is recognized`() {
        val result = URLValidator.validateURL("https://www.youtube.com/embed/dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun `YouTube URL with additional parameters is valid`() {
        val result = URLValidator.validateURL("https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10s")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
    }
    
    @Test
    fun `YouTube URL without www is valid`() {
        val result = URLValidator.validateURL("https://youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
    }
    
    // Google Drive URL validation tests
    
    @Test
    fun `valid Google Drive file URL is recognized`() {
        val result = URLValidator.validateURL("https://drive.google.com/file/d/1ABC123XYZ/view")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun `valid Google Drive open URL is recognized`() {
        val result = URLValidator.validateURL("https://drive.google.com/open?id=1ABC123XYZ")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
        assertNull(result.errorMessage)
    }
    
    @Test
    fun `Google Drive URL with additional parameters is valid`() {
        val result = URLValidator.validateURL("https://drive.google.com/file/d/1ABC123XYZ/view?usp=sharing")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
    }
    
    // Invalid URL tests
    
    @Test
    fun `empty URL returns invalid result`() {
        val result = URLValidator.validateURL("")
        assertFalse(result.isValid)
        assertEquals(URLType.INVALID, result.urlType)
        assertEquals("URL cannot be empty", result.errorMessage)
    }
    
    @Test
    fun `blank URL returns invalid result`() {
        val result = URLValidator.validateURL("   ")
        assertFalse(result.isValid)
        assertEquals(URLType.INVALID, result.urlType)
        assertEquals("URL cannot be empty", result.errorMessage)
    }
    
    @Test
    fun `random text returns invalid result`() {
        val result = URLValidator.validateURL("not-a-url")
        assertFalse(result.isValid)
        assertEquals(URLType.INVALID, result.urlType)
        assertEquals("Invalid URL format. Please enter a valid YouTube or Google Drive link", result.errorMessage)
    }
    
    @Test
    fun `invalid domain returns invalid result`() {
        val result = URLValidator.validateURL("https://www.example.com/video")
        assertFalse(result.isValid)
        assertEquals(URLType.INVALID, result.urlType)
        assertNotNull(result.errorMessage)
    }
    
    @Test
    fun `malformed YouTube URL returns invalid result`() {
        val result = URLValidator.validateURL("https://www.youtube.com/invalid")
        assertFalse(result.isValid)
        assertEquals(URLType.INVALID, result.urlType)
    }
    
    // YouTube video ID extraction tests
    
    @Test
    fun `extractYouTubeVideoId extracts from watch URL`() {
        val videoId = URLValidator.extractYouTubeVideoId("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertEquals("dQw4w9WgXcQ", videoId)
    }
    
    @Test
    fun `extractYouTubeVideoId extracts from short URL`() {
        val videoId = URLValidator.extractYouTubeVideoId("https://youtu.be/dQw4w9WgXcQ")
        assertEquals("dQw4w9WgXcQ", videoId)
    }
    
    @Test
    fun `extractYouTubeVideoId extracts from embed URL`() {
        val videoId = URLValidator.extractYouTubeVideoId("https://www.youtube.com/embed/dQw4w9WgXcQ")
        assertEquals("dQw4w9WgXcQ", videoId)
    }
    
    @Test
    fun `extractYouTubeVideoId returns null for invalid URL`() {
        val videoId = URLValidator.extractYouTubeVideoId("https://www.example.com/video")
        assertNull(videoId)
    }
    
    @Test
    fun `extractYouTubeVideoId returns null for empty URL`() {
        val videoId = URLValidator.extractYouTubeVideoId("")
        assertNull(videoId)
    }
    
    // Google Drive file ID extraction tests
    
    @Test
    fun `extractGoogleDriveFileId extracts from file URL`() {
        val fileId = URLValidator.extractGoogleDriveFileId("https://drive.google.com/file/d/1ABC123XYZ/view")
        assertEquals("1ABC123XYZ", fileId)
    }
    
    @Test
    fun `extractGoogleDriveFileId extracts from open URL`() {
        val fileId = URLValidator.extractGoogleDriveFileId("https://drive.google.com/open?id=1ABC123XYZ")
        assertEquals("1ABC123XYZ", fileId)
    }
    
    @Test
    fun `extractGoogleDriveFileId returns null for invalid URL`() {
        val fileId = URLValidator.extractGoogleDriveFileId("https://www.example.com/file")
        assertNull(fileId)
    }
    
    @Test
    fun `extractGoogleDriveFileId returns null for empty URL`() {
        val fileId = URLValidator.extractGoogleDriveFileId("")
        assertNull(fileId)
    }
    
    // Edge case tests
    
    @Test
    fun `YouTube URL with special characters in video ID is valid`() {
        val result = URLValidator.validateURL("https://www.youtube.com/watch?v=abc-DEF_123")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
    }
    
    @Test
    fun `Google Drive URL with long file ID is valid`() {
        val result = URLValidator.validateURL("https://drive.google.com/file/d/1ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_/view")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
    }
    
    @Test
    fun `http protocol is accepted for YouTube`() {
        val result = URLValidator.validateURL("http://www.youtube.com/watch?v=dQw4w9WgXcQ")
        assertTrue(result.isValid)
        assertEquals(URLType.YOUTUBE, result.urlType)
    }
    
    @Test
    fun `http protocol is accepted for Google Drive`() {
        val result = URLValidator.validateURL("http://drive.google.com/file/d/1ABC123XYZ/view")
        assertTrue(result.isValid)
        assertEquals(URLType.GOOGLE_DRIVE, result.urlType)
    }
}
