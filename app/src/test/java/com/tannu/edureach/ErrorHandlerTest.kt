package com.tannu.edureach

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.tannu.edureach.utils.ErrorHandler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Unit tests for ErrorHandler utility
 * 
 * Tests user-friendly error message generation for various error scenarios
 */
@RunWith(RobolectricTestRunner::class)
class ErrorHandlerTest {
    
    private lateinit var context: Context
    
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }
    
    @Test
    fun `handleGeminiError returns timeout message for SocketTimeoutException`() {
        val exception = SocketTimeoutException("Connection timed out")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("Request timed out. Please check your connection.", message)
    }
    
    @Test
    fun `handleGeminiError returns no internet message for UnknownHostException`() {
        val exception = UnknownHostException("Unable to resolve host")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("No internet connection. Please check your network.", message)
    }
    
    @Test
    fun `handleGeminiError returns unavailable message for API key error`() {
        val exception = Exception("Invalid API key provided")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("AI Tutor is temporarily unavailable. Please contact your teacher", message)
    }
    
    @Test
    fun `handleGeminiError returns quota message for quota exceeded error`() {
        val exception = Exception("Quota exceeded for this API key")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("AI Tutor usage limit reached. Please try again later.", message)
    }
    
    @Test
    fun `handleGeminiError returns unavailable message for 401 error`() {
        val exception = Exception("HTTP 401 Unauthorized")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("AI Tutor is temporarily unavailable. Please contact your teacher", message)
    }
    
    @Test
    fun `handleGeminiError returns generic message for unknown error`() {
        val exception = Exception("Unknown error occurred")
        val onRetry = {}
        
        val message = ErrorHandler.handleGeminiError(context, exception, onRetry)
        
        assertEquals("Unable to connect to AI Tutor. Please try again.", message)
    }
    
    @Test
    fun `handleNetworkError returns timeout message for SocketTimeoutException`() {
        val exception = SocketTimeoutException("Read timed out")
        
        val message = ErrorHandler.handleNetworkError(context, exception)
        
        assertEquals("Request timed out. Please check your connection.", message)
    }
    
    @Test
    fun `handleNetworkError returns no internet message for UnknownHostException`() {
        val exception = UnknownHostException("Unable to resolve host")
        
        val message = ErrorHandler.handleNetworkError(context, exception)
        
        assertEquals("No internet connection. Please check your network.", message)
    }
    
    @Test
    fun `handleNetworkError returns timeout message for timeout in message`() {
        val exception = Exception("Connection timeout occurred")
        
        val message = ErrorHandler.handleNetworkError(context, exception)
        
        assertEquals("Request timed out. Please check your connection.", message)
    }
    
    @Test
    fun `handleNetworkError returns connection failed message for connection error`() {
        val exception = Exception("Connection refused")
        
        val message = ErrorHandler.handleNetworkError(context, exception)
        
        assertEquals("Connection failed. Please check your network.", message)
    }
    
    @Test
    fun `handleNetworkError returns generic message for unknown network error`() {
        val exception = Exception("Unknown network error")
        
        val message = ErrorHandler.handleNetworkError(context, exception)
        
        assertEquals("Network error occurred. Please try again.", message)
    }
    
    @Test
    fun `handleFirebaseError returns permission denied message for SecurityException`() {
        val exception = SecurityException("Permission denied")
        
        val message = ErrorHandler.handleFirebaseError(context, exception)
        
        assertEquals("Download failed. Please check your storage permissions and try again", message)
    }
    
    @Test
    fun `handleFirebaseError returns not found message for not found in message`() {
        val exception = Exception("Document not found in database")
        
        val message = ErrorHandler.handleFirebaseError(context, exception)
        
        assertEquals("Content not found. It may have been removed.", message)
    }
    
    @Test
    fun `handleFirebaseError returns network error message for network in message`() {
        val exception = Exception("Network request failed")
        
        val message = ErrorHandler.handleFirebaseError(context, exception)
        
        assertEquals("Network error. Please check your connection.", message)
    }
    
    @Test
    fun `handleFirebaseError returns permission message for permission in message`() {
        val exception = Exception("Permission denied to access resource")
        
        val message = ErrorHandler.handleFirebaseError(context, exception)
        
        assertEquals("Permission denied. Please check your access rights.", message)
    }
    
    @Test
    fun `handleFirebaseError returns generic message for unknown Firebase error`() {
        val exception = Exception("Unknown Firebase error")
        
        val message = ErrorHandler.handleFirebaseError(context, exception)
        
        assertEquals("An error occurred. Please try again.", message)
    }
    
    @Test
    fun `logError handles null exception gracefully`() {
        // Should not throw exception
        ErrorHandler.logError("TestTag", "Test message", null)
        
        // Test passes if no exception is thrown
        assertTrue(true)
    }
    
    @Test
    fun `logError handles non-null exception gracefully`() {
        val exception = Exception("Test exception")
        
        // Should not throw exception
        ErrorHandler.logError("TestTag", "Test message", exception)
        
        // Test passes if no exception is thrown
        assertTrue(true)
    }
}
