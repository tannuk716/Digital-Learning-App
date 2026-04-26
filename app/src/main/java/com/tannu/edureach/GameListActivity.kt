package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.data.model.GameModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GameListActivity : AppCompatActivity() {

    private lateinit var rvGames: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var tvSubjectTitle: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        val classId = intent.getStringExtra("CLASS_ID") ?: "class_1"
        val subjectId = intent.getStringExtra("SUBJECT_ID") ?: "maths"
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"

        rvGames = findViewById(R.id.rvGames)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)

        tvSubjectTitle.text = "$subjectName Games"
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvGames.layoutManager = GridLayoutManager(this, 2)

        loadGames(classId, subjectId)
    }

    private fun loadGames(classId: String, subjectId: String) {
        android.util.Log.d("GameList", "Loading games for classId=$classId, subjectId=$subjectId")
        

        val subjectIds = when (subjectId.lowercase()) {
            "maths", "math" -> listOf("maths", "math")
            else -> listOf(subjectId)
        }
        
        lifecycleScope.launch {
            try {
                val allGames = mutableListOf<GameModel>()
                

                for (searchSubjectId in subjectIds) {
                    android.util.Log.d("GameList", "Searching with subjectId: $searchSubjectId")
                    
                    val snapshot = db.collection("classes").document(classId)
                        .collection("subjects").document(searchSubjectId)
                        .collection("games")
                        .get().await()

                    android.util.Log.d("GameList", "[$searchSubjectId] Found ${snapshot.size()} games")
                    
                    snapshot.documents.forEach { doc ->
                        doc.toObject(GameModel::class.java)?.let { game ->

                            if (allGames.none { it.id == game.id }) {
                                allGames.add(game.copy(id = doc.id))
                            }
                        }
                    }
                }

                if (allGames.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                    tvEmptyState.text = "No games available yet.\nTeachers can add games for this subject."
                    rvGames.visibility = View.GONE
                } else {
                    tvEmptyState.visibility = View.GONE
                    rvGames.visibility = View.VISIBLE
                    rvGames.adapter = GameAdapter(allGames) { game ->
                        launchGame(game)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GameList", "Error loading games", e)
                Toast.makeText(this@GameListActivity, "Failed to load games: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchGame(game: GameModel) {
        try {

            val intent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
            intent.putExtra("WEB_URL", game.activityClass)
            intent.putExtra("WEB_TITLE", game.title)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to open game: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private class GameAdapter(
        private val games: List<GameModel>,
        private val onClick: (GameModel) -> Unit
    ) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvGameTitle: TextView = view.findViewById(R.id.gameTitle)
            val tvGameDesc: TextView = view.findViewById(R.id.gameDesc)
            val gameIcon: TextView = view.findViewById(R.id.gameIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_game, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val game = games[position]
            holder.tvGameTitle.text = game.title
            holder.tvGameDesc.text = game.description
            holder.gameIcon.text = "🎮"
            holder.itemView.setOnClickListener { onClick(game) }
        }

        override fun getItemCount() = games.size
    }
}