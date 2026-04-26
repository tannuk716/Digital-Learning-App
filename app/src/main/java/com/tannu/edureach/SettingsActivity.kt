package com.tannu.edureach

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.tannu.edureach.utils.ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val spinnerLanguage = findViewById<Spinner>(R.id.spinnerLanguage)
        val spinnerClass = findViewById<Spinner>(R.id.spinnerClass)
        val tvSelectClassLabel = findViewById<TextView>(R.id.tvSelectClassLabel)

        val languages = arrayOf("English", "Hindi", "Punjabi")
        spinnerLanguage.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)

        val classes = (1..10).map { "Class $it" }.toTypedArray()
        spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classes)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("APP_LANG", "en") ?: "en"
        val currentClass = prefs.getInt("USER_CLASS", 1)
        val userRole = prefs.getString("USER_ROLE", "Student") ?: "Student"

        when (currentLang) {
            "hi" -> spinnerLanguage.setSelection(1)
            "pa" -> spinnerLanguage.setSelection(2)
            else -> spinnerLanguage.setSelection(0)
        }
        spinnerClass.setSelection(currentClass - 1)

        if (userRole == "Teacher") {
            spinnerClass.visibility = View.GONE
            tvSelectClassLabel.visibility = View.GONE
        }

        findViewById<Button>(R.id.btnSaveSettings).setOnClickListener {
            val selectedLangIndex = spinnerLanguage.selectedItemPosition
            val langCode = when (selectedLangIndex) {
                1 -> "hi"
                2 -> "pa"
                else -> "en"
            }
            
            com.tannu.edureach.utils.LanguageManager.setLocale(this, langCode)

            if (userRole == "Student") {
                val newClass = spinnerClass.selectedItemPosition + 1
                prefs.edit().putInt("USER_CLASS", newClass).apply()
                

                auth.currentUser?.uid?.let { uid ->
                    db.collection("users").document(uid).update("className", "Class $newClass")
                }
            }
            
            Toast.makeText(this, "Settings Saved. Restarting...", Toast.LENGTH_SHORT).show()
            

            val intent = Intent(this, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}