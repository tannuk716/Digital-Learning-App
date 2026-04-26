package com.tannu.edureach

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.utils.GeminiApiHelper
import kotlinx.coroutines.launch

class VocabularyBuilderActivity : AppCompatActivity() {

    private lateinit var tvWord: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvSentence: TextView
    private lateinit var tvWordNumber: TextView
    private lateinit var tvQuizQuestion: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnSubmitQuiz: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var tvQuizResult: TextView
    private lateinit var progressBar: ProgressBar
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var currentWordIndex = 0
    
    private val vocabulary = listOf(
        VocabWord("Opportunity", "A chance to do something", "This app gives students opportunity to learn.", 
            listOf("Problem", "Chance", "Failure"), 1),
        VocabWord("Courage", "Bravery to face fear", "It takes courage to speak in front of others.",
            listOf("Fear", "Bravery", "Weakness"), 1),
        VocabWord("Wisdom", "Knowledge and good judgment", "Teachers share their wisdom with students.",
            listOf("Foolishness", "Knowledge", "Confusion"), 1),
        VocabWord("Gratitude", "Being thankful", "We should show gratitude to our parents.",
            listOf("Thankfulness", "Anger", "Sadness"), 0),
        VocabWord("Perseverance", "Continuing despite difficulties", "Success requires perseverance and hard work.",
            listOf("Giving up", "Continuing", "Laziness"), 1),
        VocabWord("Compassion", "Caring about others' suffering", "Show compassion to those in need.",
            listOf("Cruelty", "Kindness", "Indifference"), 1),
        VocabWord("Integrity", "Being honest and having strong moral principles", "A person of integrity always tells the truth.",
            listOf("Dishonesty", "Honesty", "Confusion"), 1),
        VocabWord("Resilience", "Ability to recover from difficulties", "Children show great resilience when facing challenges.",
            listOf("Weakness", "Recovery ability", "Giving up"), 1),
        VocabWord("Empathy", "Understanding others' feelings", "Empathy helps us connect with people.",
            listOf("Understanding", "Ignorance", "Selfishness"), 0),
        VocabWord("Diligence", "Careful and persistent work", "Diligence in studies leads to success.",
            listOf("Laziness", "Hard work", "Carelessness"), 1)
    )
    
    private var currentWord: VocabWord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary_builder)

        tvWord = findViewById(R.id.tvWord)
        tvMeaning = findViewById(R.id.tvMeaning)
        tvSentence = findViewById(R.id.tvSentence)
        tvWordNumber = findViewById(R.id.tvWordNumber)
        tvQuizQuestion = findViewById(R.id.tvQuizQuestion)
        radioGroup = findViewById(R.id.radioGroup)
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        tvQuizResult = findViewById(R.id.tvQuizResult)
        progressBar = findViewById(R.id.progressBar)

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        updateWord()

        btnSubmitQuiz.setOnClickListener {
            checkAnswer()
        }

        btnNext.setOnClickListener {
            if (currentWordIndex < vocabulary.size - 1) {
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
        currentWord = vocabulary[currentWordIndex]
        displayWord()
        
        tvWordNumber.text = "Word ${currentWordIndex + 1} of ${vocabulary.size}"
        

        btnPrevious.isEnabled = currentWordIndex > 0
        btnNext.isEnabled = currentWordIndex < vocabulary.size - 1
        
        btnPrevious.alpha = if (currentWordIndex > 0) 1.0f else 0.5f
        btnNext.alpha = if (currentWordIndex < vocabulary.size - 1) 1.0f else 0.5f
    }

    private fun resetUI() {
        radioGroup.clearCheck()
        tvQuizResult.visibility = View.GONE
        btnSubmitQuiz.isEnabled = true
    }

    private fun displayWord() {
        currentWord?.let { word ->
            tvWord.text = word.word
            tvMeaning.text = "Meaning: ${word.meaning}"
            tvSentence.text = "Example: ${word.sentence}"
            
            tvQuizQuestion.text = "${word.word} means:"
            

            val radioButton1 = findViewById<RadioButton>(R.id.radioOption1)
            val radioButton2 = findViewById<RadioButton>(R.id.radioOption2)
            val radioButton3 = findViewById<RadioButton>(R.id.radioOption3)
            
            radioButton1.text = "A) ${word.options[0]}"
            radioButton2.text = "B) ${word.options[1]}"
            radioButton3.text = "C) ${word.options[2]}"
        }
    }

    private fun checkAnswer() {
        val selectedId = radioGroup.checkedRadioButtonId
        
        if (selectedId == -1) {
            tvQuizResult.text = "Please select an answer"
            tvQuizResult.visibility = View.VISIBLE
            return
        }
        
        val selectedIndex = when (selectedId) {
            R.id.radioOption1 -> 0
            R.id.radioOption2 -> 1
            R.id.radioOption3 -> 2
            else -> -1
        }
        
        currentWord?.let { word ->
            if (selectedIndex == word.correctAnswer) {

                getAIExplanation(word, true)
            } else {

                getAIExplanation(word, false, word.options[selectedIndex])
            }
            btnSubmitQuiz.isEnabled = false
        }
    }
    
    private fun getAIExplanation(word: VocabWord, isCorrect: Boolean, wrongAnswer: String = "") {
        progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val prompt = if (isCorrect) {
                    """
                        A Class 5-10 student correctly answered that "${word.word}" means "${word.meaning}".
                        
                        Provide encouraging feedback with:
                        1. A congratulatory message
                        2. An additional interesting fact or usage tip about the word
                        3. A memory trick to remember this word
                        
                        Keep it simple, encouraging, and under 100 words.
                    """.trimIndent()
                } else {
                    """
                        A Class 5-10 student thought "${word.word}" means "$wrongAnswer", but it actually means "${word.meaning}".
                        
                        Provide supportive feedback with:
                        1. A gentle correction
                        2. A simple explanation of why the correct answer is right
                        3. A memory trick to remember this word
                        
                        Be encouraging and supportive. Keep it under 100 words.
                    """.trimIndent()
                }
                
                val result = GeminiApiHelper.generateContent(prompt)
                
                result.onSuccess { response ->
                    val feedback = if (isCorrect) {
                        "✅ Correct! Great job!\n\n$response"
                    } else {
                        "❌ Not quite right.\n\nCorrect answer: ${word.options[word.correctAnswer]}\n\n$response"
                    }
                    
                    tvQuizResult.text = feedback
                    tvQuizResult.setTextColor(
                        if (isCorrect) resources.getColor(android.R.color.holo_green_dark, null)
                        else resources.getColor(android.R.color.holo_red_dark, null)
                    )
                    tvQuizResult.visibility = View.VISIBLE
                    
                    saveProgress(isCorrect)
                }
                
                result.onFailure { error ->
                    android.util.Log.e("Vocabulary", "AI Error", error)
                    showBasicFeedback(word, isCorrect, wrongAnswer)
                }
                
            } catch (e: Exception) {
                android.util.Log.e("Vocabulary", "Error", e)
                showBasicFeedback(word, isCorrect, wrongAnswer)
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
    
    private fun showBasicFeedback(word: VocabWord, isCorrect: Boolean, wrongAnswer: String = "") {
        if (isCorrect) {
            tvQuizResult.text = "✅ Correct! Great job!\n\n${word.word} means ${word.meaning}"
            tvQuizResult.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            saveProgress(true)
        } else {
            tvQuizResult.text = "❌ Incorrect.\n\nThe correct answer is: ${word.options[word.correctAnswer]}\n\n${word.word} means ${word.meaning}"
            tvQuizResult.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            saveProgress(false)
        }
        tvQuizResult.visibility = View.VISIBLE
    }

    private fun saveProgress(correct: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        
        if (correct) {
            db.collection("users").document(uid)
                .collection("communication_skills")
                .document("progress")
                .get()
                .addOnSuccessListener { document ->
                    val currentCount = document.getLong("vocabularyLearned") ?: 0
                    
                    db.collection("users").document(uid)
                        .collection("communication_skills")
                        .document("progress")
                        .update("vocabularyLearned", currentCount + 1)
                }
        }
    }

    data class VocabWord(
        val word: String,
        val meaning: String,
        val sentence: String,
        val options: List<String>,
        val correctAnswer: Int
    )
}