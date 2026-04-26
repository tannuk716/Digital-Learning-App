package com.tannu.edureach.practice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R
import com.tannu.edureach.data.QuizRepository

class PracticeHubActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice_hub)
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        val rvPracticeSubjects = findViewById<RecyclerView>(R.id.rvPracticeSubjects)
        rvPracticeSubjects.layoutManager = LinearLayoutManager(this)

        val categories = listOf(
            PracticeCategory("English", "📘", QuizRepository.getAllQuestions().count { it.category == "English" }),
            PracticeCategory("Mathematics", "📗", QuizRepository.getAllQuestions().count { it.category == "Math" }),
            PracticeCategory("EVS", "🌿", QuizRepository.getAllQuestions().count { it.category == "EVS" }),
            PracticeCategory("General Knowledge", "🧠", QuizRepository.getAllQuestions().count { it.category == "GK" })
        )

        rvPracticeSubjects.adapter = PracticeAdapter(categories) { category ->
            val intent = android.content.Intent(this, QuizActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.name)
            startActivity(intent)
        }
    }

    data class PracticeCategory(val name: String, val icon: String, val questionCount: Int)

    inner class PracticeAdapter(
        private val categories: List<PracticeCategory>,
        private val onClick: (PracticeCategory) -> Unit
    ) : RecyclerView.Adapter<PracticeAdapter.PracticeViewHolder>() {

        inner class PracticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: TextView = view.findViewById(R.id.subjectIcon)
            val name: TextView = view.findViewById(R.id.subjectName)
            val desc: TextView = view.findViewById(R.id.subjectDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject, parent, false)
            return PracticeViewHolder(view)
        }

        override fun onBindViewHolder(holder: PracticeViewHolder, position: Int) {
            val category = categories[position]
            holder.icon.text = category.icon
            holder.name.text = category.name
            holder.desc.text = "${category.questionCount} Questions Available"
            holder.itemView.setOnClickListener { onClick(category) }
        }

        override fun getItemCount() = categories.size
    }
}