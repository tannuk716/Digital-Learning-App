package com.tannu.edureach.utils

import android.util.Log
import com.tannu.edureach.BuildConfig
import java.net.UnknownHostException
import java.net.SocketTimeoutException
import java.io.IOException

object GeminiApiHelper {
    
    private const val TAG = "GeminiApiHelper"
    
    suspend fun generateContent(prompt: String): Result<String> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        Log.d(TAG, "Starting API call...")
        Log.d(TAG, "API Key length: ${apiKey.length}")
        
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
                
                // Check for API error in response body
                if (body.error != null) {
                    val errorMsg = body.error.message ?: "Unknown API error"
                    val errorCode = body.error.code ?: 0
                    Log.e(TAG, "API Error: Code=$errorCode, Message=$errorMsg")
                    
                    val userMessage = when (errorCode) {
                        400 -> "Invalid request. Please try again."
                        401, 403 -> "API key issue. Please contact your teacher."
                        404 -> "AI model not available. Please try again later."
                        429 -> "Too many requests. Please wait and try again."
                        500, 503 -> "Google server error. Please try again later."
                        else -> errorMsg
                    }
                    return Result.failure(Exception(userMessage))
                }
                
                // Extract response text
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
                    429 -> "Too many requests. Please wait and try again."
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
