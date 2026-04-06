package com.tannu.edureach.games

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R

class MemoryMatchActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private lateinit var cards: List<String>
    
    private var indexOfSingleSelectedCard: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_memory_match)

        buttons = listOf(
            findViewById(R.id.btnCard0),
            findViewById(R.id.btnCard1),
            findViewById(R.id.btnCard2),
            findViewById(R.id.btnCard3)
        )

        // 2 pairs of identical cards
        val images = mutableListOf("🐶", "🐶", "🐱", "🐱")
        images.shuffle()
        cards = images

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                updateModels(index)
                updateViews()
            }
        }
    }

    private fun updateModels(position: Int) {
        val button = buttons[position]
        if (button.text != "?") {
            // Invalid move if card is already face up
            return
        }

        if (indexOfSingleSelectedCard == null) {
            // 0 or 2 cards previously selected
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            // exactly 1 card previously selected
            checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
    }

    private fun restoreCards() {
        for (button in buttons) {
            if (button.currentTextColor != Color.GREEN) {
                button.text = "?"
            }
        }
    }

    private fun checkForMatch(position1: Int, position2: Int) {
        if (cards[position1] == cards[position2]) {
            Toast.makeText(this, "Match found!", Toast.LENGTH_SHORT).show()
            buttons[position1].setTextColor(Color.GREEN)
            buttons[position2].setTextColor(Color.GREEN)
            checkWin()
        }
    }

    private fun updateViews() {
        buttons.forEachIndexed { index, button ->
            if (index == indexOfSingleSelectedCard || button.currentTextColor == Color.GREEN) {
                button.text = cards[index]
            }
        }
    }

    private fun checkWin() {
        val isWon = buttons.all { it.currentTextColor == Color.GREEN }
        if (isWon) {
            Toast.makeText(this, "You won! +50 Points", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                finish() // Simulated exit
            }, 1500)
        }
    }
}
