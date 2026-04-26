package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SubjectNotesActivity : AppCompatActivity() {

    private lateinit var rvSubjects: RecyclerView
    private lateinit var tvEmptyState: TextView
    private var currentClassId = "class_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_notes)
        
        currentClassId = intent.getStringExtra("CLASS_ID") ?: "class_1"

        rvSubjects = findViewById(R.id.rvSubjects)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvSubjects.layoutManager = GridLayoutManager(this, 2)

        loadSubjects()
    }

    private fun loadSubjects() {
        android.util.Log.d("SubjectNotes", "Loading subjects for class: $currentClassId")
        

        val allSubjects = listOf(
            SubjectItem("Maths", "maths", "📗"),
            SubjectItem("English", "english", "📘"),
            SubjectItem("Hindi", "hindi", "📙"),
            SubjectItem("Science", "science", "🔬"),
            SubjectItem("EVS", "evs", "🌍"),
            SubjectItem("SST", "sst", "🏛️")
        )

        android.util.Log.d("SubjectNotes", "Creating adapter with ${allSubjects.size} subjects")
        
        val adapter = TeacherNotesSubjectAdapter(allSubjects) { subject ->
            android.util.Log.d("SubjectNotes", "Opening NoteListActivity for ${subject.name}")
            val intent = Intent(this, NoteListActivity::class.java)
            intent.putExtra("CLASS_ID", currentClassId)
            intent.putExtra("SUBJECT_ID", subject.id)
            intent.putExtra("SUBJECT_NAME", subject.name)
            intent.putExtra("TEACHER_ONLY", true)
            startActivity(intent)
        }
        rvSubjects.adapter = adapter
        rvSubjects.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        
        android.util.Log.d("SubjectNotes", "Subjects loaded successfully")
    }

    data class SubjectItem(val name: String, val id: String, val icon: String)

    private class TeacherNotesSubjectAdapter(
        private val subjects: List<SubjectItem>,
        private val onSubjectClick: (SubjectItem) -> Unit
    ) : RecyclerView.Adapter<TeacherNotesSubjectAdapter.ViewHolder>() {

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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subject = subjects[position]
            
            holder.tvSubjectName.text = subject.name
            holder.tvSubjectIcon.text = subject.icon

            val bgRes = colorGradients[position % colorGradients.size]
            holder.rootLayout.setBackgroundResource(bgRes)

            holder.rootLayout.setOnClickListener {
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
}