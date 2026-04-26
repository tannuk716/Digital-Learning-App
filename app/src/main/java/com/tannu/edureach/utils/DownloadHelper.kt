package com.tannu.edureach.utils

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import java.io.File

object DownloadHelper {
    
    private const val CHANNEL_ID = "download_channel"
    private const val CHANNEL_NAME = "Downloads"
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Download notifications for educational content"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun downloadContent(context: Context, url: String, title: String, isVideo: Boolean, subjectName: String = "") {
        if (url.isEmpty()) {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            return
        }
        

        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            Toast.makeText(context, "Cannot download YouTube directly. Use direct URLs instead.", Toast.LENGTH_LONG).show()
            return
        }

        createNotificationChannel(context)

        val extension = if (isVideo) ".mp4" else ".pdf"
        val fileNamePrefix = if (subjectName.isNotEmpty()) {
            "${subjectName.replace(Regex("[^a-zA-Z0-9.-]"), "_")}_"
        } else {
            ""
        }
        val fileName = "$fileNamePrefix${title.replace(Regex("[^a-zA-Z0-9.-]"), "_")}$extension"
        
        val folder = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_DOCUMENTS
        
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri).apply {
            setTitle("Downloading $title")
            setDescription("EduReach Content")

            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(folder, "EduReach/$fileName")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)

            if (!isVideo) {
                setMimeType("application/pdf")
            } else {
                setMimeType("video/mp4")
            }
        }
        
        try {
            val downloadId = downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: $title\nCheck notification bar", Toast.LENGTH_LONG).show()
            android.util.Log.d("DownloadHelper", "Download started with ID: $downloadId for $fileName")
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("DownloadHelper", "Download error", e)
        }
    }

    fun getLocalFileUri(context: Context, title: String, isVideo: Boolean, subjectName: String = ""): Uri? {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                    
                    if (titleIndex >= 0 && uriIndex >= 0 && idIndex >= 0) {
                        val dTitle = cursor.getString(titleIndex)
                        
                        if (subjectName.isNotEmpty()) {
                            val expectedTitle = "Downloading $title"
                            if (dTitle == expectedTitle) {
                                val localUriString = cursor.getString(uriIndex)
                                val extension = if (isVideo) ".mp4" else ".pdf"
                                val expectedFileName = "${subjectName.replace(Regex("[^a-zA-Z0-9.-]"), "_")}_${title.replace(Regex("[^a-zA-Z0-9.-]"), "_")}$extension"
                                
                                android.util.Log.d("DownloadHelper", "Checking file: $localUriString")
                                android.util.Log.d("DownloadHelper", "Expected filename contains: $expectedFileName")
                                
                                if (localUriString != null && localUriString.contains(expectedFileName)) {
                                    val downloadId = cursor.getLong(idIndex)
                                    cursor.close()
                                    android.util.Log.d("DownloadHelper", "✅ Found correct file with subject prefix")
                                    return downloadManager.getUriForDownloadedFile(downloadId)
                                } else {
                                    android.util.Log.d("DownloadHelper", "❌ File found but wrong name (old format), skipping")
                                }
                            }
                        } else {
                            if (dTitle == "Downloading $title") {
                                val downloadId = cursor.getLong(idIndex)
                                cursor.close()
                                return downloadManager.getUriForDownloadedFile(downloadId)
                            }
                        }
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
            
            if (subjectName.isNotEmpty()) {
                android.util.Log.d("DownloadHelper", "No file found with subject prefix, will download fresh")
            }
        } catch (e: Exception) {
            android.util.Log.e("DownloadHelper", "Error getting local file URI", e)
        }
        return null
    }
    
    fun deleteOldDownloads(context: Context, title: String, isVideo: Boolean) {
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val idsToDelete = mutableListOf<Long>()
                do {
                    val titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)
                    val idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID)
                    val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    
                    if (titleIndex >= 0 && idIndex >= 0 && uriIndex >= 0) {
                        val dTitle = cursor.getString(titleIndex)
                        if (dTitle == "Downloading $title") {
                            val localUriString = cursor.getString(uriIndex)
                            val extension = if (isVideo) ".mp4" else ".pdf"
                            val oldFileName = "${title.replace(Regex("[^a-zA-Z0-9.-]"), "_")}$extension"
                            
                            if (localUriString != null && localUriString.contains(oldFileName) && !localUriString.contains("_${title}")) {
                                val downloadId = cursor.getLong(idIndex)
                                idsToDelete.add(downloadId)
                                android.util.Log.d("DownloadHelper", "Marking old file for deletion: $localUriString")
                            }
                        }
                    }
                } while (cursor.moveToNext())
                cursor.close()
                
                idsToDelete.forEach { id ->
                    downloadManager.remove(id)
                    android.util.Log.d("DownloadHelper", "Deleted old download with ID: $id")
                }
                
                if (idsToDelete.isNotEmpty()) {
                    android.util.Log.d("DownloadHelper", "Cleaned up ${idsToDelete.size} old file(s)")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DownloadHelper", "Error deleting old downloads", e)
        }
    }
}