package com.tannu.edureach.utils

/**
 * Helper to convert Google Drive URLs to direct download/view format
 */
object GoogleDriveUrlHelper {
    
    /**
     * Convert any Google Drive URL to a format that works for direct viewing/downloading
     */
    fun convertToDirectUrl(url: String): String {
        // Already in direct download format
        if (url.contains("drive.google.com/uc?export=download")) {
            return url
        }
        
        // Extract file ID from various Google Drive URL formats
        val fileId = extractFileId(url) ?: return url
        
        // Return direct download URL
        return "https://drive.google.com/uc?export=download&id=$fileId"
    }
    
    /**
     * Convert to Google Docs viewer URL (for in-app viewing)
     */
    fun convertToViewerUrl(url: String): String {
        val directUrl = convertToDirectUrl(url)
        return "https://docs.google.com/gview?embedded=true&url=$directUrl"
    }
    
    /**
     * Extract file ID from various Google Drive URL formats
     */
    fun extractFileId(url: String): String? {
        return when {
            // Format: https://drive.google.com/file/d/{id}/view
            url.contains("/file/d/") -> {
                val regex = """/file/d/([^/]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }
            // Format: https://drive.google.com/open?id={id}
            url.contains("open?id=") -> {
                val regex = """open\?id=([^&]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }
            // Format: https://drive.google.com/uc?export=download&id={id}
            url.contains("uc?export=download&id=") -> {
                val regex = """id=([^&]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }
            // Format: https://drive.google.com/drive/folders/{id}
            url.contains("/folders/") -> {
                val regex = """/folders/([^?]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }
            else -> null
        }
    }
    
    /**
     * Extract file ID from various Google Drive URL formats (private version for internal use)
     */
    private fun extractFileIdPrivate(url: String): String? {
        return extractFileId(url)
    }
    
    /**
     * Check if URL is a Google Drive URL
     */
    fun isGoogleDriveUrl(url: String): Boolean {
        return url.contains("drive.google.com", ignoreCase = true)
    }
}
