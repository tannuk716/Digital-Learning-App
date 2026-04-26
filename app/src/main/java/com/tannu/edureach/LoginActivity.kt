package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)

        if (auth.currentUser != null) {
            routeUserToDashboard()
            return
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (BuildConfig.DEBUG && email == "test@test.com" && password == "test123") {
                Toast.makeText(this, "Development Mode: Bypassing login", Toast.LENGTH_SHORT).show()

                val prefs = getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("USER_EMAIL", email)
                    putString("USER_ROLE", "student")
                    putInt("USER_CLASS", 1)
                    apply()
                }
                startActivity(Intent(this, StudentDashboardActivity::class.java))
                finish()
                return@setOnClickListener
            }
            

            if (BuildConfig.DEBUG && email == "teacher@test.com" && password == "test123") {
                Toast.makeText(this, "Development Mode: Teacher login", Toast.LENGTH_SHORT).show()
                val prefs = getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("USER_EMAIL", email)
                    putString("USER_ROLE", "teacher")
                    apply()
                }
                startActivity(Intent(this, TeacherDashboardActivity::class.java))
                finish()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            val prefs = getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().remove("APP_LANG").apply()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    
                    if (task.isSuccessful) {
                        routeUserToDashboard()
                    } else {
                        val exception = task.exception
                        val errorMessage = when {
                            exception?.message?.contains("network", ignoreCase = true) == true -> 
                                "Network error. Please check your internet connection and try again.\n\nDEV TIP: Use test@test.com / test123 for student or teacher@test.com / test123 for teacher"
                            exception?.message?.contains("timeout", ignoreCase = true) == true -> 
                                "Connection timeout. Please try again.\n\nDEV TIP: Use test@test.com / test123 for testing"
                            exception?.message?.contains("password", ignoreCase = true) == true -> 
                                "Invalid email or password. Please try again."
                            exception?.message?.contains("user", ignoreCase = true) == true -> 
                                "User not found. Please check your email or register."
                            else -> "Login Failed: ${exception?.message ?: "Unknown error"}\n\nDEV TIP: Use test@test.com / test123 for testing"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        android.util.Log.e("LoginActivity", "Login error: ${exception?.message}", exception)
                    }
                }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RoleSelectionActivity::class.java))
        }
    }

    private fun routeUserToDashboard() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            return
        }
        
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                val role = document.getString("role") ?: "student"
                val targetClass = if (role == "teacher") {
                    TeacherDashboardActivity::class.java.name
                } else {
                    StudentDashboardActivity::class.java.name
                }
                
                val prefs = getSharedPreferences("AppPrefs", android.content.Context.MODE_PRIVATE)
                val existingLang = prefs.getString("APP_LANG", null)
                
                if (existingLang != null) {
                    val clazz = Class.forName(targetClass)
                    startActivity(Intent(this, clazz))
                } else {
                    val intent = Intent(this, LanguageSelectionActivity::class.java)
                    intent.putExtra("TARGET_ACTIVITY", targetClass)
                    startActivity(intent)
                }
                finish()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                Toast.makeText(this, "Failed to fetch user role. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}