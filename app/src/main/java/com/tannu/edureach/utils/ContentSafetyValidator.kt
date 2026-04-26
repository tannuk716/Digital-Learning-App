package com.tannu.edureach.utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.withTimeout
import java.net.URL

class ContentSafetyValidator(private val context: Context) {
    
    companion object {
        private const val TAG = "ContentSafetyValidator"
        private const val VALIDATION_TIMEOUT_MS = 10000L
        
        private val inappropriateKeywords = setOf(
            "violent", "violence", "kill", "murder", "blood", "gore", "weapon",
            "sexual", "porn", "xxx", "adult", "explicit", "nude", "sex"
        )
    }
    
    

    data class SafetyResult(
        val isSafe: Boolean,
        val reason: String? = null,
        val confidence: Float = 0f
    )
    
    

    suspend fun validateContent(url: String): SafetyResult {
        return try {

            withTimeout(VALIDATION_TIMEOUT_MS) {
                performValidation(url)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            Log.w(TAG, "Content validation timed out for URL: $url", e)

            SafetyResult(
                isSafe = true,
                reason = "Unable to verify content safety. Please review manually before submitting",
                confidence = 0.0f
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during content validation for URL: $url", e)

            SafetyResult(
                isSafe = true,
                reason = "Unable to verify content safety. Please review manually before submitting",
                confidence = 0.0f
            )
        }
    }
    
    

    private fun performValidation(url: String): SafetyResult {

        val domain = extractDomain(url)
        if (domain == null) {
            return SafetyResult(
                isSafe = false,
                reason = "Content blocked: Invalid URL format",
                confidence = 1.0f
            )
        }
        
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
        

        return SafetyResult(
            isSafe = true,
            reason = null,
            confidence = 1.0f
        )
    }
    
    

    private fun extractDomain(url: String): String? {
        return try {
            val urlObj = URL(url)
            urlObj.host
        } catch (e: Exception) {
            Log.e(TAG, "Failed to extract domain from URL: $url", e)
            null
        }
    }
    
    

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