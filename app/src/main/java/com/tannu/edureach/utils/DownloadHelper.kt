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
    
    fun downloadContent(context: Context, url: String, title: String, isVideo: Boolean) {
        if (url.isEmpty()) {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Cannot download raw YouTube pages natively without ytdl, 
        // assuming standard mp4/pdf direct URLs or Firebase Storage links
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            Toast.makeText(context, "Cannot download YouTube directly. Use direct URLs instead.", Toast.LENGTH_LONG).show()
            return
        }

        // Create notification channel for Android 8.0+
        createNotificationChannel(context)

        val extension = if (isVideo) ".mp4" else ".pdf"
        val fileName = "${title.replace(Regex("[^a-zA-Z0-9.-]"), "_")}$extension"
        
        val folder = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_DOCUMENTS
        
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri).apply {
            setTitle("Downloading $title")
            setDescription("EduReach Content")
            // Show notification during download and after completion
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(folder, "EduReach/$fileName")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            // Add MIME type for better handling
            if (!isVideo) {
                setMimeType("application/pdf")
            } else {
                setMimeType("video/mp4")
            }
        }
        
        try {
            val downloadId = downloadManager.enqueue(request)
            Toast.makeText(context, "Download started: $title\nCheck notification bar", Toast.LENGTH_LONG).show()
            android.util.Log.d("DownloadHelper", "Download started with ID: $downloadId for $title")
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("DownloadHelper", "Download error", e)
        }
    }

    fun getLocalFileUri(context: Context, title: String, isVideo: Boolean): Uri? {
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
                        if (dTitle == "Downloading $title") {
                            val downloadId = cursor.getLong(idIndex)
                            cursor.close()
                            
                            // Use DownloadManager to get proper content URI
                            return downloadManager.getUriForDownloadedFile(downloadId)
                        }
                    }
                } while (cursor.moveToNext())
                cursor.close()
            }
        } catch (e: Exception) {
            android.util.Log.e("DownloadHelper", "Error getting local file URI", e)
        }
        return null
    }
}
