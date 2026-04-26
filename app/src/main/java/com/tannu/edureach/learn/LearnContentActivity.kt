package com.tannu.edureach.learn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R
import com.tannu.edureach.data.OfflineDataLoader
import com.tannu.edureach.practice.QuizActivity

class LearnContentActivity : AppCompatActivity() {

    private lateinit var sensorUtil: com.tannu.edureach.utils.SensorManagerUtil
    private lateinit var overlayPaused: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_content)

        sensorUtil = com.tannu.edureach.utils.SensorManagerUtil(this)
        overlayPaused = findViewById(R.id.overlayPaused)

        sensorUtil.onProximityChanged = { isNear ->
            if (isNear) {
                overlayPaused.visibility = View.VISIBLE
            } else {
                overlayPaused.visibility = View.GONE
            }
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val classId = intent.getIntExtra("CLASS_ID", 1)
        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: ""
        val unitId = intent.getStringExtra("UNIT_ID") ?: ""

        val classes = OfflineDataLoader.loadOfflineData(this)
        val c = classes.find { it.id == classId } ?: classes.first()
        val s = c.subjects.find { it.name == subjectName } ?: c.subjects.firstOrNull()
        val unit = s?.units?.find { it.id == unitId }

        val tvTitle = findViewById<TextView>(R.id.tvContentTitle)
        val tvExplanation = findViewById<TextView>(R.id.tvExplanation)
        val tvNotes = findViewById<TextView>(R.id.tvNotes)
        val tvExamples = findViewById<TextView>(R.id.tvExamples)
        val btnVideo = findViewById<Button>(R.id.btnWatchVideo)
        val btnQuiz = findViewById<Button>(R.id.btnTakeQuiz)

        if (unit != null) {
            tvTitle.text = unit.title
            tvExplanation.text = unit.explanation
            tvNotes.text = unit.notes.joinToString("\n• ", prefix = "• ")
            if (unit.examples.isNotEmpty()) {
                tvExamples.text = unit.examples.joinToString("\n‣ ", prefix = "‣ ")
            } else {
                tvExamples.text = "No examples available."
            }

            btnVideo.setOnClickListener {
                if (unit.videoUrl.isNotEmpty()) {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(unit.videoUrl)))
                    } catch (e: Exception) {
                        Toast.makeText(this, "No app found to play video.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Video not available offline.", Toast.LENGTH_SHORT).show()
                }
            }

            btnQuiz.setOnClickListener {
                val qIntent = Intent(this, QuizActivity::class.java)
                qIntent.putExtra("UNIT_ID", unit.id)
                qIntent.putExtra("CLASS_ID", classId)
                qIntent.putExtra("SUBJECT_NAME", subjectName)
                startActivity(qIntent)
            }
        } else {
            tvTitle.text = "Error Loading Content"
        }
    }

    override fun onResume() {
        super.onResume()
        sensorUtil.registerSensors()
    }

    override fun onPause() {
        super.onPause()
        sensorUtil.unregisterSensors()
    }
}