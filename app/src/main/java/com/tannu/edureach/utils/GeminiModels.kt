package com.tannu.edureach.utils

data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = GenerationConfig()
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Double = 0.7,
    val topK: Int = 40,
    val topP: Double = 0.95,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<Candidate>?,
    val error: GeminiError?
)

data class Candidate(
    val content: Content?,
    val finishReason: String?
)

data class GeminiError(
    val code: Int?,
    val message: String?,
    val status: String?
)