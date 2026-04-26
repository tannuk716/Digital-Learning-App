package com.tannu.edureach.learn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R
import com.tannu.edureach.data.OfflineDataLoader

class LearnHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_hub)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        if (btnBack != null) {
            btnBack.setOnClickListener { finish() }
        }

        val rvSubjects = findViewById<RecyclerView>(R.id.rvSubjects)
        rvSubjects.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userClass = prefs.getInt("USER_CLASS", 1)

        val classes = OfflineDataLoader.loadOfflineData(this)
        val currentClassObj = classes.find { it.id == userClass }
        

        val subjects = currentClassObj?.subjects ?: classes.find { it.id == 1 }?.subjects ?: emptyList()

        rvSubjects.adapter = SubjectAdapter(subjects) { subject ->
            val intent = android.content.Intent(this, UnitListActivity::class.java)
            intent.putExtra("SUBJECT_NAME", subject.name)
            startActivity(intent)
        }
    }

    inner class SubjectAdapter(
        private val subjects: List<OfflineDataLoader.Subject>,
        private val onClick: (OfflineDataLoader.Subject) -> Unit
    ) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

        inner class SubjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: TextView = view.findViewById(R.id.subjectIcon)
            val name: TextView = view.findViewById(R.id.subjectName)
            val desc: TextView = view.findViewById(R.id.subjectDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject, parent, false)
            return SubjectViewHolder(view)
        }

        override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
            val subject = subjects[position]
            holder.icon.text = subject.icon
            holder.name.text = subject.name
            holder.desc.text = "${subject.units.size} Units"
            holder.itemView.setOnClickListener { onClick(subject) }
        }

        override fun getItemCount() = subjects.size
    }
}