package com.tannu.edureach.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object DuplicateRemover {
    
    private const val TAG = "DuplicateRemover"
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun removeDuplicateVideos(classId: String, subjectId: String, unitId: String): Int {
        var removedCount = 0
        try {
            val videosRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("videos")
            
            val snapshot = videosRef.get().await()
            val videos = snapshot.documents
            
            val seenVideos = mutableMapOf<String, String>()
            
            for (doc in videos) {
                val title = doc.getString("title") ?: continue
                val url = doc.getString("videoUrl") ?: continue
                val key = "$title|$url"
                
                if (seenVideos.containsKey(key)) {
                    doc.reference.delete().await()
                    removedCount++
                    Log.d(TAG, "Removed duplicate video: $title")
                } else {
                    seenVideos[key] = doc.id
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing duplicate videos", e)
        }
        return removedCount
    }
    
    suspend fun removeDuplicateNotes(classId: String, subjectId: String, unitId: String): Int {
        var removedCount = 0
        try {
            val notesRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("notes")
            
            val snapshot = notesRef.get().await()
            val notes = snapshot.documents
            
            val seenNotes = mutableMapOf<String, String>()
            
            for (doc in notes) {
                val title = doc.getString("title") ?: continue
                val url = doc.getString("fileUrl") ?: continue
                val key = "$title|$url"
                
                if (seenNotes.containsKey(key)) {
                    doc.reference.delete().await()
                    removedCount++
                    Log.d(TAG, "Removed duplicate note: $title")
                } else {
                    seenNotes[key] = doc.id
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing duplicate notes", e)
        }
        return removedCount
    }
    
    suspend fun removeDuplicateQuizzes(classId: String, subjectId: String, unitId: String): Int {
        var removedCount = 0
        try {
            val quizzesRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("quizzes")
            
            val snapshot = quizzesRef.get().await()
            val quizzes = snapshot.documents
            
            val seenQuizzes = mutableMapOf<String, String>()
            
            for (doc in quizzes) {
                val title = doc.getString("title") ?: continue
                
                if (seenQuizzes.containsKey(title)) {
                    doc.reference.delete().await()
                    removedCount++
                    Log.d(TAG, "Removed duplicate quiz: $title")
                } else {
                    seenQuizzes[title] = doc.id
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing duplicate quizzes", e)
        }
        return removedCount
    }
    
    suspend fun removeDuplicateGames(classId: String, subjectId: String): Int {
        var removedCount = 0
        try {
            val gamesRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("games")
            
            val snapshot = gamesRef.get().await()
            val games = snapshot.documents
            
            val seenGames = mutableMapOf<String, String>()
            
            for (doc in games) {
                val title = doc.getString("title") ?: continue
                val url = doc.getString("activityClass") ?: continue
                val key = "$title|$url"
                
                if (seenGames.containsKey(key)) {
                    doc.reference.delete().await()
                    removedCount++
                    Log.d(TAG, "Removed duplicate game: $title")
                } else {
                    seenGames[key] = doc.id
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing duplicate games", e)
        }
        return removedCount
    }
    
    suspend fun removeAllDuplicates(classId: String, subjectId: String, unitId: String): Map<String, Int> {
        val results = mutableMapOf<String, Int>()
        
        results["videos"] = removeDuplicateVideos(classId, subjectId, unitId)
        results["notes"] = removeDuplicateNotes(classId, subjectId, unitId)
        results["quizzes"] = removeDuplicateQuizzes(classId, subjectId, unitId)
        results["games"] = removeDuplicateGames(classId, subjectId)
        
        return results
    }
}