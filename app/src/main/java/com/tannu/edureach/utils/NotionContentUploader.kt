package com.tannu.edureach.utils

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.data.model.NoteContent
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

object NotionContentUploader {
    
    private const val TAG = "NotionContentUploader"
    private val db = FirebaseFirestore.getInstance()
    
    

    suspend fun uploadNotionContent(context: Context): Result<String> {
        return try {

            val jsonString = context.assets.open("notion_content_links.json")
                .bufferedReader()
                .use { it.readText() }
            
            val jsonObject = JSONObject(jsonString)
            var totalUploaded = 0
            var totalSkipped = 0
            

            val classKeys = jsonObject.keys()
            while (classKeys.hasNext()) {
                val classId = classKeys.next()
                val subjectsObj = jsonObject.getJSONObject(classId)
                

                val subjectKeys = subjectsObj.keys()
                while (subjectKeys.hasNext()) {
                    val subjectId = subjectKeys.next()
                    val notionUrl = subjectsObj.getString(subjectId)
                    

                    val note = NoteContent(
                        title = "${subjectId.capitalize()} - Complete Notes",
                        description = "Complete study material for ${subjectId.capitalize()}",
                        fileUrl = notionUrl,
                        timestamp = System.currentTimeMillis()
                    )
                    

                    val unitId = "unit_1"
                    

                    val exists = checkIfNoteExists(classId, subjectId, unitId, note.title, notionUrl)
                    
                    if (!exists) {
                        uploadNote(classId, subjectId, unitId, note)
                        totalUploaded++
                        Log.d(TAG, "Uploaded: $classId/$subjectId - ${note.title}")
                    } else {
                        totalSkipped++
                        Log.d(TAG, "Skipped (already exists): $classId/$subjectId - ${note.title}")
                    }
                }
            }
            
            Result.success("Upload complete! Uploaded: $totalUploaded, Skipped: $totalSkipped")
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading Notion content", e)
            Result.failure(e)
        }
    }
    
    

    private suspend fun checkIfNoteExists(
        classId: String,
        subjectId: String,
        unitId: String,
        title: String,
        url: String
    ): Boolean {
        return try {
            val snapshot = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("notes")
                .whereEqualTo("title", title)
                .whereEqualTo("fileUrl", url)
                .get()
                .await()
            
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
    
    

    private suspend fun uploadNote(
        classId: String,
        subjectId: String,
        unitId: String,
        note: NoteContent
    ) {
        try {
            db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("notes")
                .add(note)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading note", e)
        }
    }
    
    

    suspend fun deleteAllNotes(): Result<String> {
        return try {
            var totalDeleted = 0
            

            for (classNum in 1..10) {
                val classId = "class_$classNum"
                

                val subjects = listOf("english", "hindi", "maths", "science", "evs", "sst")
                
                for (subjectId in subjects) {

                    for (unitNum in 1..3) {
                        val unitId = "unit_$unitNum"
                        
                        try {
                            val snapshot = db.collection("classes").document(classId)
                                .collection("subjects").document(subjectId)
                                .collection("units").document(unitId)
                                .collection("notes")
                                .get()
                                .await()
                            
                            for (doc in snapshot.documents) {
                                doc.reference.delete().await()
                                totalDeleted++
                            }
                        } catch (e: Exception) {

                        }
                    }
                }
            }
            
            Result.success("Deleted $totalDeleted notes")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting notes", e)
            Result.failure(e)
        }
    }
}