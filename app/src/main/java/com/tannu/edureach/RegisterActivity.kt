package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var tvRegisterTitle: TextView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etSubject: EditText
    private lateinit var llStudentFields: LinearLayout
    private lateinit var spinnerClass: Spinner

    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var ivLion: ImageView
    private lateinit var ivMonkey: ImageView
    private lateinit var ivPanda: ImageView
    private lateinit var ivRabbit: ImageView

    private var selectedAvatar: String = "avatar_lion"
    private var userRole: String = "student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userRole = intent.getStringExtra("ROLE") ?: "student"

        tvRegisterTitle = findViewById(R.id.tvRegisterTitle)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etSubject = findViewById(R.id.etSubject)
        llStudentFields = findViewById(R.id.llStudentFields)
        spinnerClass = findViewById(R.id.spinnerClass)

        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tvLogin)
        progressBar = findViewById(R.id.progressBar)

        ivLion = findViewById(R.id.avatar_lion)
        ivMonkey = findViewById(R.id.avatar_monkey)
        ivPanda = findViewById(R.id.avatar_panda)
        ivRabbit = findViewById(R.id.avatar_rabbit)

        setupUI()

        btnRegister.setOnClickListener { registerUser() }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupUI() {
        if (userRole == "teacher") {
            tvRegisterTitle.text = "Teacher Registration 🍎"
            llStudentFields.visibility = View.GONE
            etSubject.visibility = View.VISIBLE
        } else {
            tvRegisterTitle.text = "Student Registration 🎓"
            llStudentFields.visibility = View.VISIBLE
            etSubject.visibility = View.GONE
            setupClassSpinner()
            setupAvatarSelection()
        }
    }

    private fun setupClassSpinner() {
        val classes = (1..10).map { "Class $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classes)
        spinnerClass.adapter = adapter
    }

    private fun setupAvatarSelection() {
        val avatars = listOf(ivLion, ivMonkey, ivPanda, ivRabbit)

        ivLion.isSelected = true

        avatars.forEach { image ->
            image.setOnClickListener {
                avatars.forEach { it.isSelected = false }
                image.isSelected = true
                selectedAvatar = resources.getResourceEntryName(image.id)
            }
        }
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val subject = etSubject.text.toString().trim()

        when {
            name.isEmpty() -> { etName.error = "Enter name"; return }
            email.isEmpty() -> { etEmail.error = "Enter email"; return }
            password.isEmpty() -> { etPassword.error = "Enter password"; return }
            password.length < 6 -> { etPassword.error = "Min 6 characters"; return }
            userRole == "teacher" && subject.isEmpty() -> {
                etSubject.error = "Enter subject"; return
            }
        }

        progressBar.visibility = View.VISIBLE
        btnRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid ?: run {
                    showError("Something went wrong")
                    return@addOnSuccessListener
                }

                val userMap = HashMap<String, Any>()
                userMap["name"] = name
                userMap["email"] = email
                userMap["role"] = userRole
                userMap["createdAt"] = System.currentTimeMillis()

                if (userRole == "student") {
                    userMap["className"] = spinnerClass.selectedItem.toString()
                    userMap["avatar"] = selectedAvatar
                } else {
                    userMap["subject"] = subject
                }

                db.collection("users").document(uid).set(userMap)
                    .addOnSuccessListener {
                        progressBar.visibility = View.GONE
                        toast("Registration Successful 🎉")

                        val intent = if (userRole == "student") {
                            Intent(this, StudentDashboardActivity::class.java)
                        } else {
                            Intent(this, TeacherDashboardActivity::class.java)
                        }

                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        showError("Failed to save data")
                    }
            }
            .addOnFailureListener {
                showError(it.message ?: "Registration failed")
            }
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        btnRegister.isEnabled = true
        toast(message)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}