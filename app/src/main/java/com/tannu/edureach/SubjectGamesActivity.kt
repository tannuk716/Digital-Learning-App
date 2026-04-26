package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SubjectGamesActivity : AppCompatActivity() {

    private lateinit var rvSubjects: RecyclerView
    private lateinit var tvEmptyState: TextView
    private var currentClassId = "class_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_games)

        rvSubjects = findViewById(R.id.rvSubjects)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvSubjects.layoutManager = GridLayoutManager(this, 2)

        loadUserClass()
    }

    private fun loadUserClass() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val className = document.getString("className") ?: "Class 1"
                    val classInt = className.replace("Class ", "").toIntOrNull() ?: 1
                    currentClassId = "class_$classInt"
                    loadSubjects()
                }
            }
            .addOnFailureListener {
                loadSubjects()
            }
    }

    private fun loadSubjects() {
        val subjects = listOf(
            SubjectItem("Maths", "maths", "#9B59B6", "#8E44AD", "🐘"),
            SubjectItem("English", "english", "#7B68EE", "#6A5ACD", "🐰"),
            SubjectItem("Hindi", "hindi", "#9370DB", "#8A2BE2", "🐵"),
            SubjectItem("Science", "science", "#5DADE2", "#3498DB", "🌍"),
            SubjectItem("SST", "sst", "#AF7AC5", "#9B59B6", "🏛️"),
            SubjectItem("EVS", "evs", "#85C1E9", "#5DADE2", "🌳")
        )

        rvSubjects.adapter = SubjectAdapter(subjects) { subject ->
            val intent = Intent(this, GameListActivity::class.java)
            intent.putExtra("CLASS_ID", currentClassId)
            intent.putExtra("SUBJECT_ID", subject.id)
            intent.putExtra("SUBJECT_NAME", subject.name)
            startActivity(intent)
        }
    }

    data class SubjectItem(val name: String, val id: String, val colorStart: String, val colorEnd: String, val icon: String)

    private class SubjectAdapter(
        private val subjects: List<SubjectItem>,
        private val onClick: (SubjectItem) -> Unit
    ) : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvSubjectName: TextView = view.findViewById(R.id.tvSubjectName)
            val tvSubjectIcon: TextView = view.findViewById(R.id.tvSubjectIcon)
            val tvSubjectDesc: android.view.View = view.findViewById(R.id.tvSubjectDesc)
            val cardBackground: android.view.View = view.findViewById(R.id.cardBackground)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject_card, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val subject = subjects[position]
            holder.tvSubjectName.text = subject.name
            

            holder.tvSubjectIcon.text = subject.icon
            

            val gradientDrawable = android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    android.graphics.Color.parseColor(subject.colorStart),
                    android.graphics.Color.parseColor(subject.colorEnd)
                )
            )
            gradientDrawable.cornerRadius = 60f
            holder.cardBackground.background = gradientDrawable
            
            holder.itemView.setOnClickListener { onClick(subject) }
        }

        override fun getItemCount() = subjects.size
    }
}