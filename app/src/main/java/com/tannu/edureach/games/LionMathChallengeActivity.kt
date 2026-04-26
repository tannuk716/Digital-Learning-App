package com.tannu.edureach.games

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R

class LionMathChallengeActivity : AppCompatActivity() {

    private lateinit var tvLionSpeech: TextView
    private lateinit var btnAns1: Button
    private lateinit var btnAns2: Button
    private lateinit var btnAns3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_lion_math)

        tvLionSpeech = findViewById(R.id.tvLionSpeech)
        btnAns1 = findViewById(R.id.btnAns1)
        btnAns2 = findViewById(R.id.btnAns2)
        btnAns3 = findViewById(R.id.btnAns3)

        loadQuestion()

        btnAns1.setOnClickListener { checkAnswer(6) }
        btnAns2.setOnClickListener { checkAnswer(7) }
        btnAns3.setOnClickListener { checkAnswer(8) }
    }

    private fun loadQuestion() {
        tvLionSpeech.text = "What is 3 + 4 ?"
        btnAns1.text = "6"
        btnAns2.text = "7"
        btnAns3.text = "8"
    }

    private fun checkAnswer(selectedAns: Int) {
        val correctAns = 7

        if (selectedAns == correctAns) {
            Toast.makeText(this, "Roar! Correct! +10 Points", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 1000)
        } else {
            Toast.makeText(this, "Oops! Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}