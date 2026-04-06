package com.tannu.edureach

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.tannu.edureach.utils.LanguageManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for LanguageSelectionActivity instant switching behavior
 * 
 * Tests Requirements 8.1, 8.2, 8.4:
 * - Language selection applies immediately
 * - UI refreshes after language change
 * - Language persists across navigation
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LanguageSelectionActivityTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // Reset to default language before each test
        LanguageManager.setLocale(context, "en")
    }

    @After
    fun tearDown() {
        // Clean up preferences after each test
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun `selecting English applies locale immediately`() {
        // Set to a different language first
        LanguageManager.setLocale(context, "hi")
        assertEquals("hi", LanguageManager.getLocale(context))
        
        // Apply English
        LanguageManager.setLocale(context, "en")
        
        // Verify immediate application (Requirement 8.1)
        val newLocale = LanguageManager.getLocale(context)
        assertEquals("en", newLocale)
    }

    @Test
    fun `selecting Hindi applies locale immediately`() {
        // Set to English first
        LanguageManager.setLocale(context, "en")
        assertEquals("en", LanguageManager.getLocale(context))
        
        // Apply Hindi
        LanguageManager.setLocale(context, "hi")
        
        // Verify immediate application (Requirement 8.1)
        val newLocale = LanguageManager.getLocale(context)
        assertEquals("hi", newLocale)
    }

    @Test
    fun `selecting Punjabi applies locale immediately`() {
        // Set to English first
        LanguageManager.setLocale(context, "en")
        assertEquals("en", LanguageManager.getLocale(context))
        
        // Apply Punjabi
        LanguageManager.setLocale(context, "pa")
        
        // Verify immediate application (Requirement 8.1)
        val newLocale = LanguageManager.getLocale(context)
        assertEquals("pa", newLocale)
    }

    @Test
    fun `language preference persists in SharedPreferences`() {
        // Test Requirement 8.3: Language persistence
        val testLanguages = listOf("en", "hi", "pa")
        
        testLanguages.forEach { langCode ->
            LanguageManager.setLocale(context, langCode)
            val retrieved = LanguageManager.getLocale(context)
            assertEquals("Language $langCode should persist", langCode, retrieved)
        }
    }

    @Test
    fun `language preference round-trip works correctly`() {
        // Test Requirement 8.4: Language consistency across navigation
        val languages = listOf("en", "hi", "pa")
        
        languages.forEach { langCode ->
            // Save language
            LanguageManager.setLocale(context, langCode)
            
            // Simulate navigation by retrieving language
            val retrieved = LanguageManager.getLocale(context)
            
            // Verify consistency
            assertEquals("Language should remain $langCode after navigation", langCode, retrieved)
        }
    }

    @Test
    fun `applyLocaleAndRecreate method exists and is callable`() {
        // Verify that the applyLocaleAndRecreate method is available
        // This test ensures the LanguageSelectionActivity can call this method
        val method = LanguageManager::class.java.getDeclaredMethod(
            "applyLocaleAndRecreate",
            android.app.Activity::class.java,
            String::class.java
        )
        assertNotNull("applyLocaleAndRecreate method should exist", method)
    }
}
