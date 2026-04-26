package com.tannu.edureach.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object ProgressManager {

    fun awardPoints(pointsToAdd: Long) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)
        
        userRef.update("points", FieldValue.increment(pointsToAdd))
            .addOnFailureListener {

                userRef.set(mapOf("points" to pointsToAdd), SetOptions.merge())
            }
    }

    fun updateStreak() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)
        
        userRef.get().addOnSuccessListener { document ->
            val lastStudyDate = document.getLong("lastStudyDate") ?: 0L
            val currentStreak = document.getLong("streak") ?: 0L
            
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            
            val todayStr = dateFormat.format(Date(currentTime))
            val lastStudyStr = if (lastStudyDate > 0) dateFormat.format(Date(lastStudyDate)) else ""
            
            if (lastStudyDate > 0 && todayStr == lastStudyStr) {

                return@addOnSuccessListener
            }
            
            val todayCal = Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val diffDays = if (lastStudyDate == 0L) {
                -1L
            } else {
                val lastStudyCal = Calendar.getInstance().apply {
                    timeInMillis = lastStudyDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val diffMs = todayCal.timeInMillis - lastStudyCal.timeInMillis
                TimeUnit.MILLISECONDS.toDays(diffMs)
            }
            

            val nextStreak = if (diffDays == 1L) {
                currentStreak + 1
            } else {
                1L
            }
            
            val updates = mapOf(
                "streak" to nextStreak,
                "lastStudyDate" to currentTime
            )
            
            userRef.set(updates, SetOptions.merge())
        }
    }
}