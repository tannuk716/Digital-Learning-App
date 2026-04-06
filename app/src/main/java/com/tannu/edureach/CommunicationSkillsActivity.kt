package com.tannu.edureach

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.utils.GeminiApiService
import kotlinx.coroutines.launch
import java.util.Locale

class CommunicationSkillsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var cardSpeaking: CardView
    private lateinit var cardPronunciation: CardView
    private lateinit var cardVocabulary: CardView
    private lateinit var cardProgress: CardView
    
    private lateinit var tvSpeakingScore: TextView
    private lateinit var tvPronunciationScore: TextView
    private lateinit var tvVocabularyCount: TextView
    private lateinit var tvStreakCount: TextView
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var textToSpeech: TextToSpeech? = null
    
    private val MICROPHONE_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_skills)

        // Initialize views
        cardSpeaking = findViewById(R.id.cardSpeaking)
        cardPronunciation = findViewById(R.id.cardPronunciation)
        cardVocabulary = findViewById(R.id.cardVocabulary)
        cardProgress = findViewById(R.id.cardProgress)
        
        tvSpeakingScore = findViewById(R.id.tvSpeakingScore)
        tvPronunciationScore = findViewById(R.id.tvPronunciationScore)
        tvVocabularyCount = findViewById(R.id.tvVocabularyCount)
        tvStreakCount = findViewById(R.id.tvStreakCount)

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        // Initialize Text-to-Speech
        textToSpeech = TextToSpeech(this, this)

        // Set click listeners
        cardSpeaking.setOnClickListener {
            startActivity(Intent(this, DailySpeakingActivity::class.java))
        }

        cardPronunciation.setOnClickListener {
            startActivity(Intent(this, PronunciationTrainerActivity::class.java))
        }

        cardVocabulary.setOnClickListener {
            startActivity(Intent(this, VocabularyBuilderActivity::class.java))
        }

        // Check microphone permission
        checkMicrophonePermission()
        
        // Load progress data
        loadProgressData()
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MICROPHONE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MICROPHONE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission is required for voice features", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadProgressData() {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("progress")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val speakingScore = document.getLong("speakingScore") ?: 0
                    val pronunciationScore = document.getLong("pronunciationScore") ?: 0
                    val vocabularyLearned = document.getLong("vocabularyLearned") ?: 0
                    val streakCount = document.getLong("streakCount") ?: 0
                    
                    tvSpeakingScore.text = "$speakingScore%"
                    tvPronunciationScore.text = "$pronunciationScore%"
                    tvVocabularyCount.text = "$vocabularyLearned words"
                    tvStreakCount.text = "$streakCount days"
                } else {
                    // Initialize with default values
                    tvSpeakingScore.text = "0%"
                    tvPronunciationScore.text = "0%"
                    tvVocabularyCount.text = "0 words"
                    tvStreakCount.text = "0 days"
                }
            }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}
