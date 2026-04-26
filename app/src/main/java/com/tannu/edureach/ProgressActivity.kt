package com.tannu.edureach

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProgressActivity : AppCompatActivity() {

    private lateinit var tvPoints: TextView
    private lateinit var tvStreak: TextView
    private lateinit var btnBack: ImageView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_progress)

        tvPoints = findViewById(R.id.tvPoints)
        tvStreak = findViewById(R.id.tvStreak)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        loadProgressData()
    }

    private fun loadProgressData() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val points = doc.getLong("points") ?: 0L
                    val streak = doc.getLong("streak") ?: 0L
                    
                    tvPoints.text = "$points XP"
                    tvStreak.text = "$streak Days"
                }
            }
    }
}