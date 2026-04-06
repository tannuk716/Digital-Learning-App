package com.tannu.edureach

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.model.RecentUploadModel
import com.tannu.edureach.ui.viewmodel.StudentViewModel
import com.tannu.edureach.data.model.Subject
import com.tannu.edureach.utils.ProgressManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tannu.edureach.data.model.OfflineData
import java.io.InputStreamReader

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var dashAvatar: ImageView
    private lateinit var dashGreetingText: TextView
    private lateinit var dashClassText: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    
    // ViewModels and Adapters
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var recentAdapter: RecentUploadsAdapter
    private lateinit var rvRecentContent: RecyclerView
    private lateinit var tvEmptyState: TextView
    
    private lateinit var rvSubjects: RecyclerView
    
    private var currentClassId = "class_1"
    private val contentRepository = com.tannu.edureach.data.repository.ContentRepository()
    private var offlineData: OfflineData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.tannu.edureach.utils.ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_student_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        dashAvatar = findViewById(R.id.dashAvatar)
        dashGreetingText = findViewById(R.id.dashGreetingText)
        dashClassText = findViewById(R.id.dashClassText)
        rvRecentContent = findViewById(R.id.rvRecentContent)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        
        rvRecentContent.layoutManager = LinearLayoutManager(this)
        recentAdapter = RecentUploadsAdapter(emptyList()) { content ->
            openRecentContent(content)
        }
        rvRecentContent.adapter = recentAdapter

        rvSubjects = findViewById(R.id.rvSubjects)
        rvSubjects.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        findViewById<View>(R.id.btnNavHome).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        
        findViewById<View>(R.id.btnNavStar).setOnClickListener {
            startActivity(Intent(this, ProgressActivity::class.java))
        }

        // 🤖 Open AI Tutor
        findViewById<View>(R.id.cardAIChat).setOnClickListener {
            startActivity(Intent(this, AIChatbotActivity::class.java))
        }

        // 🎤 Communication Skills (Class 5-10 only)
        findViewById<View>(R.id.cardCommunicationSkills)?.setOnClickListener {
            startActivity(Intent(this, CommunicationSkillsActivity::class.java))
        }

        dashAvatar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupClickListeners()
        setupFilters()
        loadProfileData()
        observeContent() // Initial load
        
        // Load subjects immediately with default class (will be updated when profile loads)
        loadSubjectsFromJson()
        
        // Clean out legacy Notion junk from database globally
        cleanNotionFiles()
        cleanNestedDummyContent()
    }

    private fun cleanNestedDummyContent() {
        // Runs passively to clean garbage Notion/AWS/Dummy links deeply nested in Class 1
        val subjectsToClean = listOf("maths", "english", "hindi", "science", "evs", "sst")
        for (sub in subjectsToClean) {
            for (i in 1..10) {
                val unitId = "unit_$i"
                
                // Clean Notes
                db.collection("classes").document("class_1")
                    .collection("subjects").document(sub)
                    .collection("units").document(unitId)
                    .collection("notes").get().addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            val url = doc.getString("fileUrl") ?: ""
                            val title = doc.getString("title") ?: ""
                            if (url.contains("notion", true) || url.contains("amazonaws", true) || 
                                title.contains("dummy", true) || title.contains("test", true) ||
                                url.contains("dummy.pdf", true)) {
                                doc.reference.delete()
                            }
                        }
                    }
                
                // Clean Videos
                db.collection("classes").document("class_1")
                    .collection("subjects").document(sub)
                    .collection("units").document(unitId)
                    .collection("videos").get().addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            val url = doc.getString("videoUrl") ?: ""
                            val title = doc.getString("title") ?: ""
                            if (url.contains("notion", true) || url.contains("amazonaws", true) || 
                                title.contains("dummy", true) || title.contains("test", true) ||
                                url.contains("dummy.pdf", true)) {
                                doc.reference.delete()
                            }
                        }
                    }
            }
        }
    }

    private fun cleanNotionFiles() {
        db.collection("recent_uploads").get().addOnSuccessListener { snapshot ->
            snapshot.documents.forEach { doc ->
                val url = doc.getString("url") ?: ""
                if (url.contains("notion", ignoreCase = true)) {
                    android.util.Log.d("StudentDashboard", "Deleting Notion document: ${doc.id}")
                    db.collection("recent_uploads").document(doc.id).delete()
                }
            }
        }
    }

    private fun setupFilters() {
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubjectFilter)
        val spinnerUnit = findViewById<Spinner>(R.id.spinnerUnitFilter)
        
        val filterListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sub = spinnerSubject.selectedItem.toString()
                val un = spinnerUnit.selectedItem.toString()
                
                if (!sub.contains("Select") && !un.contains("Select")) {
                    val subId = sub.replace(" ", "_").lowercase()
                    val unId = un.replace(" ", "_").lowercase()
                    viewModel.updateFilters(currentClassId, subId, unId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        spinnerSubject.onItemSelectedListener = filterListener
        spinnerUnit.onItemSelectedListener = filterListener
    }

    private fun observeContent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                contentRepository.getRecentUploadsByClass(currentClassId).collect { list ->
                    android.util.Log.d("StudentDashboard", "Received ${list.size} items for class $currentClassId")
                    
                    // Log all items before filtering
                    list.forEach { item ->
                        android.util.Log.d("StudentDashboard", "Item: ${item.title} | URL: ${item.url} | Contains notion: ${item.url.contains("notion.so", ignoreCase = true)}")
                    }
                    
                    val currentTime = System.currentTimeMillis()
                    val oneDayInMillis = 24 * 60 * 60 * 1000L

                    // Show valid manually-uploaded items within the last 24 hours (notion blocked) and filtered by student class/subject
                    val recent24hList = list
                        .filter { 
                            val isNotionContent = it.url.contains("notion", ignoreCase = true)
                            val isWithin24Hours = (currentTime - it.timestamp) <= oneDayInMillis
                            
                            // Check if subject is part of student's subjects. For simplicity, any valid standard subject implies student subject for their class.
                            // If we need stricter filtering, we ensure the `it.subjectId` is valid.
                            val isStudentSubject = it.subjectId.isNotEmpty()
                            
                            android.util.Log.d("StudentDashboard", "Filtering ${it.title}: isNotion=$isNotionContent, isWithin24h=$isWithin24Hours")
                            !isNotionContent && isWithin24Hours && isStudentSubject
                        }
                        .take(15)
                    
                    android.util.Log.d("StudentDashboard", "After filtering: ${recent24hList.size} items")
                    
                    if (recent24hList.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        tvEmptyState.text = "No teacher content uploaded yet.\nTeachers can upload notes and videos for your class."
                        rvRecentContent.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvRecentContent.visibility = View.VISIBLE
                        recentAdapter.updateData(recent24hList)
                    }
                }
            }
        }
    }
    
    private fun openRecentContent(content: RecentUploadModel) {
        val url = content.url
        if (url.isEmpty()) {
            Toast.makeText(this, "Content URL not available.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if it's a YouTube URL even if flag is false
        val isYouTubeUrl = url.contains("youtube.com", ignoreCase = true) || 
                          url.contains("youtu.be", ignoreCase = true)

        if (content.type == "Video") {
            if (content.isYoutube || isYouTubeUrl) {
                val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
                intent.putExtra("WEB_URL", url)
                intent.putExtra("WEB_TITLE", content.title)
                startActivity(intent)
            } else {
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra("VIDEO_URL", url)
                intent.putExtra("IS_OFFLINE", false)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
            intent.putExtra("WEB_URL", url)
            intent.putExtra("WEB_TITLE", content.title)
            startActivity(intent)
        }
        
        // Opening an item implies study activity, so update streak!
        ProgressManager.updateStreak()
    }

    override fun onResume() {
        super.onResume()
        // Reload profile data when returning from ProfileActivity
        loadProfileData()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun loadProfileData() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Student"
                    val className = document.getString("className") ?: "Class 1"
                    val avatar = document.getString("avatar") ?: "avatar_lion"

                    dashGreetingText.text = "Hello, $name!"
                    dashClassText.text = className
                    
                    val classInt = className.replace("Class ", "").toIntOrNull() ?: 1
                    val newClassId = "class_$classInt"
                    
                    // Show/Hide Communication Skills card based on class (Class 5-10 only)
                    val cardCommunicationSkills = findViewById<View>(R.id.cardCommunicationSkills)
                    if (classInt >= 5 && classInt <= 10) {
                        cardCommunicationSkills?.visibility = View.VISIBLE
                    } else {
                        cardCommunicationSkills?.visibility = View.GONE
                    }
                    
                    // If class changed, reload content
                    if (newClassId != currentClassId) {
                        currentClassId = newClassId
                        observeContent() // Reload content for new class
                    }
                    
                    // Reset Viewmodel filter with right class
                    val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubjectFilter)
                    val spinnerUnit = findViewById<Spinner>(R.id.spinnerUnitFilter)
                    if (spinnerSubject.selectedItemPosition > 0 && spinnerUnit.selectedItemPosition > 0) {
                        val subId = spinnerSubject.selectedItem.toString().replace(" ", "_").lowercase()
                        val unId = spinnerUnit.selectedItem.toString().replace(" ", "_").lowercase()
                        viewModel.updateFilters(currentClassId, subId, unId)
                    }
                    
                    loadSubjectsFromJson()

                    val resourceId = resources.getIdentifier(avatar, "drawable", packageName)
                    if (resourceId != 0) {
                        dashAvatar.setImageResource(resourceId)
                    }
                }
            }
    }

    private fun loadSubjectsFromJson() {
        android.util.Log.d("StudentDashboard", "loadSubjectsFromJson called with currentClassId=$currentClassId")
        
        // For Class 1, use predefined PDF content
        if (currentClassId == "class_1") {
            android.util.Log.d("StudentDashboard", "Loading Class 1 content")
            val class1Subjects = com.tannu.edureach.utils.Class1ContentProvider.getClass1Content()
            android.util.Log.d("StudentDashboard", "Class 1 subjects count: ${class1Subjects.size}")
            
            val subjectItems = class1Subjects.map { subject ->
                val icon = when (subject.subjectId) {
                    "maths" -> "🐘"
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    else -> "📚"
                }
                android.util.Log.d("StudentDashboard", "Creating SubjectItem: name=${subject.subjectName}, id=${subject.subjectId}, icon=$icon")
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            android.util.Log.d("StudentDashboard", "Creating adapter with ${subjectItems.size} items")
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                // Open unit list for Class 1
                android.util.Log.d("StudentDashboard", "Opening Class1UnitListActivity for ${subject.name}")
                val intent = Intent(this, Class1UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            android.util.Log.d("StudentDashboard", "RecyclerView visibility set to VISIBLE")
            return
        }
        
        // For Class 2, use specific localized exact links mapping
        if (currentClassId == "class_2") {
            android.util.Log.d("StudentDashboard", "Loading Class 2 fixed content")
            val class2Subjects = com.tannu.edureach.utils.Class2ContentProvider.getClass2Content()
            
            val subjectItems = class2Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "evs" -> "🌍"
                    "maths" -> "🐘"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                android.util.Log.d("StudentDashboard", "Opening Class2UnitListActivity for ${subject.name}")
                val intent = Intent(this, Class2UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }
        
        // For Class 3, use specific localized exact links mapping
        if (currentClassId == "class_3") {
            android.util.Log.d("StudentDashboard", "Loading Class 3 fixed content")
            val class3Subjects = com.tannu.edureach.utils.Class3ContentProvider.getClass3Content()
            
            val subjectItems = class3Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "evs" -> "🌍"
                    "maths" -> "🐘"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                android.util.Log.d("StudentDashboard", "Opening Class3UnitListActivity for ${subject.name}")
                val intent = Intent(this, Class3UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }
        
        // For Class 4
        if (currentClassId == "class_4") {
            android.util.Log.d("StudentDashboard", "Loading Class 4 fixed content")
            val class4Subjects = com.tannu.edureach.utils.Class4ContentProvider.getClass4Content()
            
            val subjectItems = class4Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "evs" -> "🌍"
                    "maths" -> "🐘"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class4UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 5
        if (currentClassId == "class_5") {
            android.util.Log.d("StudentDashboard", "Loading Class 5 fixed content")
            val class5Subjects = com.tannu.edureach.utils.Class5ContentProvider.getClass5Content()
            
            val subjectItems = class5Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "evs" -> "🌍"
                    "maths" -> "🐘"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class5UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 6
        if (currentClassId == "class_6") {
            android.util.Log.d("StudentDashboard", "Loading Class 6 fixed content")
            val class6Subjects = com.tannu.edureach.utils.Class6ContentProvider.getClass6Content()
            
            val subjectItems = class6Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "evs" -> "🌍"
                    "maths" -> "🐘"
                    "sst" -> "🗺️"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class6UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 7
        if (currentClassId == "class_7") {
            android.util.Log.d("StudentDashboard", "Loading Class 7 content")
            val class7Subjects = com.tannu.edureach.utils.Class7ContentProvider.getClass7Content()
            
            val subjectItems = class7Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "science", "evs" -> "🌍"
                    "maths" -> "🐘"
                    "sst" -> "🏛️"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class7UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 8
        if (currentClassId == "class_8") {
            android.util.Log.d("StudentDashboard", "Loading Class 8 content")
            val class8Subjects = com.tannu.edureach.utils.Class8ContentProvider.getClass8Content()
            
            val subjectItems = class8Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "science", "evs" -> "🌍"
                    "maths" -> "🐘"
                    "sst" -> "🏛️"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class8UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 9
        if (currentClassId == "class_9") {
            android.util.Log.d("StudentDashboard", "Loading Class 9 content")
            val class9Subjects = com.tannu.edureach.utils.Class9ContentProvider.getClass9Content()
            
            val subjectItems = class9Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "science", "evs" -> "🌍"
                    "maths" -> "🐘"
                    "sst" -> "🏛️"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class9UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }

        // For Class 10
        if (currentClassId == "class_10") {
            android.util.Log.d("StudentDashboard", "Loading Class 10 content")
            val class10Subjects = com.tannu.edureach.utils.Class10ContentProvider.getClass10Content()
            
            val subjectItems = class10Subjects.map { subject ->
                val icon = when (subject.subjectId.lowercase()) {
                    "english" -> "🐰"
                    "hindi" -> "🐒"
                    "science", "evs" -> "🌍"
                    "maths" -> "🐘"
                    "sst" -> "🏛️"
                    else -> "📚"
                }
                SubjectItem(subject.subjectName, subject.subjectId, icon, "")
            }
            
            val adapter = NotionSubjectAdapter(subjectItems) { subject ->
                val intent = Intent(this, Class10UnitListActivity::class.java)
                intent.putExtra("SUBJECT_ID", subject.id)
                intent.putExtra("SUBJECT_NAME", subject.name)
                startActivity(intent)
                ProgressManager.updateStreak()
            }
            rvSubjects.adapter = adapter
            rvSubjects.visibility = View.VISIBLE
            return
        }
    }
    
    data class SubjectItem(val name: String, val id: String, val icon: String, val url: String)
    
    private class NotionSubjectAdapter(
        private val subjects: List<SubjectItem>,
        private val onSubjectClick: (SubjectItem) -> Unit
    ) : RecyclerView.Adapter<NotionSubjectAdapter.ViewHolder>() {

        private val colorGradients = listOf(
            R.drawable.bg_gradient_blue,
            R.drawable.bg_gradient_orange,
            R.drawable.bg_gradient_green,
            R.drawable.bg_gradient_light_green,
            R.drawable.bg_gradient_purple,
            R.drawable.bg_gradient_dark_blue
        )

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val rootLayout: View = view
            val tvSubjectName: TextView = view.findViewById(R.id.tvSubjectName)
            val tvSubjectIcon: TextView = view.findViewById(R.id.tvSubjectIcon)
            val tvSubjectDesc: View = view.findViewById(R.id.tvSubjectDesc)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subject = subjects[position]
            
            android.util.Log.d("NotionAdapter", "Binding position=$position, subject=${subject.name}, id=${subject.id}")
            
            holder.tvSubjectName.text = subject.name
            holder.tvSubjectIcon.text = subject.icon
            // tvSubjectDesc is now a LinearLayout, so we don't set text on it

            val bgRes = colorGradients[position % colorGradients.size]
            holder.rootLayout.setBackgroundResource(bgRes)

            holder.rootLayout.setOnClickListener {
                android.util.Log.d("NotionAdapter", "Clicked position=$position, subject=${subject.name}, id=${subject.id}")
                onSubjectClick(subject)
            }
            
            holder.rootLayout.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(150).start()
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                    }
                }
                false
            }
        }

        override fun getItemCount() = subjects.size
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.cardGames).setOnClickListener {
            if (currentClassId == "class_1") {
                startActivity(Intent(this, com.tannu.edureach.games.JungleGamesActivity::class.java))
            } else {
                val intent = Intent(this, SubjectGamesActivity::class.java)
                intent.putExtra("CLASS_ID", currentClassId)
                startActivity(intent)
            }
        }
        
        findViewById<View>(R.id.cardAIChat).setOnClickListener {
            startActivity(Intent(this, AIChatbotActivity::class.java))
        }
        
        findViewById<View>(R.id.cardNotes).setOnClickListener {
            val intent = Intent(this, SubjectNotesActivity::class.java)
            intent.putExtra("CLASS_ID", currentClassId)
            startActivity(intent)
        }
        
        findViewById<View>(R.id.cardQuizzes).setOnClickListener {
            val intent = Intent(this, SubjectQuizzesActivity::class.java)
            intent.putExtra("CLASS_ID", currentClassId)
            startActivity(intent)
            ProgressManager.updateStreak()
        }
    }
}
