package com.tannu.edureach

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.data.model.GameModel
import com.tannu.edureach.utils.URLValidator
import com.tannu.edureach.utils.ContentSafetyValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddGameActivity : AppCompatActivity() {

    private lateinit var spinnerClass: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var spinnerUnit: Spinner
    private lateinit var etGameTitle: EditText
    private lateinit var etGameDescription: EditText
    private lateinit var etGameUrl: EditText
    private lateinit var btnSaveGame: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvValidationStatus: TextView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var contentSafetyValidator: ContentSafetyValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)

        contentSafetyValidator = ContentSafetyValidator(this)

        initViews()
        setupSpinners()
        setupListeners()
    }

    private fun initViews() {
        spinnerClass = findViewById(R.id.spinnerClass)
        spinnerSubject = findViewById(R.id.spinnerSubject)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        etGameTitle = findViewById(R.id.etGameTitle)
        etGameDescription = findViewById(R.id.etGameDescription)
        etGameUrl = findViewById(R.id.etGameUrl)
        btnSaveGame = findViewById(R.id.btnSaveGame)
        progressBar = findViewById(R.id.progressBar)
        tvValidationStatus = findViewById(R.id.tvValidationStatus)

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }
    }

    private fun setupSpinners() {

        val classes = arrayOf("Select Class", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", 
                             "Class 6", "Class 7", "Class 8", "Class 9", "Class 10")
        spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classes)

        val subjects = arrayOf("Select Subject", "English", "Hindi", "Maths", "Science", "SST", "EVS")
        spinnerSubject.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)

        val units = arrayOf("Select Unit", "Unit 1", "Unit 2", "Unit 3", "Unit 4", "Unit 5",
                           "Unit 6", "Unit 7", "Unit 8", "Unit 9", "Unit 10",
                           "Unit 11", "Unit 12", "Unit 13", "Unit 14", "Unit 15",
                           "Unit 16", "Unit 17", "Unit 18", "Unit 19", "Unit 20")
        spinnerUnit.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
    }

    private fun setupListeners() {
        btnSaveGame.setOnClickListener {
            saveGame()
        }
    }

    private fun saveGame() {
        val classSelection = spinnerClass.selectedItem.toString()
        val subjectSelection = spinnerSubject.selectedItem.toString()
        val unitSelection = spinnerUnit.selectedItem.toString()

        if (classSelection.contains("Select") || subjectSelection.contains("Select") || unitSelection.contains("Select")) {
            Toast.makeText(this, "Please select Class, Subject, and Unit", Toast.LENGTH_SHORT).show()
            return
        }

        val title = etGameTitle.text.toString().trim()
        val description = etGameDescription.text.toString().trim()
        val url = etGameUrl.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter game title", Toast.LENGTH_SHORT).show()
            return
        }

        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter game URL", Toast.LENGTH_SHORT).show()
            return
        }

        val validationResult = URLValidator.validateURL(url)
        if (!validationResult.isValid) {
            showValidationError("Invalid URL format. Please enter a valid web link")
            return
        }

        val classId = classSelection.replace(" ", "_").lowercase()
        val subjectId = subjectSelection.lowercase()
        val unitId = unitSelection.replace(" ", "_").lowercase()

        btnSaveGame.isEnabled = false
        showValidationProgress()

        lifecycleScope.launch {
            try {

                val safetyResult = contentSafetyValidator.validateContent(url)
                
                hideValidationProgress()
                
                if (!safetyResult.isSafe) {

                    showValidationError(safetyResult.reason ?: "Content blocked: This link contains inappropriate material")
                    

                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
                    contentSafetyValidator.logBlockedSubmission(userId, url, safetyResult.reason ?: "Inappropriate content detected")
                    
                    btnSaveGame.isEnabled = true
                    return@launch
                }
                

                if (safetyResult.reason != null) {
                    showValidationWarning(safetyResult.reason)
                }

                btnSaveGame.text = "Saving..."
                progressBar.visibility = View.VISIBLE

                val repository = com.tannu.edureach.data.repository.ContentRepository()
                val isDuplicate = repository.checkDuplicateGame(classId, subjectId, title, url)
                
                if (isDuplicate) {
                    Toast.makeText(this@AddGameActivity, "Duplicate game detected! This game already exists.", Toast.LENGTH_LONG).show()
                    btnSaveGame.isEnabled = true
                    btnSaveGame.text = "Add Game"
                    progressBar.visibility = View.GONE
                    return@launch
                }

                val game = GameModel(
                    title = title,
                    description = description,
                    gameType = "web_game",
                    activityClass = url,
                    classId = classId,
                    subjectId = subjectId,
                    unitId = unitId
                )

                db.collection("classes").document(classId)
                    .collection("subjects").document(subjectId)
                    .collection("games")
                    .add(game).await()
                    
                repository.addRecentUpload(title, "Game", classId, subjectId, unitSelection, url)

                Toast.makeText(this@AddGameActivity, "Game added successfully!", Toast.LENGTH_SHORT).show()
                finish()

            } catch (e: Exception) {
                Toast.makeText(this@AddGameActivity, "Failed to add game: ${e.message}", Toast.LENGTH_SHORT).show()
                btnSaveGame.isEnabled = true
                btnSaveGame.text = "Add Game"
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showValidationProgress() {
        progressBar.visibility = View.VISIBLE
        tvValidationStatus.apply {
            visibility = View.VISIBLE
            text = "Validating content safety..."
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
        }
    }
    
    private fun hideValidationProgress() {
        progressBar.visibility = View.GONE
        tvValidationStatus.visibility = View.GONE
    }
    
    private fun showValidationError(message: String) {
        tvValidationStatus.apply {
            visibility = View.VISIBLE
            text = message
            setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
    }
    
    private fun showValidationWarning(message: String) {
        tvValidationStatus.apply {
            visibility = View.VISIBLE
            text = message
            setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
        }
    }
}