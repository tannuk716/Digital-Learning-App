package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import java.util.Calendar

class DailySpeakingActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var tvSpokenText: TextView
    private lateinit var tvFeedback: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvDifficultyLevel: TextView
    private lateinit var btnRecord: Button
    private lateinit var btnSubmit: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var progressBar: ProgressBar
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private var spokenText = ""
    private val SPEECH_REQUEST_CODE = 101
    private var currentQuestionIndex = 0
    private var currentWeekLevel = 1
    
    // Week 1: Basic (Simple present, personal info)
    private val week1Questions = listOf(
        "What is your name?",
        "How old are you?",
        "Where do you live?",
        "What is your favorite color?",
        "Do you like animals?",
        "What do you eat for breakfast?",
        "How many people are in your family?",
        "What is your favorite game?",
        "Do you have any pets?",
        "What makes you happy?"
    )
    
    // Week 2: Elementary (Simple descriptions)
    private val week2Questions = listOf(
        "Describe your best friend.",
        "What did you do yesterday?",
        "Tell me about your favorite toy.",
        "What is your favorite subject in school?",
        "Describe your bedroom.",
        "What do you like to do after school?",
        "Tell me about your favorite food.",
        "What games do you play with friends?",
        "Describe your teacher.",
        "What do you do on weekends?"
    )
    
    // Week 3: Intermediate (Past tense, longer responses)
    private val week3Questions = listOf(
        "Tell me about your last birthday.",
        "What is the best gift you ever received?",
        "Describe a fun day you had recently.",
        "What do you want to be when you grow up and why?",
        "Tell me about a book you read.",
        "Describe your favorite place to visit.",
        "What is something new you learned this week?",
        "Tell me about a time you helped someone.",
        "What makes a good friend?",
        "Describe your dream vacation."
    )
    
    // Week 4: Advanced (Complex sentences, opinions)
    private val week4Questions = listOf(
        "If you could have any superpower, what would it be and why?",
        "What would you do if you found a lost puppy?",
        "Explain why education is important.",
        "Describe how you would spend a million rupees.",
        "What changes would you make to your school?",
        "Tell me about someone you admire and why.",
        "How can we help protect the environment?",
        "What is the most important lesson you've learned?",
        "Describe your perfect day from morning to night.",
        "If you could travel anywhere, where would you go and what would you do?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_speaking)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvSpokenText = findViewById(R.id.tvSpokenText)
        tvFeedback = findViewById(R.id.tvFeedback)
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvDifficultyLevel = findViewById(R.id.tvDifficultyLevel)
        btnRecord = findViewById(R.id.btnRecord)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        progressBar = findViewById(R.id.progressBar)

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        // Calculate current week level (1-4, repeating)
        calculateWeekLevel()
        
        // Set initial question based on day
        setDailyQuestion()

        btnRecord.setOnClickListener {
            startSpeechRecognition()
        }

        btnSubmit.setOnClickListener {
            if (spokenText.isNotEmpty()) {
                evaluateSpeech()
            } else {
                Toast.makeText(this, "Please record your speech first", Toast.LENGTH_SHORT).show()
            }
        }

        btnNext.setOnClickListener {
            val questions = getCurrentWeekQuestions()
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                updateQuestion()
                resetUI()
            }
        }

        btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                updateQuestion()
                resetUI()
            }
        }
    }

    private fun calculateWeekLevel() {
        val calendar = Calendar.getInstance()
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        // Cycle through weeks 1-4
        currentWeekLevel = ((weekOfYear - 1) % 4) + 1
    }

    private fun getCurrentWeekQuestions(): List<String> {
        return when (currentWeekLevel) {
            1 -> week1Questions
            2 -> week2Questions
            3 -> week3Questions
            4 -> week4Questions
            else -> week1Questions
        }
    }

    private fun getDifficultyLabel(): String {
        return when (currentWeekLevel) {
            1 -> "Basic Level"
            2 -> "Elementary Level"
            3 -> "Intermediate Level"
            4 -> "Advanced Level"
            else -> "Basic Level"
        }
    }

    private fun setDailyQuestion() {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val questions = getCurrentWeekQuestions()
        
        // Set question based on day of year
        currentQuestionIndex = dayOfYear % questions.size
        updateQuestion()
    }

    private fun updateQuestion() {
        val questions = getCurrentWeekQuestions()
        tvQuestion.text = questions[currentQuestionIndex]
        tvQuestionNumber.text = "Question ${currentQuestionIndex + 1} of ${questions.size}"
        tvDifficultyLevel.text = "📊 ${getDifficultyLabel()} - Week $currentWeekLevel"
        
        // Update button states
        btnPrevious.isEnabled = currentQuestionIndex > 0
        btnNext.isEnabled = currentQuestionIndex < questions.size - 1
        
        btnPrevious.alpha = if (currentQuestionIndex > 0) 1.0f else 0.5f
        btnNext.alpha = if (currentQuestionIndex < questions.size - 1) 1.0f else 0.5f
    }

    private fun resetUI() {
        spokenText = ""
        tvSpokenText.text = "Your speech will appear here..."
        tvSpokenText.visibility = View.VISIBLE
        tvFeedback.visibility = View.GONE
        btnSubmit.isEnabled = false
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
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
            tvSpokenText.text = "You said: $spokenText"
            tvSpokenText.visibility = View.VISIBLE
            btnSubmit.isEnabled = true
        }
    }

    private fun evaluateSpeech() {
        progressBar.visibility = View.VISIBLE
        btnSubmit.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val difficultyContext = when (currentWeekLevel) {
                    1 -> "This is a basic level question. Expect simple, short answers."
                    2 -> "This is an elementary level question. Expect simple descriptions."
                    3 -> "This is an intermediate level question. Expect past tense and longer responses."
                    4 -> "This is an advanced level question. Expect complex sentences and opinions."
                    else -> "Basic level question."
                }
                
                val prompt = """
                    Evaluate this English speaking response for a Class 5-10 student:
                    
                    Difficulty Level: Week $currentWeekLevel - ${getDifficultyLabel()}
                    $difficultyContext
                    
                    Question: ${tvQuestion.text}
                    Student's Response: $spokenText
                    
                    Provide feedback in JSON format:
                    {
                        "score": <number 0-100>,
                        "pronunciation": "<Good/Fair/Needs Improvement>",
                        "fluency": "<High/Medium/Low>",
                        "grammar": "<Correct/Minor Errors/Major Errors>",
                        "suggestion": "<helpful tip in simple English>"
                    }
                    
                    Be encouraging and supportive. Adjust expectations based on the difficulty level.
                """.trimIndent()
                
                val result = GeminiApiHelper.generateContent(prompt)
                
                result.onSuccess { response ->
                    val jsonStart = response.indexOf("{")
                    val jsonEnd = response.lastIndexOf("}") + 1
                    
                    if (jsonStart >= 0 && jsonEnd > jsonStart) {
                        val jsonStr = response.substring(jsonStart, jsonEnd)
                        val json = JSONObject(jsonStr)
                        
                        val score = json.optInt("score", 70)
                        val pronunciation = json.optString("pronunciation", "Good")
                        val fluency = json.optString("fluency", "Medium")
                        val grammar = json.optString("grammar", "Correct")
                        val suggestion = json.optString("suggestion", "Keep practicing!")
                        
                        val feedback = """
                            ✅ Great job! 
                            
                            📊 Score: $score%
                            🗣️ Pronunciation: $pronunciation
                            💬 Fluency: $fluency
                            📝 Grammar: $grammar
                            
                            💡 Tip: $suggestion
                            
                            🎯 Level: ${getDifficultyLabel()}
                        """.trimIndent()
                        
                        tvFeedback.text = feedback
                        tvFeedback.visibility = View.VISIBLE
                        
                        saveProgress(score)
                    } else {
                        showBasicFeedback()
                    }
                }
                
                result.onFailure { error ->
                    android.util.Log.e("DailySpeaking", "Error evaluating speech", error)
                    showBasicFeedback()
                }
                
            } catch (e: Exception) {
                android.util.Log.e("DailySpeaking", "Error evaluating speech", e)
                showBasicFeedback()
            } finally {
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled = true
            }
        }
    }

    private fun showBasicFeedback() {
        val wordCount = spokenText.split(" ").size
        val score = when {
            wordCount >= 20 -> 85
            wordCount >= 10 -> 70
            else -> 60
        }
        
        val feedback = """
            ✅ Good effort! 
            
            📊 Score: $score%
            🗣️ Words spoken: $wordCount
            🎯 Level: ${getDifficultyLabel()}
            
            💡 Tip: Try to speak more to improve your score!
        """.trimIndent()
        
        tvFeedback.text = feedback
        tvFeedback.visibility = View.VISIBLE
        
        saveProgress(score)
    }

    private fun saveProgress(score: Int) {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("progress")
            .get()
            .addOnSuccessListener { document ->
                val currentScore = document.getLong("speakingScore") ?: 0
                val newScore = ((currentScore + score) / 2).toInt()
                
                val updates = hashMapOf<String, Any>(
                    "speakingScore" to newScore,
                    "lastPracticeDate" to System.currentTimeMillis(),
                    "currentWeekLevel" to currentWeekLevel
                )
                
                db.collection("users").document(uid)
                    .collection("communication_skills")
                    .document("progress")
                    .set(updates, com.google.firebase.firestore.SetOptions.merge())
                
                updateStreak()
            }
    }

    private fun updateStreak() {
        val uid = auth.currentUser?.uid ?: return
        
        db.collection("users").document(uid)
            .collection("communication_skills")
            .document("progress")
            .get()
            .addOnSuccessListener { document ->
                val lastDate = document.getLong("lastPracticeDate") ?: 0
                val currentStreak = document.getLong("streakCount") ?: 0
                
                val oneDayMillis = 24 * 60 * 60 * 1000L
                val daysSinceLastPractice = (System.currentTimeMillis() - lastDate) / oneDayMillis
                
                val newStreak = when {
                    daysSinceLastPractice <= 1 -> currentStreak + 1
                    else -> 1
                }
                
                db.collection("users").document(uid)
                    .collection("communication_skills")
                    .document("progress")
                    .update("streakCount", newStreak)
            }
    }
}
