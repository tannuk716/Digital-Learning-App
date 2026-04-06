package com.tannu.edureach

data class ChatMessage(
    val message: String,
    val isSender: Boolean, // true if Student, false if AI
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val originalQuery: String = ""
)
