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
import com.tannu.edureach.utils.GeminiApiHelper
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
    private lateinit var tvAiTips: TextView
    private lateinit var btnGetAiTips: Button
    private lateinit var progressBarAi: ProgressBar
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var textToSpeech: TextToSpeech? = null
    
    private val MICROPHONE_PERMISSION_CODE = 100
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication_skills)
        
        cardSpeaking = findViewById(R.id.cardSpeaking)
        cardPronunciation = findViewById(R.id.cardPronunciation)
        cardVocabulary = findViewById(R.id.cardVocabulary)
        cardProgress = findViewById(R.id.cardProgress)
        
        tvSpeakingScore = findViewById(R.id.tvSpeakingScore)
        tvPronunciationScore = findViewById(R.id.tvPronunciationScore)
        tvVocabularyCount = findViewById(R.id.tvVocabularyCount)
        tvStreakCount = findViewById(R.id.tvStreakCount)
        tvAiTips = findViewById(R.id.tvAiTips)
        btnGetAiTips = findViewById(R.id.btnGetAiTips)
        progressBarAi = findViewById(R.id.progressBarAi)
        
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }
        
        textToSpeech = TextToSpeech(this, this)
        
        cardSpeaking.setOnClickListener {
            startActivity(Intent(this, DailySpeakingActivity::class.java))
        }
        
        cardPronunciation.setOnClickListener {
            startActivity(Intent(this, PronunciationTrainerActivity::class.java))
        }
        
        cardVocabulary.setOnClickListener {
            startActivity(Intent(this, VocabularyBuilderActivity::class.java))
        }
        
        btnGetAiTips.setOnClickListener {
            getAiPersonalizedTips()
        }
        
        checkMicrophonePermission()
        
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
                    
                    generateInitialAiTips(speakingScore.toInt(), pronunciationScore.toInt(), vocabularyLearned.toInt(), streakCount.toInt())
                } else {
                    tvSpeakingScore.text = "0%"
                    tvPronunciationScore.text = "0%"
                    tvVocabularyCount.text = "0 words"
                    tvStreakCount.text = "0 days"
                    
                    tvAiTips.text = "👋 Welcome! Start practicing to get personalized AI tips."
                }
            }
    }
    
    private fun generateInitialAiTips(speaking: Int, pronunciation: Int, vocabulary: Int, streak: Int) {
        val tips = when {
            speaking == 0 && pronunciation == 0 && vocabulary == 0 -> 
                "🎯 Start your journey! Try the Daily Speaking practice first."
            streak >= 7 -> 
                "🔥 Amazing ${streak}-day streak! Keep up the excellent work!"
            speaking < 50 -> 
                "💡 Focus on Daily Speaking to improve your communication skills."
            pronunciation < 50 -> 
                "🗣️ Work on Pronunciation Trainer to speak more clearly."
            vocabulary < 20 -> 
                "📚 Build your vocabulary to express yourself better."
            else -> 
                "✨ Great progress! Click 'Get AI Tips' for personalized advice."
        }
        tvAiTips.text = tips
    }
    
    private fun getAiPersonalizedTips() {
        val uid = auth.currentUser?.uid ?: return
        
        progressBarAi.visibility = View.VISIBLE
        btnGetAiTips.isEnabled = false
        tvAiTips.text = "🤖 AI is analyzing your progress..."
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("progress")
            .get()
            .addOnSuccessListener { document ->
                val speakingScore = document.getLong("speakingScore")?.toInt() ?: 0
                val pronunciationScore = document.getLong("pronunciationScore")?.toInt() ?: 0
                val vocabularyLearned = document.getLong("vocabularyLearned")?.toInt() ?: 0
                val streakCount = document.getLong("streakCount")?.toInt() ?: 0
                val currentWeekLevel = document.getLong("currentWeekLevel")?.toInt() ?: 1
                
                lifecycleScope.launch {
                    try {
                        val prompt = buildAiPrompt(speakingScore, pronunciationScore, vocabularyLearned, streakCount, currentWeekLevel)
                        
                        val result = GeminiApiHelper.generateContent(prompt)
                        
                        result.onSuccess { aiResponse ->
                            tvAiTips.text = "🤖 AI Coach Says:\n\n$aiResponse"
                            saveAiTipsToFirebase(aiResponse)
                        }.onFailure { exception ->
                            tvAiTips.text = "❌ ${exception.message}\n\n${getFallbackTips(speakingScore, pronunciationScore, vocabularyLearned, streakCount)}"
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CommunicationSkills", "AI Error", e)
                        tvAiTips.text = "❌ Error getting AI tips\n\n${getFallbackTips(speakingScore, pronunciationScore, vocabularyLearned, streakCount)}"
                    } finally {
                        progressBarAi.visibility = View.GONE
                        btnGetAiTips.isEnabled = true
                    }
                }
            }
            .addOnFailureListener {
                progressBarAi.visibility = View.GONE
                btnGetAiTips.isEnabled = true
                tvAiTips.text = "❌ Error loading progress data"
            }
    }
    
    private fun buildAiPrompt(speaking: Int, pronunciation: Int, vocabulary: Int, streak: Int, level: Int): String {
        return """
            You are an expert English communication coach for rural students in India. Analyze this student's progress and provide personalized, encouraging tips.
            
            Student Progress:
            - Speaking Score: $speaking%
            - Pronunciation Score: $pronunciation%
            - Vocabulary Learned: $vocabulary words
            - Practice Streak: $streak days
            - Current Level: Week $level (1=Basic, 2=Elementary, 3=Intermediate, 4=Advanced)
            
            Provide:
            1. A brief encouraging comment on their progress (1-2 sentences)
            2. Their strongest area (1 sentence)
            3. One specific area to improve with actionable advice (2-3 sentences)
            4. A motivational tip to keep them practicing (1 sentence)
            
            Keep the language simple, encouraging, and suitable for students aged 10-16. Use emojis to make it engaging. Keep total response under 150 words.
        """.trimIndent()
    }
    
    private fun getFallbackTips(speaking: Int, pronunciation: Int, vocabulary: Int, streak: Int): String {
        val strongestArea = when {
            speaking >= pronunciation && speaking >= vocabulary -> "Speaking"
            pronunciation >= speaking && pronunciation >= vocabulary -> "Pronunciation"
            else -> "Vocabulary"
        }
        
        val weakestArea = when {
            speaking <= pronunciation && speaking <= vocabulary -> "Speaking"
            pronunciation <= speaking && pronunciation <= vocabulary -> "Pronunciation"
            else -> "Vocabulary"
        }
        
        return """
            📊 Your Progress Analysis:
            
            ✨ Great job! You're doing well in $strongestArea.
            
            🎯 Focus Area: Work on your $weakestArea skills.
            
            💡 Tips:
            ${when (weakestArea) {
                "Speaking" -> "• Practice speaking for 10 minutes daily\n• Record yourself and listen back\n• Try to speak in complete sentences"
                "Pronunciation" -> "• Listen to native speakers carefully\n• Practice difficult sounds slowly\n• Use the pronunciation trainer daily"
                else -> "• Learn 5 new words every day\n• Use new words in sentences\n• Read English books or stories"
            }}
            
            🔥 Streak: ${if (streak > 0) "$streak days! Keep it up!" else "Start your streak today!"}
            
            Keep practicing every day! 🌟
        """.trimIndent()
    }
    
    private fun saveAiTipsToFirebase(tips: String) {
        val uid = auth.currentUser?.uid ?: return
        
        val tipsData = hashMapOf(
            "tips" to tips,
            "timestamp" to System.currentTimeMillis(),
            "type" to "ai_generated"
        )
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("ai_tips")
            .set(tipsData)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
        }
    }
    
    override fun onDestroy() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            android.util.Log.e("CommunicationSkills", "Error stopping TextToSpeech", e)
        }
        super.onDestroy()
    }
}
