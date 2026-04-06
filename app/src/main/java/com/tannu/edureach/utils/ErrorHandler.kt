package com.tannu.edureach.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ErrorHandler - Centralized error handling utility
 * 
 * Validates: Requirements 4.6, 9.5
 * 
 * This object provides centralized error handling with user-friendly messages
 * for Gemini API errors, network errors, and Firebase exceptions.
 */
object ErrorHandler {
    
    private const val TAG = "ErrorHandler"
    
    /**
     * Handles Gemini API errors with context and retry callback
     * 
     * @param context Android context for resource access
     * @param exception The exception that occurred
     * @param onRetry Callback function to retry the operation
     * @return User-friendly error message
     */
    fun handleGeminiError(context: Context, exception: Exception, onRetry: () -> Unit): String {
        logError(TAG, "Gemini API error occurred", exception)
        
        return when (exception) {
            is SocketTimeoutException -> {
                "Request timed out. Please check your connection."
            }
            is UnknownHostException -> {
                "No internet connection. Please check your network."
            }
            else -> {
                val message = exception.message ?: ""
                when {
                    message.contains("API key", ignoreCase = true) -> {
                        "AI Tutor is temporarily unavailable. Please contact your teacher"
                    }
                    message.contains("quota", ignoreCase = true) -> {
                        "AI Tutor usage limit reached. Please try again later."
                    }
                    message.contains("401") || message.contains("403") -> {
                        "AI Tutor is temporarily unavailable. Please contact your teacher"
                    }
                    else -> {
                        "Unable to connect to AI Tutor. Please try again."
                    }
                }
            }
        }
    }
    
    /**
     * Handles network errors for general network failures
     * 
     * @param context Android context for resource access
     * @param exception The network exception that occurred
     * @return User-friendly error message
     */
    fun handleNetworkError(context: Context, exception: Exception): String {
        logError(TAG, "Network error occurred", exception)
        
        return when (exception) {
            is SocketTimeoutException -> {
                "Request timed out. Please check your connection."
            }
            is UnknownHostException -> {
                "No internet connection. Please check your network."
            }
            else -> {
                val message = exception.message ?: ""
                when {
                    message.contains("timeout", ignoreCase = true) -> {
                        "Request timed out. Please check your connection."
                    }
                    message.contains("connection", ignoreCase = true) -> {
                        "Connection failed. Please check your network."
                    }
                    else -> {
                        "Network error occurred. Please try again."
                    }
                }
            }
        }
    }
    
    /**
     * Handles Firebase exceptions (Auth and Firestore)
     * 
     * @param context Android context for resource access
     * @param exception The Firebase exception that occurred
     * @return User-friendly error message
     */
    fun handleFirebaseError(context: Context, exception: Exception): String {
        logError(TAG, "Firebase error occurred", exception)
        
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email address. Please check and try again."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again."
                    "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use a stronger password."
                    "ERROR_USER_DISABLED" -> "This account has been disabled."
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection."
                    else -> "Login failed. Please check your credentials and try again"
                }
            }
            is FirebaseFirestoreException -> {
                when (exception.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                        "Access denied. Please check your permissions."
                    }
                    FirebaseFirestoreException.Code.NOT_FOUND -> {
                        "Content not found. It may have been removed."
                    }
                    FirebaseFirestoreException.Code.UNAVAILABLE -> {
                        "Service temporarily unavailable. Please try again later."
                    }
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> {
                        "Request timed out. Please try again."
                    }
                    else -> {
                        "An error occurred. Please try again."
                    }
                }
            }
            is SecurityException -> {
                "Download failed. Please check your storage permissions and try again"
            }
            else -> {
                val message = exception.message ?: ""
                when {
                    message.contains("permission", ignoreCase = true) -> {
                        "Permission denied. Please check your access rights."
                    }
                    message.contains("not found", ignoreCase = true) -> {
                        "Content not found. It may have been removed."
                    }
                    message.contains("network", ignoreCase = true) -> {
                        "Network error. Please check your connection."
                    }
                    else -> {
                        "An error occurred. Please try again."
                    }
                }
            }
        }
    }
    
    /**
     * Logs error details for debugging and monitoring
     * 
     * @param tag Log tag for categorization
     * @param message Descriptive error message
     * @param exception The exception to log (optional)
     */
    fun logError(tag: String, message: String, exception: Exception?) {
        if (exception != null) {
            Log.e(tag, message, exception)
        } else {
            Log.e(tag, message)
        }
        
        // Optional: Send to Firebase Crashlytics for production monitoring
        // FirebaseCrashlytics.getInstance().recordException(exception ?: Exception(message))
    }
}
