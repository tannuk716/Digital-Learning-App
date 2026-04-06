package com.tannu.edureach.content_uploader

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tannu.edureach.R
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Activity to upload content from JSON file to Firebase
 * Place your content_data.json in assets folder
 */
class ContentUploadActivity : AppCompatActivity() {

    private lateinit var btnUpload: Button
    private lateinit var btnRemoveDuplicates: Button
    private lateinit var tvStatus: TextView
    private lateinit var progressBar: ProgressBar
    private val uploadHelper = ContentUploadHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_upload)

        btnUpload = findViewById(R.id.btnUpload)
        btnRemoveDuplicates = findViewById(R.id.btnRemoveDuplicates)
        tvStatus = findViewById(R.id.tvStatus)
        progressBar = findViewById(R.id.progressBar)

        btnUpload.setOnClickListener {
            uploadContentFromAssets()
        }

        btnRemoveDuplicates.setOnClickListener {
            removeDuplicatesFromAllClasses()
        }
    }

    private fun uploadContentFromAssets() {
        lifecycleScope.launch {
            try {
                btnUpload.isEnabled = false
                progressBar.visibility = android.view.View.VISIBLE
                tvStatus.text = "Reading content data..."

                // Read JSON from assets
                val jsonString = readJsonFromAssets("content_data.json")
                val contentItems = parseContentJson(jsonString)

                tvStatus.text = "Uploading ${contentItems.size} items..."

                // Upload in batch
                val result = uploadHelper.uploadBatch(contentItems)

                progressBar.visibility = android.view.View.GONE
                btnUpload.isEnabled = true

                val statusText = """
                    Upload Complete!
                    
                    Total: ${result.total}
                    Success: ${result.success}
                    Failed: ${result.failed}
                    
                    ${if (result.failed > 0) "Check logs for errors" else "All content uploaded successfully!"}
                """.trimIndent()

                tvStatus.text = statusText

                // Log failures
                result.results.filter { !it.success }.forEach { uploadResult ->
                    android.util.Log.e("ContentUpload", 
                        "Failed: ${uploadResult.item.title} - ${uploadResult.error}")
                }

                Toast.makeText(this@ContentUploadActivity, 
                    "Uploaded ${result.success} items", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                btnUpload.isEnabled = true
                tvStatus.text = "Error: ${e.message}"
                android.util.Log.e("ContentUpload", "Upload failed", e)
                Toast.makeText(this@ContentUploadActivity, 
                    "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun removeDuplicatesFromAllClasses() {
        lifecycleScope.launch {
            try {
                btnRemoveDuplicates.isEnabled = false
                progressBar.visibility = android.view.View.VISIBLE
                tvStatus.text = "Removing duplicates..."

                var totalRemoved = 0
                val classes = listOf("class_1", "class_2", "class_3", "class_4", "class_5")
                val subjects = listOf("english", "hindi", "maths", "science")
                val units = listOf("unit_1", "unit_2", "unit_3", "unit_4", "unit_5", "unit_6")

                for (classId in classes) {
                    for (subjectId in subjects) {
                        for (unitId in units) {
                            val result = uploadHelper.removeDuplicates(classId, subjectId, unitId)
                            if (result.success) {
                                totalRemoved += result.duplicatesRemoved
                            }
                        }
                    }
                }

                progressBar.visibility = android.view.View.GONE
                btnRemoveDuplicates.isEnabled = true
                tvStatus.text = "Removed $totalRemoved duplicate items"
                Toast.makeText(this@ContentUploadActivity, 
                    "Removed $totalRemoved duplicates", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                progressBar.visibility = android.view.View.GONE
                btnRemoveDuplicates.isEnabled = true
                tvStatus.text = "Error: ${e.message}"
                Toast.makeText(this@ContentUploadActivity, 
                    "Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun readJsonFromAssets(fileName: String): String {
        val inputStream = assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.use { it.readText() }
    }

    private fun parseContentJson(jsonString: String): List<ContentUploadHelper.ContentItem> {
        val items = mutableListOf<ContentUploadHelper.ContentItem>()
        val json = JSONObject(jsonString)
        val contentArray = json.getJSONArray("content")

        for (i in 0 until contentArray.length()) {
            val classObj = contentArray.getJSONObject(i)
            val classId = classObj.getString("classId")
            val subjectsArray = classObj.getJSONArray("subjects")

            for (j in 0 until subjectsArray.length()) {
                val subjectObj = subjectsArray.getJSONObject(j)
                val subjectId = subjectObj.getString("subjectId")
                val unitsArray = subjectObj.getJSONArray("units")

                for (k in 0 until unitsArray.length()) {
                    val unitObj = unitsArray.getJSONObject(k)
                    val unitId = unitObj.getString("unitId")

                    // Parse videos
                    if (unitObj.has("videos")) {
                        val videosArray = unitObj.getJSONArray("videos")
                        for (v in 0 until videosArray.length()) {
                            val video = videosArray.getJSONObject(v)
                            items.add(ContentUploadHelper.ContentItem(
                                classId = classId,
                                subjectId = subjectId,
                                unitId = unitId,
                                type = ContentUploadHelper.ContentType.VIDEO,
                                title = video.getString("title"),
                                description = video.optString("description", ""),
                                url = video.getString("url"),
                                isYoutube = video.optBoolean("isYoutube", false)
                            ))
                        }
                    }

                    // Parse notes
                    if (unitObj.has("notes")) {
                        val notesArray = unitObj.getJSONArray("notes")
                        for (n in 0 until notesArray.length()) {
                            val note = notesArray.getJSONObject(n)
                            items.add(ContentUploadHelper.ContentItem(
                                classId = classId,
                                subjectId = subjectId,
                                unitId = unitId,
                                type = ContentUploadHelper.ContentType.NOTE,
                                title = note.getString("title"),
                                description = note.optString("description", ""),
                                url = note.getString("url")
                            ))
                        }
                    }

                    // Parse quizzes
                    if (unitObj.has("quizzes")) {
                        val quizzesArray = unitObj.getJSONArray("quizzes")
                        for (q in 0 until quizzesArray.length()) {
                            val quiz = quizzesArray.getJSONObject(q)
                            val questions = parseQuestions(quiz.getJSONArray("questions"))
                            items.add(ContentUploadHelper.ContentItem(
                                classId = classId,
                                subjectId = subjectId,
                                unitId = unitId,
                                type = ContentUploadHelper.ContentType.QUIZ,
                                title = quiz.getString("title"),
                                questions = questions
                            ))
                        }
                    }
                }
            }
        }

        return items
    }

    private fun parseQuestions(questionsArray: JSONArray): List<com.tannu.edureach.data.model.QuestionModel> {
        val questions = mutableListOf<com.tannu.edureach.data.model.QuestionModel>()
        for (i in 0 until questionsArray.length()) {
            val q = questionsArray.getJSONObject(i)
            val optionsArray = q.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }
            questions.add(com.tannu.edureach.data.model.QuestionModel(
                text = q.getString("text"),
                options = options,
                correctIndex = q.getInt("correctIndex"),
                explanation = q.optString("explanation", "")
            ))
        }
        return questions
    }
}
