package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.utils.EducationalWebActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TeacherContentViewActivity : AppCompatActivity() {

    private lateinit var tvSubjectTitle: TextView
    private lateinit var rvContent: RecyclerView
    private lateinit var tvEmptyState: TextView
    
    private val db = FirebaseFirestore.getInstance()
    private var subjectId = ""
    private var currentClassId = "class_1"
    private lateinit var rvClassTabs: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_content_view)

        subjectId = intent.getStringExtra("SUBJECT_ID") ?: "maths"
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"

        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)
        rvContent = findViewById(R.id.rvContent)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        tvSubjectTitle.text = "$subjectName Content"
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvContent.layoutManager = LinearLayoutManager(this)
        
        rvClassTabs = findViewById(R.id.rvClassTabs)
        rvClassTabs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        
        setupClassTabs()
        loadContentForClass(currentClassId)
    }

    private fun setupClassTabs() {
        val classes = (1..10).map { "class_$it" }
        val adapter = ClassTabAdapter(classes, currentClassId) { selectedClass ->
            if (currentClassId != selectedClass) {
                currentClassId = selectedClass
                setupClassTabs()
                loadContentForClass(currentClassId)
            }
        }
        rvClassTabs.adapter = adapter
    }

    private fun loadContentForClass(classId: String) {
        android.util.Log.d("TeacherContentView", "Loading content for classId=$classId, subjectId=$subjectId")
        

        val subjectIds = when (subjectId.lowercase()) {
            "maths", "math" -> listOf("maths", "math")
            else -> listOf(subjectId, subjectId.lowercase())
        }
        
        lifecycleScope.launch {
            try {
                val units = listOf("unit_1", "unit_2", "unit_3", "unit_4", "unit_5", "unit_6", "unit_7", "unit_8", "unit_9", "unit_10")
                
                val allContent = mutableListOf<ContentItem>()

                for (searchSubjectId in subjectIds) {
                    android.util.Log.d("TeacherContentView", "Searching with subjectId: $searchSubjectId")
                    
                    for (unitId in units) {

                        val videosSnapshot = db.collection("classes").document(classId)
                            .collection("subjects").document(searchSubjectId)
                            .collection("units").document(unitId)
                            .collection("videos")
                            .get().await()
                        
                        android.util.Log.d("TeacherContentView", "[$searchSubjectId] Found ${videosSnapshot.size()} videos in $classId/$searchSubjectId/$unitId")
                        
                        videosSnapshot.documents.forEach { doc ->
                            doc.toObject(VideoContent::class.java)?.let { video ->

                                val isNotionContent = video.videoUrl.contains("notion.so", ignoreCase = true) || 
                                                     video.videoUrl.contains("notion.site", ignoreCase = true) ||
                                                     video.videoUrl.contains("prod-files-secure", ignoreCase = true)
                                
                                if (!isNotionContent && video.videoUrl.isNotEmpty()) {
                                    allContent.add(ContentItem(
                                        id = doc.id,
                                        classId = classId,
                                        unitId = unitId,
                                        type = "Video",
                                        title = video.title,
                                        description = video.description,
                                        url = video.videoUrl
                                    ))
                                }
                            }
                        }

                        val notesSnapshot = db.collection("classes").document(classId)
                            .collection("subjects").document(searchSubjectId)
                            .collection("units").document(unitId)
                            .collection("notes")
                            .get().await()
                        
                        android.util.Log.d("TeacherContentView", "[$searchSubjectId] Found ${notesSnapshot.size()} notes in $classId/$searchSubjectId/$unitId")
                        
                        notesSnapshot.documents.forEach { doc ->
                            doc.toObject(NoteContent::class.java)?.let { note ->

                                val isNotionContent = note.fileUrl.contains("notion.so", ignoreCase = true) || 
                                                     note.fileUrl.contains("notion.site", ignoreCase = true) ||
                                                     note.fileUrl.contains("prod-files-secure", ignoreCase = true)
                                
                                if (!isNotionContent && note.fileUrl.isNotEmpty()) {
                                    allContent.add(ContentItem(
                                        id = doc.id,
                                        classId = classId,
                                        unitId = unitId,
                                        type = "Note",
                                        title = note.title,
                                        description = note.description,
                                        url = note.fileUrl
                                    ))
                                }
                            }
                        }

                        val quizzesSnapshot = db.collection("classes").document(classId)
                            .collection("subjects").document(searchSubjectId)
                            .collection("units").document(unitId)
                            .collection("quizzes")
                            .get().await()
                        
                        android.util.Log.d("TeacherContentView", "[$searchSubjectId] Found ${quizzesSnapshot.size()} quizzes in $classId/$searchSubjectId/$unitId")
                        
                        quizzesSnapshot.documents.forEach { doc ->
                            doc.toObject(QuizModel::class.java)?.let { quiz ->

                                allContent.add(ContentItem(
                                    id = doc.id,
                                    classId = classId,
                                    unitId = unitId,
                                    type = "Quiz",
                                    title = quiz.title,
                                    description = "${quiz.questions.size} questions",
                                    url = ""
                                ))
                            }
                        }
                    }
                }
                

                val uniqueContent = allContent.distinctBy { it.id }

                android.util.Log.d("TeacherContentView", "Total content items after filtering: ${uniqueContent.size}")

                if (uniqueContent.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                    tvEmptyState.text = "No content uploaded for ${classId.replace("_", " ").replaceFirstChar { it.uppercase() }} yet.\nUpload content from the dashboard."
                    rvContent.visibility = View.GONE
                } else {
                    tvEmptyState.visibility = View.GONE
                    rvContent.visibility = View.VISIBLE
                    rvContent.adapter = ContentAdapter(uniqueContent, { item ->
                        openContent(item)
                    }, { item ->
                        deleteContent(item)
                    })
                }

            } catch (e: Exception) {
                android.util.Log.e("TeacherContentView", "Error loading content", e)
                Toast.makeText(this@TeacherContentViewActivity, "Failed to load content: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openContent(item: ContentItem) {
        when (item.type) {
            "Video" -> {

                if (item.url.contains("youtube.com", ignoreCase = true) || 
                    item.url.contains("youtu.be", ignoreCase = true)) {

                    val intent = Intent(this, EducationalWebActivity::class.java)
                    intent.putExtra("WEB_URL", item.url)
                    intent.putExtra("WEB_TITLE", item.title)
                    startActivity(intent)
                } else {

                    val intent = Intent(this, VideoPlayerActivity::class.java)
                    intent.putExtra("VIDEO_URL", item.url)
                    intent.putExtra("IS_OFFLINE", false)
                    startActivity(intent)
                }
            }
            "Note" -> {

                val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
                intent.putExtra("WEB_URL", item.url)
                intent.putExtra("WEB_TITLE", item.title)
                startActivity(intent)
            }
            "Quiz" -> {

                lifecycleScope.launch {
                    try {

                        val quizDoc = db.collection("classes").document(item.classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(item.unitId)
                            .collection("quizzes")
                            .document(item.id)
                            .get()
                            .await()
                        
                        val quiz = quizDoc.toObject(QuizModel::class.java)
                        if (quiz != null) {
                            val intent = Intent(this@TeacherContentViewActivity, com.tannu.edureach.practice.QuizActivity::class.java)
                            intent.putExtra("QUIZ_TITLE", quiz.title)
                            intent.putExtra("QUIZ_QUESTIONS", ArrayList(quiz.questions))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@TeacherContentViewActivity, "Quiz data not found", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("TeacherContentView", "Error loading quiz", e)
                        Toast.makeText(this@TeacherContentViewActivity, "Failed to open quiz: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun deleteContent(item: ContentItem) {
        AlertDialog.Builder(this)
            .setTitle("Delete Content")
            .setMessage("Are you sure you want to delete \"${item.title}\"?\n\nThis will remove it from all student accounts.")
            .setPositiveButton("Delete") { _, _ ->
                performDelete(item)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDelete(item: ContentItem) {
        lifecycleScope.launch {
            try {
                val collection = when (item.type) {
                    "Video" -> "videos"
                    "Note" -> "notes"
                    "Quiz" -> "quizzes"
                    else -> return@launch
                }

                db.collection("classes").document(item.classId)
                    .collection("subjects").document(subjectId)
                    .collection("units").document(item.unitId)
                    .collection(collection)
                    .document(item.id)
                    .delete()
                    .await()

                Toast.makeText(this@TeacherContentViewActivity, "Content deleted successfully", Toast.LENGTH_SHORT).show()
                loadContentForClass(item.classId)

            } catch (e: Exception) {
                Toast.makeText(this@TeacherContentViewActivity, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class ContentItem(
        val id: String,
        val classId: String,
        val unitId: String,
        val type: String,
        val title: String,
        val description: String,
        val url: String
    )

    private class ContentAdapter(
        private val items: List<ContentItem>,
        private val onOpen: (ContentItem) -> Unit,
        private val onDelete: (ContentItem) -> Unit
    ) : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvType: TextView = view.findViewById(R.id.tvContentType)
            val tvTitle: TextView = view.findViewById(R.id.tvContentTitle)
            val tvLocation: TextView = view.findViewById(R.id.tvContentLocation)
            val tvDescription: TextView = view.findViewById(R.id.tvContentDescription)
            val btnOpen: Button = view.findViewById(R.id.btnOpenContent)
            val btnDelete: Button = view.findViewById(R.id.btnDeleteContent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_teacher_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvType.text = item.type
            holder.tvTitle.text = item.title
            
            val classDisplay = item.classId.replace("_", " ").replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase() else it.toString() 
            }
            val unitDisplay = item.unitId.replace("_", " ").replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase() else it.toString() 
            }
            
            holder.tvLocation.text = "$classDisplay / $unitDisplay"
            holder.tvDescription.text = item.description
            holder.btnOpen.setOnClickListener { onOpen(item) }
            holder.btnDelete.setOnClickListener { onDelete(item) }
        }

        override fun getItemCount() = items.size
    }

    private class ClassTabAdapter(
        private val classes: List<String>,
        private val currentClassId: String,
        private val onClassClick: (String) -> Unit
    ) : RecyclerView.Adapter<ClassTabAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvClass: TextView = view as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context).apply {
                id = View.generateViewId()
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
                setPadding(40, 20, 40, 20)
                textSize = 16f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val classId = classes[position]
            val className = "Class ${classId.replace("class_", "")}"
            
            holder.tvClass.text = className
            holder.tvClass.textAlignment = View.TEXT_ALIGNMENT_CENTER
            
            val drawable = android.graphics.drawable.GradientDrawable()
            drawable.cornerRadius = 40f
            
            if (classId == currentClassId) {
                drawable.setColor(android.graphics.Color.parseColor("#4CAF50"))
                holder.tvClass.setTextColor(android.graphics.Color.WHITE)
            } else {
                drawable.setColor(android.graphics.Color.parseColor("#E0E0E0"))
                holder.tvClass.setTextColor(android.graphics.Color.DKGRAY)
            }
            holder.tvClass.background = drawable
            
            holder.itemView.setOnClickListener { onClassClick(classId) }
        }

        override fun getItemCount() = classes.size
    }
}