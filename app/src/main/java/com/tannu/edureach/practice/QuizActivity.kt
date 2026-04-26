package com.tannu.edureach.practice

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tannu.edureach.R
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.data.repository.ContentRepository
import com.tannu.edureach.utils.ThemeManager
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class QuizActivity : AppCompatActivity() {

    private lateinit var tvQuizTitle: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvQuestionProgress: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var pbQuizProgress: ProgressBar
    
    private lateinit var btnOptions: List<Button>
    private lateinit var btnPrev: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmitQuiz: Button

    private val repository = ContentRepository()
    private var quizModel: QuizModel? = null
    private var currentQuestionIndex = 0
    
    private val selectedAnswers = mutableMapOf<Int, Int>()
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_quiz)

        tvQuizTitle = findViewById(R.id.tvQuizTitle)
        tvTimer = findViewById(R.id.tvTimer)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        pbQuizProgress = findViewById(R.id.pbQuizProgress)

        btnOptions = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            findViewById(R.id.btnOption4)
        )
        
        btnPrev = findViewById(R.id.btnPrev)
        btnNext = findViewById(R.id.btnNext)
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz)

        val quizId = intent.getStringExtra("QUIZ_ID")
        val classId = intent.getStringExtra("CLASS_ID")
        val subjectId = intent.getStringExtra("SUBJECT_ID")
        val unitId = intent.getStringExtra("UNIT_ID")

        if (quizId.isNullOrEmpty() || classId.isNullOrEmpty() || subjectId.isNullOrEmpty() || unitId.isNullOrEmpty()) {
            Toast.makeText(this, "Quiz details missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnPrev.setOnClickListener { navigateQuestion(-1) }
        btnNext.setOnClickListener { navigateQuestion(1) }
        btnSubmitQuiz.setOnClickListener { submitQuiz() }

        loadQuizData(classId, subjectId, unitId, quizId)
    }

    private fun loadQuizData(classId: String, subjectId: String, unitId: String, quizId: String) {
        lifecycleScope.launch {
            quizModel = repository.getQuizById(classId, subjectId, unitId, quizId)
            if (quizModel != null && quizModel!!.questions.isNotEmpty()) {
                tvQuizTitle.text = quizModel!!.title
                pbQuizProgress.max = quizModel!!.questions.size
                startTimer(10 * 60 * 1000)
                showQuestion(0)
            } else {
                Toast.makeText(this@QuizActivity, "Failed to load quiz.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startTimer(durationMillis: Long) {
        timeLeftInMillis = durationMillis
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = (timeLeftInMillis / 1000) / 60
                val seconds = (timeLeftInMillis / 1000) % 60
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                Toast.makeText(this@QuizActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                submitQuiz()
            }
        }.start()
    }

    private fun showQuestion(index: Int) {
        val questions = quizModel?.questions ?: return
        if (index < 0 || index >= questions.size) return
        
        currentQuestionIndex = index
        val question = questions[index]
        
        tvQuestionProgress.text = "Question ${index + 1}/${questions.size}"
        pbQuizProgress.progress = index + 1
        tvQuestionText.text = question.text
        
        val previouslySelected = selectedAnswers[index]

        for (i in btnOptions.indices) {
            val btn = btnOptions[i]
            if (i < question.options.size) {
                btn.visibility = View.VISIBLE
                btn.text = question.options[i]
                
                if (previouslySelected == i) {
                    btn.setBackgroundColor(Color.parseColor("#4CAF50"))
                } else {
                    btn.setBackgroundColor(Color.parseColor("#FFB74D"))
                }

                btn.setOnClickListener {
                    selectedAnswers[currentQuestionIndex] = i
                    showQuestion(currentQuestionIndex) 
                }
            } else {
                btn.visibility = View.GONE
            }
        }

        btnPrev.isEnabled = index > 0
        btnPrev.setBackgroundColor(if (index > 0) Color.parseColor("#FF9800") else Color.parseColor("#9E9E9E"))
        
        if (index == questions.size - 1) {
            btnNext.visibility = View.GONE
            btnSubmitQuiz.visibility = View.VISIBLE
        } else {
            btnNext.visibility = View.VISIBLE
            btnSubmitQuiz.visibility = View.GONE
        }
    }

    private fun navigateQuestion(direction: Int) {
        showQuestion(currentQuestionIndex + direction)
    }

    private fun submitQuiz() {
        timer?.cancel()
        btnSubmitQuiz.isEnabled = false
        
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val questions = quizModel?.questions ?: return
        
        var score = 0
        val explanationsArray = ArrayList<String>()
        val correctAnswersArray = ArrayList<String>()
        
        for (i in questions.indices) {
            val q = questions[i]
            val selected = selectedAnswers[i] ?: -1
            
            if (selected == q.correctIndex) {
                score++
            }
            explanationsArray.add(q.explanation)
            correctAnswersArray.add(q.options.getOrElse(q.correctIndex) { "Invalid Mapping" })
        }
        
        val db = FirebaseFirestore.getInstance()
        val resultData = mapOf(
            "quizId" to quizModel!!.id,
            "title" to quizModel!!.title,
            "score" to score,
            "total" to questions.size,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("users").document(uid).collection("quiz_results").add(resultData)
        
        val intent = Intent(this, QuizResultActivity::class.java)
        intent.putExtra("SCORE", score)
        intent.putExtra("TOTAL", questions.size)
        intent.putStringArrayListExtra("EXPLANATIONS", explanationsArray)
        intent.putStringArrayListExtra("CORRECT_ANSWERS", correctAnswersArray)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}