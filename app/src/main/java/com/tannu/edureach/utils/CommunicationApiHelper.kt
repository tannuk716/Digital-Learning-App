package com.tannu.edureach.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CommunicationApiHelper {
    
    private val apiKey = "20889e96f7214928b7d63c44fa7cfd7b"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }
            
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$baseUrl?key=$apiKey")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            if (response.isSuccessful) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val content = candidates.getJSONObject(0)
                        .getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
            }
            
            Log.e("CommunicationAPI", "API Error: ${response.code} - $responseBody")
            throw Exception("API request failed: ${response.code}")
            
        } catch (e: Exception) {
            Log.e("CommunicationAPI", "Error: ${e.message}", e)
            throw e
        }
    }
}