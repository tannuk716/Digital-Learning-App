package com.tannu.edureach.games

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R

class MonkeyJumpActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var ivMonkey: TextView
    private lateinit var btnOpt1: Button
    private lateinit var btnOpt2: Button
    private lateinit var btnOpt3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_monkey_jump)

        tvQuestion = findViewById(R.id.tvQuestion)
        ivMonkey = findViewById(R.id.ivMonkey)
        btnOpt1 = findViewById(R.id.btnOption1)
        btnOpt2 = findViewById(R.id.btnOption2)
        btnOpt3 = findViewById(R.id.btnOption3)

        loadQuestion()

        btnOpt1.setOnClickListener { checkAnswer(0, btnOpt1) }
        btnOpt2.setOnClickListener { checkAnswer(1, btnOpt2) }
        btnOpt3.setOnClickListener { checkAnswer(2, btnOpt3) }
    }

    private fun loadQuestion() {

        tvQuestion.text = "A is for?"
        btnOpt1.text = "🍎 Apple"
        btnOpt2.text = "🚗 Car"
        btnOpt3.text = "🐶 Dog"
    }

    private fun checkAnswer(selectedIndex: Int, clickedBtn: Button) {

        val correctAnswerIndex = 0

        if (selectedIndex == correctAnswerIndex) {
            Toast.makeText(this, "Correct! +10 Points", Toast.LENGTH_SHORT).show()
            animateMonkeyJump(clickedBtn)
        } else {
            Toast.makeText(this, "Oops! Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun animateMonkeyJump(targetBtn: Button) {
        val targetX = targetBtn.x + (targetBtn.width / 2) - (ivMonkey.width / 2)
        val targetY = targetBtn.y - ivMonkey.height

        val animX = ObjectAnimator.ofFloat(ivMonkey, "x", ivMonkey.x, targetX)
        val animY = ObjectAnimator.ofFloat(ivMonkey, "y", ivMonkey.y, targetY)

        animX.duration = 500
        animY.duration = 500

        animX.start()
        animY.start()

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }
}