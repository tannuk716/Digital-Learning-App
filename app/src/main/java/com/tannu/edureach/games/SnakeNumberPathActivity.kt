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

class SnakeNumberPathActivity : AppCompatActivity() {

    private lateinit var tvSequence: TextView
    private lateinit var btnOpt1: Button
    private lateinit var btnOpt2: Button
    private lateinit var btnOpt3: Button

    private val sequences = listOf(
        Pair("1, 2, 3, _, 5", listOf("4", "6", "7") to 0),
        Pair("10, 20, 30, _, 50", listOf("35", "40", "45") to 1),
        Pair("2, 4, 6, _, 10", listOf("7", "8", "9") to 1),
        Pair("5, 10, 15, _, 25", listOf("20", "22", "18") to 0)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_number)

        tvSequence = findViewById(R.id.tvSequence)
        btnOpt1 = findViewById(R.id.btnOpt1)
        btnOpt2 = findViewById(R.id.btnOpt2)
        btnOpt3 = findViewById(R.id.btnOpt3)

        loadSequence()
    }

    private fun loadSequence() {
        if (currentIndex >= sequences.size) {
            Toast.makeText(this, "Great job! Path completed!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val data = sequences[currentIndex]
        tvSequence.text = data.first

        val options = data.second.first
        val correctIndex = data.second.second

        val buttons = listOf(btnOpt1, btnOpt2, btnOpt3)
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].setBackgroundColor(Color.parseColor("#8BC34A"))
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
            loadSequence()
        }, 1500)
    }
}