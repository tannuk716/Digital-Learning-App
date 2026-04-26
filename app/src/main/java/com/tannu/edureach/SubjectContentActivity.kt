package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.ui.viewmodel.UnifiedContent
import com.tannu.edureach.utils.DownloadHelper
import com.tannu.edureach.utils.EducationalWebActivity
import com.tannu.edureach.utils.ThemeManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.isActive

class SubjectContentActivity : AppCompatActivity() {

    private lateinit var rvContent: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var tvSubjectTitle: TextView
    private lateinit var btnBack: ImageView
    
    private lateinit var adapter: UnifiedContentAdapter
    private val db = FirebaseFirestore.getInstance()
    
    private var classId: String = ""
    private var subjectId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_subject_content)

        classId = intent.getStringExtra("CLASS_ID") ?: "class_1"
        subjectId = intent.getStringExtra("SUBJECT_ID") ?: "maths"

        rvContent = findViewById(R.id.rvContent)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)
        btnBack = findViewById(R.id.btnBack)

        val formattedSubject = subjectId.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        tvSubjectTitle.text = "$formattedSubject Content"

        btnBack.setOnClickListener { finish() }

        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = UnifiedContentAdapter(emptyList(), { openContent(it) }, { downloadContent(it) })
        rvContent.adapter = adapter

        fetchGroupedContent()
    }

    private fun fetchGroupedContent() {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        rvContent.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val displayList = mutableListOf<UnifiedContent>()
                
                for (unitNum in 1..20) {
                    if (!isActive) return@launch
                    val unitId = "unit_$unitNum"
                    var headerAdded = false
                    
                    fun checkAddHeader() {
                        if (!headerAdded) {
                            displayList.add(UnifiedContent(id = "${unitId}_header", title = "Unit $unitNum", type = "Header", urlOrData = "", timestamp = 0L))
                            headerAdded = true
                        }
                    }

                    val videosSnapshot = db.collection("classes").document(classId)
                        .collection("subjects").document(subjectId)
                        .collection("units").document(unitId)
                        .collection("videos")
                        .get().await()
                        
                    videosSnapshot.documents.forEach { doc ->
                        doc.toObject(VideoContent::class.java)?.let { video ->
                            checkAddHeader()
                            displayList.add(UnifiedContent(id = doc.id, title = "Video: ${video.title}", type = "Video", urlOrData = video.videoUrl, timestamp = 0L))
                        }
                    }
                    

                    val notesSnapshot = db.collection("classes").document(classId)
                        .collection("subjects").document(subjectId)
                        .collection("units").document(unitId)
                        .collection("notes")
                        .get().await()
                        
                    notesSnapshot.documents.forEach { doc ->
                        doc.toObject(NoteContent::class.java)?.let { note ->
                            checkAddHeader()
                            displayList.add(UnifiedContent(id = doc.id, title = "Note: ${note.title}", type = "Note", urlOrData = note.fileUrl, timestamp = 0L))
                        }
                    }
                }

                progressBar.visibility = View.GONE
                if (!isActive) return@launch
                
                if (displayList.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                } else {
                    rvContent.visibility = View.VISIBLE
                    adapter.submitList(displayList)
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isActive) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@SubjectContentActivity, "Error loading content: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openContent(content: UnifiedContent) {
        if (content.type == "Header" || content.urlOrData.isBlank()) {
            if (content.type != "Header") Toast.makeText(this, "Error: Invalid or missing URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        when (content.type) {
            "Video" -> {
                val localUri = DownloadHelper.getLocalFileUri(this, content.title, true)
                if (localUri != null) {
                    val intent = Intent(this, VideoPlayerActivity::class.java)
                    intent.putExtra("VIDEO_URL", localUri.toString())
                    intent.putExtra("IS_OFFLINE", true)
                    Toast.makeText(this, "Playing Offline Video", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                } else {
                    val url = content.urlOrData
                    if (url.contains("youtube.com", true) || url.contains("youtu.be", true)) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(this, EducationalWebActivity::class.java)
                            intent.putExtra("WEB_URL", url)
                            intent.putExtra("WEB_TITLE", content.title)
                            startActivity(intent)
                        }
                    } else if (url.endsWith(".mp4", true)) {
                        val intent = Intent(this, VideoPlayerActivity::class.java)
                        intent.putExtra("VIDEO_URL", url)
                        intent.putExtra("IS_OFFLINE", false)
                        startActivity(intent)
                    } else {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Cannot open this link format directly", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            "Note" -> {
                val localUri = DownloadHelper.getLocalFileUri(this, content.title, false)
                if (localUri != null) {
                    Toast.makeText(this, "Opening Offline Document", Toast.LENGTH_SHORT).show()
                    val pIntent = Intent(Intent.ACTION_VIEW)
                    pIntent.setDataAndType(localUri, "application/pdf")
                    pIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    try {
                        startActivity(pIntent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "No PDF Viewer installed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val url = content.urlOrData
                    if (url.contains("notion.so", true) || url.endsWith(".pdf", true)) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                            val intent = Intent(this, EducationalWebActivity::class.java)
                            intent.putExtra("WEB_URL", url)
                            intent.putExtra("WEB_TITLE", content.title)
                            startActivity(intent)
                        }
                    } else {
                        val intent = Intent(this, EducationalWebActivity::class.java)
                        intent.putExtra("WEB_URL", url)
                        intent.putExtra("WEB_TITLE", content.title)
                        startActivity(intent)
                    }
                }
            }
        }
    }
    
    private fun downloadContent(content: UnifiedContent) {
        if (content.type == "Header" || content.urlOrData.isBlank() || content.urlOrData.contains("youtube.com", true) || content.urlOrData.contains("youtu.be", true)) return
        val isVideo = content.type == "Video"
        DownloadHelper.downloadContent(this, content.urlOrData, content.title, isVideo)
    }
}