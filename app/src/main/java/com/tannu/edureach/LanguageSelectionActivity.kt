package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.utils.LanguageManager

class LanguageSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_language_selection)

        val targetClassName = intent.getStringExtra("TARGET_ACTIVITY") ?: StudentDashboardActivity::class.java.name

        findViewById<Button>(R.id.btnEnglish).setOnClickListener {
            selectLanguageAndNavigate("en", targetClassName)
        }
        
        findViewById<Button>(R.id.btnHindi).setOnClickListener {
            selectLanguageAndNavigate("hi", targetClassName)
        }
        
        findViewById<Button>(R.id.btnPunjabi).setOnClickListener {
            selectLanguageAndNavigate("pa", targetClassName)
        }
    }

    private fun selectLanguageAndNavigate(langCode: String, targetClassName: String) {

        LanguageManager.setLocale(this, langCode)
        

        try {
            val targetClass = Class.forName(targetClassName)
            val intent = Intent(this, targetClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {

            startActivity(Intent(this, StudentDashboardActivity::class.java))
            finish()
        }
    }
}