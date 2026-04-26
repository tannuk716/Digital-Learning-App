package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        val btnStudent = findViewById<Button>(R.id.btnStudent)
        val btnTeacher = findViewById<Button>(R.id.btnTeacher)

        btnStudent.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("ROLE", "student")
            startActivity(intent)
        }

        btnTeacher.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("ROLE", "teacher")
            startActivity(intent)
        }
    }
}