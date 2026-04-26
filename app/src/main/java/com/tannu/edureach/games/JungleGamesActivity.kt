package com.tannu.edureach.games

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R

data class Game(val title: String, val icon: String, val desc: String)

class JungleGamesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jungle_games)
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { 
            finish() 
        }

        val rvGames = findViewById<RecyclerView>(R.id.rvGames)
        rvGames.layoutManager = LinearLayoutManager(this)

        rvGames.isNestedScrollingEnabled = false

        val gamesList = listOf(
            Game("1. Jungle Jump", "🐒", "Monkey jumps to correct answer"),
            Game("2. Lion Math Challenge", "🐯", "Lion asks math questions"),
            Game("3. Memory Match", "🐘", "Match cards like Dog -> Dog"),
            Game("4. Parrot Spelling", "🦜", "Parrot asks spelling C_T"),
            Game("5. Snake Number Path", "🐍", "Follow correct number sequence"),
            Game("6. Honey Count", "🐻", "Bear counts honey jars"),
            Game("7. Animal Sound", "🦁", "Match animal with sound"),
            Game("8. Shape Finder", "🐢", "Find shape in jungle"),
            Game("9. Frog Jump Addition", "🐸", "Frog jumps to correct answer"),
            Game("10. Fruit Catch", "🐼", "Catch only fruits dropping")
        )

        rvGames.adapter = GamesAdapter(gamesList) { game ->
            com.tannu.edureach.utils.ProgressManager.awardPoints(10)
            when (game.title) {
                "1. Jungle Jump" -> startActivity(Intent(this, MonkeyJumpActivity::class.java))
                "2. Lion Math Challenge" -> startActivity(Intent(this, LionMathChallengeActivity::class.java))
                "3. Memory Match" -> startActivity(Intent(this, MemoryMatchActivity::class.java))
                "4. Parrot Spelling" -> startActivity(Intent(this, ParrotSpellingActivity::class.java))
                "5. Snake Number Path" -> startActivity(Intent(this, SnakeNumberPathActivity::class.java))
                "6. Honey Count" -> startActivity(Intent(this, HoneyCountActivity::class.java))
                "7. Animal Sound" -> startActivity(Intent(this, AnimalSoundActivity::class.java))
                "8. Shape Finder" -> startActivity(Intent(this, ShapeFinderActivity::class.java))
                "9. Frog Jump Addition" -> startActivity(Intent(this, FrogJumpAdditionActivity::class.java))
                "10. Fruit Catch" -> startActivity(Intent(this, FruitCatchActivity::class.java))
                else -> Toast.makeText(this, "Coming soon: ${game.title}!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class GamesAdapter(
        private val games: List<Game>,
        private val onClick: (Game) -> Unit
    ) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

        class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: TextView = view.findViewById(R.id.gameIcon)
            val title: TextView = view.findViewById(R.id.gameTitle)
            val desc: TextView = view.findViewById(R.id.gameDesc)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_game, parent, false)
            return GameViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = games[position]
            holder.icon.text = game.icon
            holder.title.text = game.title
            holder.desc.text = game.desc
            holder.itemView.setOnClickListener { onClick(game) }
        }

        override fun getItemCount() = games.size
    }
}