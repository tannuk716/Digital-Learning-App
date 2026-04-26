package com.tannu.edureach

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tannu.edureach.data.model.QuestionModel
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.data.repository.ContentRepository
import kotlinx.coroutines.launch

class AddQuizActivity : AppCompatActivity() {

    private val questions = mutableListOf<QuestionModel>()
    private val repository = ContentRepository()

    private var classId = ""
    private var subjectId = ""
    private var unitId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)

        classId = intent.getStringExtra("CLASS_ID") ?: ""
        subjectId = intent.getStringExtra("SUBJECT_ID") ?: ""
        unitId = intent.getStringExtra("UNIT_ID") ?: ""

        val etQuizTitle = findViewById<EditText>(R.id.etQuizTitle)
        val tvQuestionCount = findViewById<TextView>(R.id.tvQuestionCount)
        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val etOption1 = findViewById<EditText>(R.id.etOption1)
        val etOption2 = findViewById<EditText>(R.id.etOption2)
        val etOption3 = findViewById<EditText>(R.id.etOption3)
        val etOption4 = findViewById<EditText>(R.id.etOption4)
        val etCorrectIndex = findViewById<EditText>(R.id.etCorrectIndex)
        val etExplanation = findViewById<EditText>(R.id.etExplanation)

        val btnAddQuestion = findViewById<Button>(R.id.btnAddQuestion)
        val btnPublishQuiz = findViewById<Button>(R.id.btnPublishQuiz)

        btnAddQuestion.setOnClickListener {
            val qText = etQuestion.text.toString().trim()
            val o1 = etOption1.text.toString().trim()
            val o2 = etOption2.text.toString().trim()
            val o3 = etOption3.text.toString().trim()
            val o4 = etOption4.text.toString().trim()
            val correctStr = etCorrectIndex.text.toString().trim()
            val explanation = etExplanation.text.toString().trim()

            if (qText.isEmpty() || o1.isEmpty() || o2.isEmpty() || o3.isEmpty() || o4.isEmpty() || correctStr.isEmpty()) {
                Toast.makeText(this, "Please fill all question fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val correctInt = correctStr.toIntOrNull()
            if (correctInt == null || correctInt !in 1..4) {
                Toast.makeText(this, "Correct Index must be between 1 and 4.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val question = QuestionModel(
                text = qText,
                options = listOf(o1, o2, o3, o4),
                correctIndex = correctInt - 1,
                explanation = explanation
            )
            
            questions.add(question)
            tvQuestionCount.text = "Questions Added: ${questions.size}"
            
            etQuestion.text.clear()
            etOption1.text.clear()
            etOption2.text.clear()
            etOption3.text.clear()
            etOption4.text.clear()
            etCorrectIndex.text.clear()
            etExplanation.text.clear()
            
            Toast.makeText(this, "Question Added!", Toast.LENGTH_SHORT).show()
            etQuestion.requestFocus()
        }

        btnPublishQuiz.setOnClickListener {
            val title = etQuizTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a Quiz Title.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (questions.isEmpty()) {
                Toast.makeText(this, "Please add at least 1 question.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (classId.isEmpty()) {
                Toast.makeText(this, "Missing Navigation Arguments. Cannot publish.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnPublishQuiz.isEnabled = false
            btnAddQuestion.isEnabled = false
            btnPublishQuiz.text = "Publishing..."

            val quiz = QuizModel(
                title = title,
                questions = questions
            )
            
            lifecycleScope.launch {
                val success = repository.uploadQuiz(classId, subjectId, unitId, quiz)
                if (success) {
                    Toast.makeText(this@AddQuizActivity, "Quiz Published Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddQuizActivity, "Duplicate quiz detected! A quiz with this title already exists.", Toast.LENGTH_LONG).show()
                    btnPublishQuiz.isEnabled = true
                    btnAddQuestion.isEnabled = true
                    btnPublishQuiz.text = "Finish & Publish Quiz"
                }
            }
        }
    }
}