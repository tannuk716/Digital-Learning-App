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

class FruitCatchActivity : AppCompatActivity() {

    private lateinit var tvInstruction: TextView
    private lateinit var btnOpt1: Button
    private lateinit var btnOpt2: Button
    private lateinit var btnOpt3: Button

    private val puzzles = listOf(
        Pair("Which one is a FRUIT?", listOf("🍎", "🥦", "🍔") to 0),
        Pair("Which one is a FRUIT?", listOf("🥔", "🍕", "🍌") to 2),
        Pair("Which one is a FRUIT?", listOf("🍉", "🥕", "🌭") to 0),
        Pair("Which one is a FRUIT?", listOf("🧅", "🍇", "🌮") to 1)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruit_catch)

        tvInstruction = findViewById(R.id.tvInstruction)
        btnOpt1 = findViewById(R.id.btnOpt1)
        btnOpt2 = findViewById(R.id.btnOpt2)
        btnOpt3 = findViewById(R.id.btnOpt3)

        loadPuzzle()
    }

    private fun loadPuzzle() {
        if (currentIndex >= puzzles.size) {
            Toast.makeText(this, "Great job! Panda is full of fruits!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val data = puzzles[currentIndex]
        tvInstruction.text = data.first

        val options = data.second.first
        val correctIndex = data.second.second

        val buttons = listOf(btnOpt1, btnOpt2, btnOpt3)
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].setBackgroundColor(Color.parseColor("#FFCC80")) 
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