package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.repository.ContentRepository
import com.tannu.edureach.utils.GoogleDriveUrlHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive

class NoteListActivity : AppCompatActivity() {

    private lateinit var rvNotes: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var tvSubjectTitle: TextView
    private val repository = ContentRepository()
    private val allNotesMap = mutableMapOf<String, List<NoteWithUnit>>()
    private val allVideosMap = mutableMapOf<String, List<VideoWithUnit>>()
    private var teacherOnly = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        val classId = intent.getStringExtra("CLASS_ID") ?: "class_1"
        val subjectId = intent.getStringExtra("SUBJECT_ID") ?: "maths"
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"
        teacherOnly = intent.getBooleanExtra("TEACHER_ONLY", false)

        rvNotes = findViewById(R.id.rvNotes)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)

        tvSubjectTitle.text = "$subjectName - Teacher Content"
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvNotes.layoutManager = LinearLayoutManager(this)

        rvNotes.adapter = ContentAdapter(emptyList()) {}

        loadContent(classId, subjectId)
    }

    private fun loadContent(classId: String, subjectId: String) {

        android.util.Log.d("NoteList", "========================================")
        android.util.Log.d("NoteList", "LOADING CONTENT")
        android.util.Log.d("NoteList", "ClassID: $classId")
        android.util.Log.d("NoteList", "SubjectID: $subjectId")
        android.util.Log.d("NoteList", "========================================")
        

        val subjectIds = if (subjectId == "maths") {
            listOf("maths", "math")
        } else {
            listOf(subjectId)
        }
        

        for (searchSubjectId in subjectIds) {
            android.util.Log.d("NoteList", "Searching with subjectId: $searchSubjectId")
            
            for (unitNum in 1..20) {
                val unitId = "unit_$unitNum"
                

                lifecycleScope.launch {
                    if (!isActive) return@launch
                    try {
                        repository.getNotes(classId, searchSubjectId, unitId).collect { notes ->
                            if (notes.isNotEmpty()) {
                                android.util.Log.d("NoteList", "[$searchSubjectId] Unit $unitId: Found ${notes.size} notes")
                                notes.forEach { note ->
                                    android.util.Log.d("NoteList", "  📄 ${note.title} | ${note.fileUrl}")
                                }
                            }

                            val existing = allNotesMap[unitId] ?: emptyList()
                            val combined = (existing + notes.map { NoteWithUnit(it, unitId) }).distinctBy { it.note.id }
                            allNotesMap[unitId] = combined
                            combineAndRefreshUI()
                        }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        android.util.Log.e("NoteList", "Error loading notes $searchSubjectId/$unitId", e)
                    }
                }
                

                lifecycleScope.launch {
                    if (!isActive) return@launch
                    try {
                        repository.getVideos(classId, searchSubjectId, unitId).collect { videos ->
                            if (videos.isNotEmpty()) {
                                android.util.Log.d("NoteList", "[$searchSubjectId] Unit $unitId: Found ${videos.size} videos")
                                videos.forEach { video ->
                                    android.util.Log.d("NoteList", "  📹 ${video.title} | ${video.videoUrl}")
                                }
                            }

                            val existing = allVideosMap[unitId] ?: emptyList()
                            val combined = (existing + videos.map { VideoWithUnit(it, unitId) }).distinctBy { it.video.id }
                            allVideosMap[unitId] = combined
                            combineAndRefreshUI()
                        }
                    } catch (e: kotlinx.coroutines.CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        android.util.Log.e("NoteList", "Error loading videos $searchSubjectId/$unitId", e)
                    }
                }
            }
        }
    }
    
    private fun combineAndRefreshUI() {
        if (isDestroyed || isFinishing) return
        

        val contentList = mutableListOf<ContentItem>()
        contentList.clear()
        

        val notesList = allNotesMap.values.flatten()
            .distinctBy { it.note.title + it.note.fileUrl }
        
        android.util.Log.d("NoteList", "Total notes before filtering: ${notesList.size}")
        

        val filteredNotes = notesList.filter { noteWithUnit ->
            val url = noteWithUnit.note.fileUrl
            val title = noteWithUnit.note.title.lowercase()
            

            val isNotion = url.contains("notion.so", ignoreCase = true) ||
                          url.contains("notion.site", ignoreCase = true) ||
                          url.contains("prod-files-secure", ignoreCase = true)
            

            val isWorksheetContent = title.contains("chapter", ignoreCase = true) ||
                                    title.contains("worksheet", ignoreCase = true) ||
                                    title.contains("tap to view", ignoreCase = true) ||
                                    noteWithUnit.note.description.contains("worksheet", ignoreCase = true) ||
                                    noteWithUnit.note.description.contains("tap to view", ignoreCase = true)
            
            val shouldInclude = !isNotion && !isWorksheetContent && url.isNotEmpty()
            
            if (!shouldInclude) {
                android.util.Log.d("NoteList", "BLOCKED: ${noteWithUnit.note.title} (isNotion=$isNotion, isWorksheet=$isWorksheetContent)")
            }
            
            shouldInclude
        }
        
        android.util.Log.d("NoteList", "Notes after filtering: ${filteredNotes.size}")
        
        filteredNotes.forEach { noteWithUnit ->
            contentList.add(ContentItem(
                type = "📄 Note",
                title = noteWithUnit.note.title,
                description = noteWithUnit.note.description,
                url = noteWithUnit.note.fileUrl,
                unitId = noteWithUnit.unitId,
                timestamp = noteWithUnit.note.timestamp,
                isYoutube = false,
                isVideo = false
            ))
        }
        

        val videosList = allVideosMap.values.flatten()
            .distinctBy { it.video.title + it.video.videoUrl }
        
        android.util.Log.d("NoteList", "Total videos before filtering: ${videosList.size}")
        

        val filteredVideos = videosList.filter { videoWithUnit ->
            val url = videoWithUnit.video.videoUrl
            val title = videoWithUnit.video.title.lowercase()
            

            val isNotion = url.contains("notion.so", ignoreCase = true) ||
                          url.contains("notion.site", ignoreCase = true) ||
                          url.contains("prod-files-secure", ignoreCase = true)
            

            val isWorksheetContent = title.contains("chapter", ignoreCase = true) ||
                                    title.contains("worksheet", ignoreCase = true) ||
                                    title.contains("tap to view", ignoreCase = true) ||
                                    videoWithUnit.video.description.contains("worksheet", ignoreCase = true) ||
                                    videoWithUnit.video.description.contains("tap to view", ignoreCase = true)
            
            val shouldInclude = !isNotion && !isWorksheetContent && url.isNotEmpty()
            
            if (!shouldInclude) {
                android.util.Log.d("NoteList", "BLOCKED: ${videoWithUnit.video.title} (isNotion=$isNotion, isWorksheet=$isWorksheetContent)")
            }
            
            shouldInclude
        }
        
        android.util.Log.d("NoteList", "Videos after filtering: ${filteredVideos.size}")
        
        filteredVideos.forEach { videoWithUnit ->
            contentList.add(ContentItem(
                type = "📹 Video",
                title = videoWithUnit.video.title,
                description = videoWithUnit.video.description,
                url = videoWithUnit.video.videoUrl,
                unitId = videoWithUnit.unitId,
                timestamp = videoWithUnit.video.timestamp,
                isYoutube = videoWithUnit.video.isYoutube,
                isVideo = true
            ))
        }
        

        val sortedList = contentList.sortedByDescending { it.timestamp }
        
        android.util.Log.d("NoteList", "TOTAL TO DISPLAY: ${sortedList.size} items")

        if (sortedList.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No teacher content available yet.\nTeachers can upload notes and videos for this subject."
            rvNotes.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvNotes.visibility = View.VISIBLE
            rvNotes.adapter = ContentAdapter(sortedList) { content ->
                openContent(content)
            }
        }
    }
    
    private fun openContent(content: ContentItem) {
        if (content.url.isEmpty()) {
            Toast.makeText(this, "Content URL not available", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.isVideo) {
            if (content.isYoutube) {
                val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
                intent.putExtra("WEB_URL", content.url)
                intent.putExtra("WEB_TITLE", content.title)
                startActivity(intent)
            } else {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", content.url)
                intent.putExtra("IS_OFFLINE", false)
                startActivity(intent)
            }
        } else {

            openNote(content)
        }
    }

    private fun openNote(content: ContentItem) {
        var url = content.url
        

        if (GoogleDriveUrlHelper.isGoogleDriveUrl(url)) {
            url = GoogleDriveUrlHelper.convertToDirectUrl(url)
        }
        

        val localUri = com.tannu.edureach.utils.DownloadHelper.getLocalFileUri(this, content.title, false)
        if (localUri != null) {
            Toast.makeText(this, "Opening offline document", Toast.LENGTH_SHORT).show()
            val pIntent = Intent(Intent.ACTION_VIEW)
            pIntent.setDataAndType(localUri, "application/pdf")
            pIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(pIntent)
                return
            } catch (e: Exception) {
                Toast.makeText(this, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
            }
        }

        if (url.lowercase().endsWith(".pdf") || url.contains(".pdf?alt=media", ignoreCase = true) || 
            url.contains("drive.google.com", ignoreCase = true)) {

            val directIntent = Intent(Intent.ACTION_VIEW)
            directIntent.setDataAndType(android.net.Uri.parse(url), "application/pdf")
            directIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            directIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            
            try {
                startActivity(directIntent)
                return
            } catch (e: Exception) {

                android.util.Log.d("NoteList", "Direct PDF open failed, using WebView: ${e.message}")
            }
        }

        val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
        intent.putExtra("WEB_URL", url)
        intent.putExtra("WEB_TITLE", content.title)
        startActivity(intent)
    }

    data class NoteWithUnit(val note: NoteContent, val unitId: String)
    data class VideoWithUnit(val video: VideoContent, val unitId: String)
    data class ContentItem(
        val type: String,
        val title: String,
        val description: String,
        val url: String,
        val unitId: String,
        val timestamp: Long,
        val isYoutube: Boolean,
        val isVideo: Boolean
    )

    private class ContentAdapter(
        private val items: List<ContentItem>,
        private val onClick: (ContentItem) -> Unit
    ) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvContentType: TextView? = view.findViewById(R.id.tvContentType)
            val tvNoteTitle: TextView = view.findViewById(R.id.tvNoteTitle)
            val tvNoteDesc: TextView = view.findViewById(R.id.tvNoteDesc)
            val tvUnitBadge: TextView = view.findViewById(R.id.tvUnitBadge)
            val btnDownload: View? = view.findViewById(R.id.btnDownloadNote)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            

            holder.tvContentType?.text = item.type
            holder.tvContentType?.visibility = View.VISIBLE
            
            holder.tvNoteTitle.text = item.title
            holder.tvNoteDesc.text = item.description
            
            val unitName = item.unitId.replace("_", " ").uppercase()
            holder.tvUnitBadge.text = unitName
            holder.tvUnitBadge.visibility = View.VISIBLE
            

            holder.itemView.setOnClickListener { onClick(item) }
            

            holder.btnDownload?.setOnClickListener {
                val context = holder.itemView.context
                if (item.url.isEmpty()) {
                    Toast.makeText(context, "URL not available", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                

                com.tannu.edureach.utils.DownloadHelper.downloadContent(
                    context, 
                    item.url, 
                    item.title, 
                    item.isVideo
                )
            }
        }

        override fun getItemCount() = items.size
    }
}