package com.tannu.edureach.tracking

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.tannu.edureach.evaluation.models.ActivityType
import com.tannu.edureach.evaluation.models.Mistake
import com.tannu.edureach.tracking.models.MistakePattern
import com.tannu.edureach.tracking.models.MistakeRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProgressTracker(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    
    companion object {
        private const val TAG = "ProgressTracker"
        private const val USERS_COLLECTION = "users"
        private const val COMM_SKILLS_COLLECTION = "communication_skills"
        private const val PROGRESS_DOCUMENT = "progress"
        private const val MISTAKES_COLLECTION = "mistakes"
        private const val PATTERNS_DOCUMENT = "patterns"
    }
    
    suspend fun saveMistakes(
        mistakes: List<Mistake>,
        score: Int,
        activityType: ActivityType,
        questionText: String = ""
    ) = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: return@withContext
            val mistakesRef = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(COMM_SKILLS_COLLECTION)
                .document(PROGRESS_DOCUMENT)
                .collection(MISTAKES_COLLECTION)
            

            mistakes.forEach { mistake ->
                val mistakeData = hashMapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "activityType" to activityType.name,
                    "mistakeType" to mistake.type.name,
                    "description" to mistake.description,
                    "score" to score,
                    "severity" to mistake.severity.name,
                    "questionText" to questionText
                )
                
                mistakesRef.add(mistakeData).await()
            }
            

            updateMistakePatterns(uid, mistakes)
            
            Log.d(TAG, "Saved ${mistakes.size} mistakes for $activityType")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving mistakes", e)

        }
    }
    
    private suspend fun updateMistakePatterns(uid: String, mistakes: List<Mistake>) {
        try {
            val patternsRef = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(COMM_SKILLS_COLLECTION)
                .document(PROGRESS_DOCUMENT)
                .collection(MISTAKES_COLLECTION)
                .document(PATTERNS_DOCUMENT)
            

            val snapshot = patternsRef.get().await()
            val currentPatterns = snapshot.data?.toMutableMap() ?: mutableMapOf()
            

            mistakes.forEach { mistake ->
                val key = mistake.type.name
                val currentFreq = (currentPatterns[key] as? Long)?.toInt() ?: 0
                currentPatterns[key] = currentFreq + 1
                currentPatterns["${key}_lastOccurrence"] = System.currentTimeMillis()
                currentPatterns["${key}_isCommon"] = (currentFreq + 1) > 3
            }
            
            patternsRef.set(currentPatterns, SetOptions.merge()).await()
            
            Log.d(TAG, "Updated mistake patterns")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating patterns", e)
        }
    }
    
    suspend fun getCommonMistakes(
        activityType: ActivityType,
        limit: Int = 5
    ): List<MistakePattern> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: return@withContext emptyList()
            
            val patternsRef = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(COMM_SKILLS_COLLECTION)
                .document(PROGRESS_DOCUMENT)
                .collection(MISTAKES_COLLECTION)
                .document(PATTERNS_DOCUMENT)
            
            val snapshot = patternsRef.get().await()
            val data = snapshot.data ?: return@withContext emptyList()
            

            val patterns = mutableListOf<MistakePattern>()
            val processedTypes = mutableSetOf<String>()
            
            data.keys.forEach { key ->
                if (!key.contains("_") && !processedTypes.contains(key)) {
                    processedTypes.add(key)
                    val frequency = (data[key] as? Long)?.toInt() ?: 0
                    val lastOccurrence = (data["${key}_lastOccurrence"] as? Long) ?: 0L
                    val isCommon = (data["${key}_isCommon"] as? Boolean) ?: false
                    
                    if (isCommon) {
                        patterns.add(MistakePattern(
                            mistakeType = key,
                            frequency = frequency,
                            lastOccurrence = lastOccurrence,
                            isCommon = isCommon
                        ))
                    }
                }
            }
            
            patterns.sortedByDescending { it.frequency }.take(limit)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting common mistakes", e)
            emptyList()
        }
    }
    
    suspend fun getMistakeHistory(
        days: Int = 30
    ): List<MistakeRecord> = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: return@withContext emptyList()
            
            val thirtyDaysAgo = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
            
            val snapshot = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .collection(COMM_SKILLS_COLLECTION)
                .document(PROGRESS_DOCUMENT)
                .collection(MISTAKES_COLLECTION)
                .whereGreaterThan("timestamp", thirtyDaysAgo)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                if (doc.id == PATTERNS_DOCUMENT) return@mapNotNull null
                try {
                    MistakeRecord(
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        activityType = doc.getString("activityType") ?: "",
                        mistakeType = doc.getString("mistakeType") ?: "",
                        description = doc.getString("description") ?: "",
                        score = doc.getLong("score")?.toInt() ?: 0,
                        questionText = doc.getString("questionText") ?: ""
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing mistake record", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting mistake history", e)
            emptyList()
        }
    }
    
    private fun getUserMistakesPath(): String {
        val uid = auth.currentUser?.uid ?: ""
        return "$USERS_COLLECTION/$uid/$COMM_SKILLS_COLLECTION/$PROGRESS_DOCUMENT/$MISTAKES_COLLECTION"
    }
}