package com.tannu.edureach.utils

object GoogleDriveUrlHelper {
    
    

    fun convertToDirectUrl(url: String): String {

        if (url.contains("drive.google.com/uc?export=download")) {
            return url
        }
        

        val fileId = extractFileId(url) ?: return url
        

        return "https://drive.google.com/uc?export=download&id=$fileId"
    }
    
    

    fun convertToViewerUrl(url: String): String {
        val directUrl = convertToDirectUrl(url)
        return "https://docs.google.com/gview?embedded=true&url=$directUrl"
    }
    
    

    fun extractFileId(url: String): String? {
        return when {

            url.contains("/file/d/") -> {
                val regex = """/file/d/([^/]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }

            url.contains("open?id=") -> {
                val regex = """open\?id=([^&]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }

            url.contains("uc?export=download&id=") -> {
                val regex = """id=([^&]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }

            url.contains("/folders/") -> {
                val regex = """/folders/([^?]+)""".toRegex()
                regex.find(url)?.groupValues?.get(1)
            }
            else -> null
        }
    }
    
    

    private fun extractFileIdPrivate(url: String): String? {
        return extractFileId(url)
    }
    
    

    fun isGoogleDriveUrl(url: String): Boolean {
        return url.contains("drive.google.com", ignoreCase = true)
    }
}