package com.tannu.edureach.games

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R

class FrogJumpAdditionActivity : AppCompatActivity() {

    private lateinit var tvEquation: TextView
    private lateinit var btnOpt1: Button
    private lateinit var btnOpt2: Button
    private lateinit var btnOpt3: Button

    private val puzzles = listOf(
        Pair("1 + 2 = ?", listOf("2", "3", "4") to 1),
        Pair("3 + 2 = ?", listOf("5", "6", "4") to 0),
        Pair("4 + 4 = ?", listOf("7", "9", "8") to 2),
        Pair("5 + 5 = ?", listOf("10", "11", "9") to 0)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frog_jump)

        tvEquation = findViewById(R.id.tvEquation)
        btnOpt1 = findViewById(R.id.btnOpt1)
        btnOpt2 = findViewById(R.id.btnOpt2)
        btnOpt3 = findViewById(R.id.btnOpt3)

        loadPuzzle()
    }

    private fun loadPuzzle() {
        if (currentIndex >= puzzles.size) {
            Toast.makeText(this, "Great job! Frog has crossed the river!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val data = puzzles[currentIndex]
        tvEquation.text = data.first

        val options = data.second.first
        val correctIndex = data.second.second

        val buttons = listOf(btnOpt1, btnOpt2, btnOpt3)
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].setBackgroundColor(Color.parseColor("#4DB6AC")) 
            buttons[i].isEnabled = true
            buttons[i].setOnClickListener { checkAnswer(i, correctIndex, buttons) }
        }
    }

    private fun checkAnswer(selectedIndex: Int, correctIndex: Int, buttons: List<Button>) {
        buttons.forEach { it.isEnabled = false }

        if (selectedIndex == correctIndex) {
            buttons[selectedIndex].setBackgroundColor(Color.parseColor("#4CAF50"))
        } else {
            buttons[selectedIndex].setBackgroundColor(Color.parseColor("#F44336"))
            buttons[correctIndex].setBackgroundColor(Color.parseColor("#4CAF50"))
        }

        Handler(Looper.getMainLooper()).postDelayed({
            currentIndex++
            loadPuzzle()
        }, 1500)
    }
}