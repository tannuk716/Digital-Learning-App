package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.animation.AnimationUtils
import android.view.MotionEvent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.repository.ContentRepository
import com.tannu.edureach.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class TeacherDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val repository = ContentRepository()

    private lateinit var tvTeacherName: TextView
    private lateinit var tvTeacherSubject: TextView
    private lateinit var rvTeacherContent: RecyclerView
    private lateinit var emptyStateLayout: View
    
    private var teacherSubject: String = "General"
    private var notesListenerRegistration: ListenerRegistration? = null
    private var videosListenerRegistration: ListenerRegistration? = null
    
    private var selectedSubjectFilter: String = "All"
    
    private val currentNotes = mutableListOf<TeacherContentItem>()
    private val currentVideos = mutableListOf<TeacherContentItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvTeacherName = findViewById(R.id.tvTeacherName)
        tvTeacherSubject = findViewById(R.id.tvTeacherSubject)
        rvTeacherContent = findViewById(R.id.rvTeacherContent)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        
        rvTeacherContent.layoutManager = LinearLayoutManager(this)

        val rvTeacherSubjects = findViewById<RecyclerView>(R.id.rvTeacherSubjects)
        rvTeacherSubjects.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
        setupSubjectCards(rvTeacherSubjects)

        findViewById<Button>(R.id.btnAddContent).setOnClickListener {
            startActivity(Intent(this, AddLearningContentActivity::class.java))
        }
        
        findViewById<Button>(R.id.btnAddGame)?.setOnClickListener {
            startActivity(Intent(this, AddGameActivity::class.java))
        }
        
        findViewById<View>(R.id.btnNavProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnNavLogout).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.btnRefreshContent)?.setOnClickListener {
            loadTeacherContent()
            Toast.makeText(this, "Refreshing content...", Toast.LENGTH_SHORT).show()
        }

        loadTeacherData()
    }

    override fun onResume() {
        super.onResume()
        loadTeacherData()
    }

    private fun loadTeacherData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Teacher"
                    teacherSubject = document.getString("subject") ?: "General"
                    tvTeacherName.text = "Welcome, $name"
                    tvTeacherSubject.text = "Subject: $teacherSubject"
                    
                    loadTeacherContent()
                }
            }
            .addOnFailureListener {
                tvTeacherName.text = "Error Loading Profile"
            }
    }

    private fun loadTeacherContent() {
        notesListenerRegistration?.remove()
        videosListenerRegistration?.remove()

        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L

        notesListenerRegistration = db.collectionGroup("notes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                currentNotes.clear()
                snapshot?.documents?.forEach { doc ->
                    val pathParts = doc.reference.path.split("/")
                    if (pathParts.size >= 7) {
                        val classId = pathParts[1]
                        val subjectId = pathParts[3]
                        val unitId = pathParts[5]
                        
                        val title = doc.getString("title") ?: ""
                        val fileUrl = doc.getString("fileUrl") ?: ""
                        val timestamp = doc.getLong("timestamp") ?: 0L
                        
                        val isNotion = fileUrl.contains("notion", ignoreCase = true) || 
                                       fileUrl.contains("amazonaws.com", ignoreCase = true)
                        

                        val isWithin24Hours = (currentTime - timestamp) <= oneDayInMillis
                        
                        if (title.isNotEmpty() && !isNotion && isWithin24Hours) {
                            if (teacherSubject == "General" || subjectId.equals(teacherSubject, ignoreCase = true)) {
                                currentNotes.add(TeacherContentItem(
                                    id = doc.id, classId = classId, subjectId = subjectId, unitId = unitId,
                                    type = "Note", title = title, url = fileUrl, description = doc.getString("description") ?: "",
                                    timestamp = timestamp
                                ))
                            }
                        }
                    }
                }
                updateTeacherContentList()
            }
            
        videosListenerRegistration = db.collectionGroup("videos")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                currentVideos.clear()
                snapshot?.documents?.forEach { doc ->
                    val pathParts = doc.reference.path.split("/")
                    if (pathParts.size >= 7) {
                        val classId = pathParts[1]
                        val subjectId = pathParts[3]
                        val unitId = pathParts[5]
                        
                        val title = doc.getString("title") ?: ""
                        val videoUrl = doc.getString("videoUrl") ?: ""
                        val timestamp = doc.getLong("timestamp") ?: 0L
                        

                        val isWithin24Hours = (currentTime - timestamp) <= oneDayInMillis
                        
                        if (title.isNotEmpty() && isWithin24Hours) {
                            if (teacherSubject == "General" || subjectId.equals(teacherSubject, ignoreCase = true)) {
                                currentVideos.add(TeacherContentItem(
                                    id = doc.id, classId = classId, subjectId = subjectId, unitId = unitId,
                                    type = "Video", title = title, url = videoUrl, description = doc.getString("description") ?: "",
                                    timestamp = timestamp
                                ))
                            }
                        }
                    }
                }
                updateTeacherContentList()
            }
    }

    private fun updateTeacherContentList() {

        val allContent = (currentNotes + currentVideos).sortedByDescending { it.timestamp }

        if (allContent.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            rvTeacherContent.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            rvTeacherContent.visibility = View.VISIBLE
            rvTeacherContent.adapter = TeacherContentAdapter(allContent, 
                onOpen = { openContent(it) },
                onDelete = { deleteContent(it) }
            )
        }
    }

    private fun setupSubjectCards(recyclerView: RecyclerView) {
        val subjects = listOf(
            SubjectCard("English", "🐰"),
            SubjectCard("Hindi", "🐒"),
            SubjectCard("Maths", "🐘"),
            SubjectCard("Science", "🔬"),
            SubjectCard("EVS", "🌍"),
            SubjectCard("SST", "🏛️")
        )
        recyclerView.adapter = SubjectCardAdapter(subjects, "All") { selected ->
            val intent = Intent(this, TeacherContentViewActivity::class.java)
            val subjectId = selected.name.lowercase()
            intent.putExtra("SUBJECT_ID", subjectId)
            intent.putExtra("SUBJECT_NAME", selected.name)
            startActivity(intent)
        }
    }

    private fun openContent(item: TeacherContentItem) {
        val url = item.url
        if (url.isEmpty()) return
        
        val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
        intent.putExtra("WEB_URL", url)
        intent.putExtra("WEB_TITLE", item.title)
        startActivity(intent)
    }

    private fun deleteContent(item: TeacherContentItem) {
        val className = item.classId.replace("_", " ").replaceFirstChar { it.uppercase() }
        AlertDialog.Builder(this)
            .setTitle("Delete Content")
            .setMessage("Delete \"${item.title}\" from $className?")
            .setPositiveButton("Delete") { _, _ -> performDelete(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDelete(item: TeacherContentItem) {
        lifecycleScope.launch {
            try {
                val collection = if (item.type == "Video") "videos" else "notes"
                db.collection("classes").document(item.classId)
                    .collection("subjects").document(item.subjectId)
                    .collection("units").document(item.unitId)
                    .collection(collection)
                    .document(item.id)
                    .delete()
                    .await()
                Toast.makeText(this@TeacherDashboardActivity, "Deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@TeacherDashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class TeacherContentItem(
        val id: String, val classId: String, val subjectId: String, val unitId: String,
        val type: String, val title: String, val url: String, val description: String,
        val timestamp: Long = 0L
    )

    private class TeacherContentAdapter(
        private val items: List<TeacherContentItem>,
        private val onOpen: (TeacherContentItem) -> Unit,
        private val onDelete: (TeacherContentItem) -> Unit
    ) : RecyclerView.Adapter<TeacherContentAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvType: TextView = view.findViewById(R.id.tvContentType)
            val tvTitle: TextView = view.findViewById(R.id.tvContentTitle)
            val tvLocation: TextView = view.findViewById(R.id.tvContentLocation)
            val tvDescription: TextView = view.findViewById(R.id.tvContentDescription)
            val btnOpen: Button = view.findViewById(R.id.btnOpenContent)
            val btnDelete: Button = view.findViewById(R.id.btnDeleteContent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher_content_dashboard, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvType.text = item.type
            holder.tvTitle.text = item.title
            holder.tvLocation.text = "${item.classId} / ${item.subjectId} / ${item.unitId}"
            holder.tvDescription.text = item.description
            holder.btnOpen.setOnClickListener { onOpen(item) }
            holder.btnDelete.setOnClickListener { onDelete(item) }
        }

        override fun getItemCount() = items.size
    }

    data class SubjectCard(val name: String, val icon: String)

    private class SubjectCardAdapter(
        private val subjects: List<SubjectCard>,
        private var selectedFilter: String,
        private val onSubjectClick: (SubjectCard) -> Unit
    ) : RecyclerView.Adapter<SubjectCardAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(8, 8, 8, 8) }
                setPadding(16, 24, 16, 24)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setTextColor(android.graphics.Color.WHITE)
                background = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.bg_gradient_blue)
            }
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subject = subjects[position]
            holder.tvName.text = "${subject.icon} ${subject.name}"
            
            if (subject.name == selectedFilter) {
                holder.tvName.background = androidx.core.content.ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_gradient_green)
            } else {
                holder.tvName.background = androidx.core.content.ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_gradient_blue)
            }

            holder.itemView.setOnClickListener {
                selectedFilter = subject.name
                onSubjectClick(subject)
            }
        }

        override fun getItemCount() = subjects.size
    }

    override fun onDestroy() {
        super.onDestroy()
        notesListenerRegistration?.remove()
        videosListenerRegistration?.remove()
    }
}