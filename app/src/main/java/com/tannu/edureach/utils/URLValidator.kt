package com.tannu.edureach.utils

import android.util.Log

object URLValidator {
    
    data class ValidationResult(
        val isValid: Boolean,
        val urlType: URLType,
        val errorMessage: String? = null
    )
    
    enum class URLType {
        YOUTUBE, GOOGLE_DRIVE, WEB_URL, INVALID
    }
    

    private val youtubePatterns = listOf(
        Regex("""^https?://(?:www\.)?youtube\.com/watch\?.*v=([a-zA-Z0-9_-]{11}).*$"""),
        Regex("""^https?://(?:www\.)?youtu\.be/([a-zA-Z0-9_-]{11})(?:\?.*)?$"""),
        Regex("""^https?://(?:www\.)?youtube\.com/embed/([a-zA-Z0-9_-]{11}).*$""")
    )
    
    private val googleDrivePatterns = listOf(
        Regex("""^https?://drive\.google\.com/file/d/([a-zA-Z0-9_-]+).*$"""),
        Regex("""^https?://drive\.google\.com/open\?id=([a-zA-Z0-9_-]+).*$""")
    )
    
    private val webUrlPattern = Regex("""^https?://[a-zA-Z0-9\-._~:/?#\[\]@!$&'()*+,;=%]+$""")
    
    fun validateURL(url: String): ValidationResult {
        if (url.isBlank()) {
            return ValidationResult(false, URLType.INVALID, "URL cannot be empty")
        }
        
        val cleanUrl = url.trim()
        
        for (pattern in youtubePatterns) {
            if (pattern.matches(cleanUrl)) {
                return ValidationResult(true, URLType.YOUTUBE)
            }
        }
        
        for (pattern in googleDrivePatterns) {
            if (pattern.matches(cleanUrl)) {
                return ValidationResult(true, URLType.GOOGLE_DRIVE)
            }
        }
        
        if (webUrlPattern.matches(cleanUrl)) {
            return ValidationResult(true, URLType.WEB_URL)
        }
        
        return ValidationResult(
            false, 
            URLType.INVALID, 
            "Invalid URL format. Please enter a valid web link (must start with http:// or https://)"
        )
    }
    
    fun extractYouTubeVideoId(url: String): String? {
        val cleanUrl = url.trim()
        for (pattern in youtubePatterns) {
            val matchResult = pattern.find(cleanUrl)
            if (matchResult != null && matchResult.groupValues.size >= 2) {
                return matchResult.groupValues[1]
            }
        }
        return null
    }

    

    fun extractGoogleDriveFileId(url: String): String? {
        if (url.isBlank()) return null
        
        val cleanUrl = url.trim()
        for (pattern in googleDrivePatterns) {
            val matchResult = pattern.find(cleanUrl)
            if (matchResult != null && matchResult.groupValues.size > 1) {
                return matchResult.groupValues[1]
            }
        }
        
        return null
    }
}