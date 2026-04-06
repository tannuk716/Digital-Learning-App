package com.tannu.edureach.utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.withTimeout
import java.net.URL

/**
 * ContentSafetyValidator - Utility class for validating content safety of URLs
 * 
 * Validates: Requirements 3.1, 3.2, 3.3, 3.5
 * 
 * This class provides content safety validation for URLs submitted by teachers,
 * checking for inappropriate content indicators and maintaining an audit log.
 */
class ContentSafetyValidator(private val context: Context) {
    
    companion object {
        private const val TAG = "ContentSafetyValidator"
        private const val VALIDATION_TIMEOUT_MS = 10000L // 10 seconds
        
        // Educational domain allowlist
        private val allowedDomains = setOf(
            "youtube.com",
            "www.youtube.com",
            "youtu.be",
            "drive.google.com"
        )
        
        // Inappropriate content keywords (violence and sexual content)
        private val inappropriateKeywords = setOf(
            "violent", "violence", "kill", "murder", "blood", "gore", "weapon",
            "sexual", "porn", "xxx", "adult", "explicit", "nude", "sex"
        )
    }
    
    /**
     * Result of content safety validation
     */
    data class SafetyResult(
        val isSafe: Boolean,
        val reason: String? = null,
        val confidence: Float = 0f
    )
    
    /**
     * Validates content safety of a URL with timeout handling
     * 
     * @param url The URL to validate
     * @return SafetyResult indicating if content is safe, with reason and confidence
     */
    suspend fun validateContent(url: String): SafetyResult {
        return try {
            // Apply 10-second timeout as per requirement 3.3
            withTimeout(VALIDATION_TIMEOUT_MS) {
                performValidation(url)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.w(TAG, "Content validation timed out for URL: $url", e)
            // Return warning result on timeout (requirement 3.3)
            SafetyResult(
                isSafe = true, // Allow with warning
                reason = "Unable to verify content safety. Please review manually before submitting",
                confidence = 0.0f
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during content validation for URL: $url", e)
            // On error, return warning result
            SafetyResult(
                isSafe = true, // Allow with warning
                reason = "Unable to verify content safety. Please review manually before submitting",
                confidence = 0.0f
            )
        }
    }
    
    /**
     * Performs the actual content validation logic
     */
    private fun performValidation(url: String): SafetyResult {
        // Step 1: Domain allowlist check (requirement 3.1)
        val domain = extractDomain(url)
        if (domain == null) {
            return SafetyResult(
                isSafe = false,
                reason = "Content blocked: Invalid URL format",
                confidence = 1.0f
            )
        }
        
        // Check if domain is in allowlist
        val isAllowedDomain = allowedDomains.any { allowedDomain ->
            domain.equals(allowedDomain, ignoreCase = true) || 
            domain.endsWith(".$allowedDomain", ignoreCase = true)
        }
        
        if (!isAllowedDomain) {
            return SafetyResult(
                isSafe = false,
                reason = "Content blocked: Only YouTube and Google Drive links are allowed",
                confidence = 1.0f
            )
        }
        
        // Step 2: URL pattern analysis - check for inappropriate keywords (requirement 3.2)
        val urlLowerCase = url.lowercase()
        val foundKeywords = inappropriateKeywords.filter { keyword ->
            urlLowerCase.contains(keyword)
        }
        
        if (foundKeywords.isNotEmpty()) {
            Log.w(TAG, "Inappropriate keywords found in URL: $foundKeywords")
            return SafetyResult(
                isSafe = false,
                reason = "Content blocked: This link contains inappropriate material",
                confidence = 0.9f
            )
        }
        
        // Step 3: Keyword detection in URL path and query parameters (requirement 3.2)
        val pathAndQuery = extractPathAndQuery(url)
        if (pathAndQuery != null) {
            val pathLowerCase = pathAndQuery.lowercase()
            val foundInPath = inappropriateKeywords.filter { keyword ->
                pathLowerCase.contains(keyword)
            }
            
            if (foundInPath.isNotEmpty()) {
                Log.w(TAG, "Inappropriate keywords found in URL path: $foundInPath")
                return SafetyResult(
                    isSafe = false,
                    reason = "Content blocked: This link contains inappropriate material",
                    confidence = 0.85f
                )
            }
        }
        
        // All checks passed - content is safe (requirement 3.4)
        return SafetyResult(
            isSafe = true,
            reason = null,
            confidence = 1.0f
        )
    }
    
    /**
     * Extracts the domain from a URL
     */
    private fun extractDomain(url: String): String? {
        return try {
            val urlObj = URL(url)
            urlObj.host
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract domain from URL: $url", e)
            null
        }
    }
    
    /**
     * Extracts the path and query parameters from a URL
     */
    private fun extractPathAndQuery(url: String): String? {
        return try {
            val urlObj = URL(url)
            val path = urlObj.path ?: ""
            val query = urlObj.query ?: ""
            "$path?$query"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract path and query from URL: $url", e)
            null
        }
    }
    
    /**
     * Logs a blocked submission to Firestore for audit purposes (requirement 3.5)
     * 
     * @param userId The ID of the user who attempted the submission
     * @param url The blocked URL
     * @param reason The reason for blocking
     */
    fun logBlockedSubmission(userId: String, url: String, reason: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val logEntry = hashMapOf(
                "userId" to userId,
                "url" to url,
                "blockedReason" to reason,
                "timestamp" to System.currentTimeMillis()
            )
            
            db.collection("content_validation_logs")
                .add(logEntry)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Blocked submission logged with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to log blocked submission", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error logging blocked submission", e)
        }
    }
}
