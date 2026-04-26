package com.tannu.edureach.practice

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R
import com.tannu.edureach.StudentDashboardActivity

class QuizResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)
        val explanations = intent.getStringArrayListExtra("EXPLANATIONS") ?: arrayListOf()
        val correctAnswers = intent.getStringArrayListExtra("CORRECT_ANSWERS") ?: arrayListOf()

        val tvScore: TextView = findViewById(R.id.tvScore)
        tvScore.text = "Your Score: $score/$total"

        val rvExplanations: RecyclerView = findViewById(R.id.rvExplanations)
        rvExplanations.layoutManager = LinearLayoutManager(this)
        rvExplanations.adapter = QuizExplanationAdapter(correctAnswers, explanations)

        val btnFinish: Button = findViewById(R.id.btnFinish)
        btnFinish.setOnClickListener {

            val intent = Intent(this, StudentDashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}