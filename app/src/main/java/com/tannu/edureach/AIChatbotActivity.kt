package com.tannu.edureach

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.tannu.edureach.R
import com.tannu.edureach.BuildConfig
import com.tannu.edureach.utils.ErrorHandler
import kotlinx.coroutines.launch

class AIChatbotActivity : AppCompatActivity() {

    private lateinit var rvChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aichatbot)

        rvChat = findViewById(R.id.rvChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        chatAdapter = ChatAdapter(messages) { originalQuery ->

            val errorIdx = messages.indexOfLast { it.isError }
            if (errorIdx != -1) {
                messages.removeAt(errorIdx)
                chatAdapter.notifyItemRemoved(errorIdx)
            }

            simulateAIResponse(originalQuery)
        }
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = chatAdapter

        if (!validateGeminiApiKey()) {
            return
        }

        addMessage(ChatMessage("Hello! I am your AI Tutor. I can help you with math, science, english, or any other subject. Ask me anything!", false))

        btnSend.setOnClickListener {
            val query = etMessage.text.toString().trim()
            if (query.isNotEmpty()) {
                addMessage(ChatMessage(query, true))
                etMessage.text.clear()
                simulateAIResponse(query)
            }
        }
    }

    private fun addMessage(chatMessage: ChatMessage) {
        messages.add(chatMessage)
        chatAdapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    

    private fun validateGeminiApiKey(): Boolean {
        val apiKey = BuildConfig.GEMINI_API_KEY
        

        if (apiKey.isNullOrEmpty() || 
            apiKey == "YOUR_API_KEY_HERE" || 
            apiKey == "your_api_key_here" ||
            apiKey.length < 20) {
            

            addMessage(ChatMessage(
                "AI Tutor is temporarily unavailable. Please contact your teacher",
                isSender = false,
                isError = true
            ))
            

            btnSend.isEnabled = false
            etMessage.isEnabled = false
            

            ErrorHandler.logError(
                "AIChatbotActivity",
                "Invalid or missing Gemini API key",
                null
            )
            
            return false
        }
        
        return true
    }

    private fun simulateAIResponse(query: String) {

        val typingMessage = ChatMessage("Thinking...", isSender = false, isLoading = true)
        addMessage(typingMessage)
        val typingIdx = messages.size - 1
        
        lifecycleScope.launch {
            try {
                val prefs = getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
                val userClass = prefs.getInt("USER_CLASS", 1)
                val lang = prefs.getString("APP_LANG", "en") ?: "en"
                

                val promptText = "You are a helpful educational AI tutor for rural students in Class $userClass. " +
                        "Answer simply and clearly in the language code '$lang'. If the question is about Math, Science, " +
                        "English, or General Knowledge, provide a concise, easy-to-understand explanation appropriate for their age. " +
                        "Question: $query"

                val result = com.tannu.edureach.utils.GeminiApiHelper.generateContent(promptText)

                if (typingIdx < messages.size) {
                    messages.removeAt(typingIdx)
                    chatAdapter.notifyItemRemoved(typingIdx)
                }

                result.onSuccess { replyText ->
                    addMessage(ChatMessage(replyText, false))
                }.onFailure { exception ->
                    android.util.Log.e("AIChatbot", "Error: ${exception.message}", exception)
                    
                    val errorMessage = when {
                        exception.message?.contains("Daily AI usage limit", ignoreCase = true) == true -> 
                            exception.message ?: "Daily limit reached"
                        exception.message?.contains("internet", ignoreCase = true) == true -> 
                            "No internet connection. Please check your network and try again."
                        exception.message?.contains("timeout", ignoreCase = true) == true -> 
                            "Request timed out. Please try again."
                        exception.message?.contains("404") == true -> 
                            "AI service temporarily unavailable. Please try again later."
                        exception.message?.contains("401") == true || exception.message?.contains("403") == true -> 
                            "API key issue. Please contact your teacher."
                        exception.message?.contains("quota", ignoreCase = true) == true -> 
                            "Daily AI usage limit reached. The AI Tutor will be available again tomorrow. You can continue using other features of the app."
                        exception.message?.contains("429") == true -> 
                            "Too many requests at once. Please wait a minute and try again."
                        else -> exception.message ?: "An error occurred. Please try again."
                    }
                    
                    addMessage(ChatMessage(errorMessage, isSender = false, isError = true, originalQuery = query))
                }
            } catch (e: java.net.UnknownHostException) {
                android.util.Log.e("AIChatbot", "No internet connection", e)
                if (typingIdx < messages.size) {
                    messages.removeAt(typingIdx)
                    chatAdapter.notifyItemRemoved(typingIdx)
                }
                addMessage(ChatMessage("No internet connection. Please check your network and try again.", isSender = false, isError = true, originalQuery = query))
            } catch (e: java.net.SocketTimeoutException) {
                android.util.Log.e("AIChatbot", "Request timeout", e)
                if (typingIdx < messages.size) {
                    messages.removeAt(typingIdx)
                    chatAdapter.notifyItemRemoved(typingIdx)
                }
                addMessage(ChatMessage("Request timed out. Please try again.", isSender = false, isError = true, originalQuery = query))
            } catch (e: Exception) {
                android.util.Log.e("AIChatbot", "Unexpected error", e)
                if (typingIdx < messages.size) {
                    messages.removeAt(typingIdx)
                    chatAdapter.notifyItemRemoved(typingIdx)
                }
                addMessage(ChatMessage("An error occurred: ${e.message}. Please try again.", isSender = false, isError = true, originalQuery = query))
            }
        }
    }
}