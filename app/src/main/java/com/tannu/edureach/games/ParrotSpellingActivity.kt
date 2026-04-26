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

class ParrotSpellingActivity : AppCompatActivity() {

    private lateinit var tvWord: TextView
    private lateinit var btnOpt1: Button
    private lateinit var btnOpt2: Button
    private lateinit var btnOpt3: Button

    private val words = listOf(
        Pair("M O N K _ Y", listOf("A", "E", "I") to 1),
        Pair("P A R R _ T", listOf("O", "U", "A") to 0),
        Pair("T I G _ R", listOf("O", "A", "E") to 2),
        Pair("E L E P H _ N T", listOf("A", "E", "I") to 0)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parrot_spelling)

        tvWord = findViewById(R.id.tvWord)
        btnOpt1 = findViewById(R.id.btnOption1)
        btnOpt2 = findViewById(R.id.btnOption2)
        btnOpt3 = findViewById(R.id.btnOption3)

        loadWord()
    }

    private fun loadWord() {
        if (currentIndex >= words.size) {
            Toast.makeText(this, "Great job! You finished spelling!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val data = words[currentIndex]
        tvWord.text = data.first

        val options = data.second.first
        val correctIndex = data.second.second

        val buttons = listOf(btnOpt1, btnOpt2, btnOpt3)
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].setBackgroundColor(Color.parseColor("#FFB300"))
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
            loadWord()
        }, 1500)
    }
}