package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.repository.ContentRepository
import com.tannu.edureach.utils.URLValidator
import com.tannu.edureach.utils.ContentSafetyValidator
import kotlinx.coroutines.launch

class AddLearningContentActivity : AppCompatActivity() {
    
    private val repository = ContentRepository()
    private lateinit var contentSafetyValidator: ContentSafetyValidator
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_content)

        contentSafetyValidator = ContentSafetyValidator(this)

        val spinnerClass = findViewById<Spinner>(R.id.spinnerClass)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val spinnerUnit = findViewById<Spinner>(R.id.spinnerUnit)
        
        val radioGroupType = findViewById<RadioGroup>(R.id.radioGroupType)
        val rbQuiz = findViewById<RadioButton>(R.id.rbQuiz)
        val rbVideo = findViewById<RadioButton>(R.id.rbVideo)
        
        val layoutDataInput = findViewById<LinearLayout>(R.id.layoutDataInput)
        val etTitle = findViewById<EditText>(R.id.etContentTitle)
        val etUrl = findViewById<EditText>(R.id.etContentUrl)
        val etDesc = findViewById<EditText>(R.id.etContentDesc)
        val btnSaveContent = findViewById<Button>(R.id.btnSaveContent)

        radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbQuiz) {
                layoutDataInput.visibility = View.GONE
                btnSaveContent.text = "Continue to Add Quiz ❓"
            } else {
                layoutDataInput.visibility = View.VISIBLE
                btnSaveContent.text = "Upload Content ☁️"
            }
        }

        btnSaveContent.setOnClickListener {
            val classSel = spinnerClass.selectedItem.toString()
            val subjectSel = spinnerSubject.selectedItem.toString()
            val unitSel = spinnerUnit.selectedItem.toString()

            if (classSel.contains("Select") || subjectSel.contains("Select") || unitSel.contains("Select")) {
                Toast.makeText(this, "Please select Class, Subject, and Unit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val classId = classSel.replace(" ", "_").lowercase()
            val subjectId = subjectSel.replace(" ", "_").lowercase()
            val unitId = unitSel.replace(" ", "_").lowercase()

            if (rbQuiz.isChecked) {
                val intent = Intent(this, AddQuizActivity::class.java).apply {
                    putExtra("CLASS_ID", classId)
                    putExtra("SUBJECT_ID", subjectId)
                    putExtra("UNIT_ID", unitId)
                }
                startActivity(intent)
                finish()
            } else {
                val title = etTitle.text.toString().trim()
                val url = etUrl.text.toString().trim()
                val desc = etDesc.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(this, "Please enter Title.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (url.isEmpty()) {
                    Toast.makeText(this, "Please enter a URL.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val validationResult = URLValidator.validateURL(url)
                if (!validationResult.isValid) {
                    showValidationError("Invalid URL format. Please enter a valid YouTube or Google Drive link")
                    return@setOnClickListener
                }

                btnSaveContent.isEnabled = false
                showValidationProgress()

                lifecycleScope.launch {

                    val safetyResult = contentSafetyValidator.validateContent(url)
                    
                    hideValidationProgress()
                    
                    if (!safetyResult.isSafe) {

                        showValidationError(safetyResult.reason ?: "Content blocked: This link contains inappropriate material")
                        

                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
                        contentSafetyValidator.logBlockedSubmission(userId, url, safetyResult.reason ?: "Inappropriate content detected")
                        
                        btnSaveContent.isEnabled = true
                        return@launch
                    }
                    

                    if (safetyResult.reason != null) {
                        showValidationWarning(safetyResult.reason)
                    }
                    

                    btnSaveContent.text = "Uploading..."
                    
                    val success = if (rbVideo.isChecked) {
                        val isYouTube = validationResult.urlType == URLValidator.URLType.YOUTUBE
                        val video = VideoContent(title = title, description = desc, videoUrl = url, isYoutube = isYouTube)
                        repository.uploadVideo(classId, subjectId, unitId, video)
                    } else {
                        val note = NoteContent(title = title, description = desc, fileUrl = url)
                        repository.uploadNote(classId, subjectId, unitId, note)
                    }

                    if (success) {
                        Toast.makeText(this@AddLearningContentActivity, "Upload Successful!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddLearningContentActivity, "Duplicate content detected! This content already exists.", Toast.LENGTH_LONG).show()
                        btnSaveContent.isEnabled = true
                        btnSaveContent.text = "Upload Content ☁️"
                    }
                }
            }
        }
    }

    private fun showValidationProgress() {
        findViewById<ProgressBar>(R.id.progressValidation).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvValidationStatus).apply {
            visibility = View.VISIBLE
            text = "Validating content safety..."
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
        }
    }
    
    private fun hideValidationProgress() {
        findViewById<ProgressBar>(R.id.progressValidation).visibility = View.GONE
        findViewById<TextView>(R.id.tvValidationStatus).visibility = View.GONE
    }
    
    private fun showValidationError(message: String) {
        findViewById<TextView>(R.id.tvValidationStatus).apply {
            visibility = View.VISIBLE
            text = message
            setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }
    }
    
    private fun showValidationWarning(message: String) {
        findViewById<TextView>(R.id.tvValidationStatus).apply {
            visibility = View.VISIBLE
            text = message
            setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
        }
    }
}