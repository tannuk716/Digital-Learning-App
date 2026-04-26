package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.utils.GeminiApiHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

class PronunciationTrainerActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tvWord: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvWordNumber: TextView
    private lateinit var tvScore: TextView
    private lateinit var btnListen: Button
    private lateinit var btnRepeat: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var progressBar: ProgressBar
    
    private var textToSpeech: TextToSpeech? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val SPEECH_REQUEST_CODE = 102
    private var currentWordIndex = 0
    private var spokenText = ""
    
    private val words = listOf(
        Pair("Environment", "The natural world around us"),
        Pair("Opportunity", "A chance to do something"),
        Pair("Communication", "Sharing information with others"),
        Pair("Education", "Learning and gaining knowledge"),
        Pair("Technology", "Tools and machines that help us"),
        Pair("Responsibility", "A duty or task you must do"),
        Pair("Achievement", "Something you accomplish successfully"),
        Pair("Imagination", "The ability to create ideas in your mind"),
        Pair("Determination", "Strong will to achieve something"),
        Pair("Cooperation", "Working together with others")
    )
    
    private var currentWord = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation_trainer)

        tvWord = findViewById(R.id.tvWord)
        tvMeaning = findViewById(R.id.tvMeaning)
        tvWordNumber = findViewById(R.id.tvWordNumber)
        tvScore = findViewById(R.id.tvScore)
        btnListen = findViewById(R.id.btnListen)
        btnRepeat = findViewById(R.id.btnRepeat)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        progressBar = findViewById(R.id.progressBar)

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        textToSpeech = TextToSpeech(this, this)

        updateWord()

        btnListen.setOnClickListener {
            speakWord(currentWord)
        }

        btnRepeat.setOnClickListener {
            startSpeechRecognition()
        }

        btnNext.setOnClickListener {
            if (currentWordIndex < words.size - 1) {
                currentWordIndex++
                updateWord()
                resetUI()
            }
        }

        btnPrevious.setOnClickListener {
            if (currentWordIndex > 0) {
                currentWordIndex--
                updateWord()
                resetUI()
            }
        }
    }

    private fun updateWord() {
        val wordPair = words[currentWordIndex]
        currentWord = wordPair.first
        
        tvWord.text = currentWord
        tvMeaning.text = wordPair.second
        tvWordNumber.text = "Word ${currentWordIndex + 1} of ${words.size}"
        

        btnPrevious.isEnabled = currentWordIndex > 0
        btnNext.isEnabled = currentWordIndex < words.size - 1
        
        btnPrevious.alpha = if (currentWordIndex > 0) 1.0f else 0.5f
        btnNext.alpha = if (currentWordIndex < words.size - 1) 1.0f else 0.5f
    }

    private fun resetUI() {
        tvScore.visibility = View.GONE
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US
            textToSpeech?.setSpeechRate(0.8f)
        }
    }

    private fun speakWord(word: String) {
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: $currentWord")
        }
        
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            spokenText = results?.get(0) ?: ""
            

            evaluatePronunciationWithAI()
        }
    }
    
    private fun evaluatePronunciationWithAI() {
        progressBar.visibility = View.VISIBLE
        btnRepeat.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val prompt = """
                    Evaluate pronunciation for a Class 5-10 student learning English:
                    
                    Target Word: $currentWord
                    Student Said: $spokenText
                    
                    Provide feedback in JSON format:
                    {
                        "score": <number 0-100>,
                        "accuracy": "<Excellent/Good/Fair/Needs Practice>",
                        "phonetic_match": "<High/Medium/Low>",
                        "suggestion": "<specific tip to improve pronunciation>"
                    }
                    
                    Be encouraging and provide actionable advice.
                """.trimIndent()
                
                val result = GeminiApiHelper.generateContent(prompt)
                
                result.onSuccess { response ->
                    val jsonStart = response.indexOf("{")
                    val jsonEnd = response.lastIndexOf("}") + 1
                    
                    if (jsonStart >= 0 && jsonEnd > jsonStart) {
                        val jsonStr = response.substring(jsonStart, jsonEnd)
                        val json = JSONObject(jsonStr)
                        
                        val score = json.optInt("score", 70)
                        val accuracy = json.optString("accuracy", "Good")
                        val phoneticMatch = json.optString("phonetic_match", "Medium")
                        val suggestion = json.optString("suggestion", "Keep practicing!")
                        
                        val feedback = """
                            🎯 Pronunciation Score: $score%
                            
                            ✅ Accuracy: $accuracy
                            🔊 Phonetic Match: $phoneticMatch
                            
                            💡 Tip: $suggestion
                            
                            You said: "$spokenText"
                            Target: "$currentWord"
                        """.trimIndent()
                        
                        tvScore.text = feedback
                        tvScore.visibility = View.VISIBLE
                        
                        saveProgress(score)
                    } else {
                        showBasicFeedback()
                    }
                }
                
                result.onFailure { error ->
                    android.util.Log.e("Pronunciation", "AI Error", error)
                    showBasicFeedback()
                }
                
            } catch (e: Exception) {
                android.util.Log.e("Pronunciation", "Error", e)
                showBasicFeedback()
            } finally {
                progressBar.visibility = View.GONE
                btnRepeat.isEnabled = true
            }
        }
    }
    
    private fun showBasicFeedback() {
        val score = calculateSimilarity(currentWord.lowercase(), spokenText.lowercase())
        
        tvScore.text = when {
            score >= 80 -> "✅ Excellent! Score: $score%"
            score >= 60 -> "👍 Good! Score: $score%\nTry again for better pronunciation"
            else -> "💪 Keep trying! Score: $score%\nListen carefully and repeat"
        }
        tvScore.visibility = View.VISIBLE
        
        saveProgress(score)
    }

    private fun calculateSimilarity(target: String, spoken: String): Int {
        if (target == spoken) return 100
        
        val targetWords = target.split(" ")
        val spokenWords = spoken.split(" ")
        
        var matches = 0
        targetWords.forEach { targetWord ->
            if (spokenWords.any { it.contains(targetWord) || targetWord.contains(it) }) {
                matches++
            }
        }
        
        return ((matches.toFloat() / targetWords.size) * 100).toInt()
    }

    private fun saveProgress(score: Int) {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("progress")
            .get()
            .addOnSuccessListener { document ->
                val currentScore = document.getLong("pronunciationScore") ?: 0
                val newScore = ((currentScore + score) / 2).toInt()
                
                db.collection("users").document(uid)
                    .collection("communication_skills")
                    .document("progress")
                    .update("pronunciationScore", newScore)
            }
    }

    override fun onDestroy() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            android.util.Log.e("PronunciationTrainer", "Error stopping TextToSpeech", e)
        }
        super.onDestroy()
    }
}