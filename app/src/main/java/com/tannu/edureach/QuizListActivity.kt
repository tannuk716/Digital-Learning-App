package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.data.repository.ContentRepository
import com.tannu.edureach.practice.QuizActivity
import kotlinx.coroutines.launch

class QuizListActivity : AppCompatActivity() {

    private lateinit var rvQuizzes: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var tvSubjectTitle: TextView
    private val repository = ContentRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        val classId = intent.getStringExtra("CLASS_ID") ?: "class_1"
        val subjectId = intent.getStringExtra("SUBJECT_ID") ?: "maths"
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"

        rvQuizzes = findViewById(R.id.rvQuizzes)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)

        tvSubjectTitle.text = "$subjectName Quizzes"
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvQuizzes.layoutManager = LinearLayoutManager(this)

        loadQuizzes(classId, subjectId)
    }

    private fun loadQuizzes(classId: String, subjectId: String) {
        android.util.Log.d("QuizList", "Loading quizzes for classId=$classId, subjectId=$subjectId")
        

        val subjectIds = when (subjectId.lowercase()) {
            "maths", "math" -> listOf("maths", "math")
            else -> listOf(subjectId)
        }
        
        lifecycleScope.launch {

            val units = listOf("unit_1", "unit_2", "unit_3", "unit_4", "unit_5", "unit_6", "unit_7", "unit_8", "unit_9", "unit_10")
            val allQuizzes = mutableListOf<QuizWithUnit>()

            for (searchSubjectId in subjectIds) {
                android.util.Log.d("QuizList", "Searching with subjectId: $searchSubjectId")
                
                for (unit in units) {
                    repository.getQuizzes(classId, searchSubjectId, unit).collect { quizzes ->
                        android.util.Log.d("QuizList", "[$searchSubjectId] Found ${quizzes.size} quizzes in $unit")
                        
                        quizzes.forEach { quiz ->

                            if (allQuizzes.none { it.quiz.id == quiz.id }) {
                                allQuizzes.add(QuizWithUnit(quiz, unit))
                            }
                        }
                        

                        if (allQuizzes.isEmpty()) {
                            tvEmptyState.visibility = View.VISIBLE
                            tvEmptyState.text = "No quizzes available yet.\nTeachers can create quizzes for this subject."
                            rvQuizzes.visibility = View.GONE
                        } else {
                            tvEmptyState.visibility = View.GONE
                            rvQuizzes.visibility = View.VISIBLE
                            rvQuizzes.adapter = QuizAdapter(allQuizzes) { quizWithUnit ->
                                com.tannu.edureach.utils.ProgressManager.awardPoints(5)
                                val intent = Intent(this@QuizListActivity, QuizActivity::class.java)
                                intent.putExtra("QUIZ_ID", quizWithUnit.quiz.id)
                                intent.putExtra("CLASS_ID", classId)
                                intent.putExtra("SUBJECT_ID", searchSubjectId)
                                intent.putExtra("UNIT_ID", quizWithUnit.unitId)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }

    data class QuizWithUnit(val quiz: QuizModel, val unitId: String)

    private class QuizAdapter(
        private val quizzes: List<QuizWithUnit>,
        private val onClick: (QuizWithUnit) -> Unit
    ) : RecyclerView.Adapter<QuizAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvQuizTitle: TextView = view.findViewById(R.id.tvQuizTitle)
            val tvQuizInfo: TextView = view.findViewById(R.id.tvQuizInfo)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quiz, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val quizWithUnit = quizzes[position]
            holder.tvQuizTitle.text = quizWithUnit.quiz.title
            holder.tvQuizInfo.text = "${quizWithUnit.quiz.questions.size} Questions"
            holder.itemView.setOnClickListener { onClick(quizWithUnit) }
        }

        override fun getItemCount() = quizzes.size
    }
}