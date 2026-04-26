package com.tannu.edureach

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var etEditName: EditText
    private lateinit var etEditEmail: EditText
    private lateinit var etEditPhone: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var llAvatarSelection: LinearLayout
    private lateinit var pbProfile: ProgressBar

    private lateinit var avatars: List<ImageView>
    private var selectedAvatar: String = "avatar_lion"

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var userRole: String = "student"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEditName = findViewById(R.id.etEditName)
        etEditEmail = findViewById(R.id.etEditEmail)
        etEditPhone = findViewById(R.id.etEditPhone)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnLogout = findViewById(R.id.btnLogout)
        llAvatarSelection = findViewById(R.id.llAvatarSelection)
        pbProfile = findViewById(R.id.pbProfile)

        val ivLion: ImageView = findViewById(R.id.edit_avatar_lion)
        val ivMonkey: ImageView = findViewById(R.id.edit_avatar_monkey)
        val ivPanda: ImageView = findViewById(R.id.edit_avatar_panda)
        val ivRabbit: ImageView = findViewById(R.id.edit_avatar_rabbit)
        avatars = listOf(ivLion, ivMonkey, ivPanda, ivRabbit)

        setupAvatarClicks()
        loadUserProfile()

        btnSaveProfile.setOnClickListener { saveProfile() }
        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupAvatarClicks() {
        for (imageView in avatars) {
            imageView.setOnClickListener {
                avatars.forEach { iv -> iv.isSelected = false }
                imageView.isSelected = true
                selectedAvatar = resources.getResourceEntryName(imageView.id).replace("edit_", "")
            }
        }
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        pbProfile.visibility = View.VISIBLE
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                pbProfile.visibility = View.GONE
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    userRole = document.getString("role") ?: "student"

                    etEditName.setText(name)
                    etEditEmail.setText(email)
                    etEditPhone.setText(phone)

                    if (userRole == "student") {
                        llAvatarSelection.visibility = View.VISIBLE
                        val avatar = document.getString("avatar") ?: "avatar_lion"
                        selectedAvatar = avatar
                        avatars.forEach { it.isSelected = false }
                        val selectedImageViewId = resources.getIdentifier("edit_\$avatar", "id", packageName)
                        if (selectedImageViewId != 0) {
                            findViewById<ImageView>(selectedImageViewId)?.isSelected = true
                        }
                    } else {
                        llAvatarSelection.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener {
                pbProfile.visibility = View.GONE
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val name = etEditName.text.toString().trim()
        val phone = etEditPhone.text.toString().trim()

        if (name.isEmpty()) {
            etEditName.error = "Name cannot be empty"
            return
        }

        pbProfile.visibility = View.VISIBLE
        btnSaveProfile.isEnabled = false

        val updateMap = mutableMapOf<String, Any>(
            "name" to name,
            "phone" to phone
        )
        if (userRole == "student") {
            updateMap["avatar"] = selectedAvatar
        }

        db.collection("users").document(uid).update(updateMap)
            .addOnSuccessListener {
                pbProfile.visibility = View.GONE
                btnSaveProfile.isEnabled = true
                Toast.makeText(this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                pbProfile.visibility = View.GONE
                btnSaveProfile.isEnabled = true
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}