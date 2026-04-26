package com.tannu.edureach.utils

import android.util.Log
import com.tannu.edureach.BuildConfig
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.io.IOException

object GeminiApiHelper {
    
    private const val TAG = "GeminiApiHelper"
    private const val PREFS_NAME = "GeminiApiPrefs"
    private const val KEY_LAST_QUOTA_ERROR = "last_quota_error_time"
    private const val KEY_QUOTA_ERROR_COUNT = "quota_error_count"
    private const val QUOTA_COOLDOWN_MS = 60000L
    
    suspend fun generateContent(prompt: String): Result<String> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE" || apiKey.length < 20) {
            Log.e(TAG, "Invalid API key detected in BuildConfig")
            return Result.failure(Exception("API configuration error. Please reinstall the app or contact support."))
        }
        
        Log.d(TAG, "Starting API call...")
        Log.d(TAG, "API Key present: ${apiKey.isNotEmpty()}, Length: ${apiKey.length}, Prefix: ${apiKey.take(5)}")
        
        val request = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7,
                maxOutputTokens = 1024
            )
        )
        
        return try {
            val service = RetrofitClient.geminiApi
            val response = service.generateContent(apiKey, request)
            
            Log.d(TAG, "Response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                

                if (body.error != null) {
                    val errorMsg = body.error.message ?: "Unknown API error"
                    val errorCode = body.error.code ?: 0
                    Log.e(TAG, "API Error: Code=$errorCode, Message=$errorMsg")
                    
                    val userMessage = when (errorCode) {
                        400 -> "Invalid request. Please try again."
                        401, 403 -> "API key issue. Please contact your teacher."
                        404 -> "AI model not available. Please try again later."
                        429 -> {
                            if (errorMsg.contains("quota", ignoreCase = true)) {
                                "Daily AI usage limit reached. The AI Tutor will be available again tomorrow. You can continue using other features of the app."
                            } else {
                                "Too many requests at once. Please wait a minute and try again."
                            }
                        }
                        500, 503 -> "Google server error. Please try again later."
                        else -> errorMsg
                    }
                    return Result.failure(Exception(userMessage))
                }
                

                val replyText = body.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!replyText.isNullOrEmpty()) {
                    Log.d(TAG, "✓ Success! Response length: ${replyText.length}")
                    return Result.success(replyText)
                } else {
                    Log.e(TAG, "Empty response from API")
                    return Result.failure(Exception("AI returned an empty response. Please try again."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP ${response.code()}: $errorBody")
                
                val errorMsg = when (response.code()) {
                    400 -> "Invalid request. Please try again."
                    401, 403 -> "API key issue. Please contact your teacher."
                    404 -> "AI model not available. Please try again later."
                    429 -> {
                        if (errorBody?.contains("quota", ignoreCase = true) == true) {
                            "Daily AI usage limit reached. The AI Tutor will be available again tomorrow. You can continue using other features of the app."
                        } else {
                            "Too many requests at once. Please wait a minute and try again."
                        }
                    }
                    500, 503 -> "Google server error. Please try again later."
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                return Result.failure(Exception(errorMsg))
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Network error: Cannot resolve host", e)
            Result.failure(Exception("No internet connection. Please check your network and try again."))
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Request timeout", e)
            Result.failure(Exception("Request timed out. Please check your internet connection and try again."))
        } catch (e: IOException) {
            Log.e(TAG, "IO Exception", e)
            Result.failure(Exception("Network error. Please check your connection and try again."))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error: ${e.javaClass.simpleName}", e)
            Result.failure(Exception("An unexpected error occurred: ${e.message}"))
        }
    }
}