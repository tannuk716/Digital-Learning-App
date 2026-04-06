package com.tannu.edureach.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utility to validate and fix content that's in wrong class/subject/unit
 */
object ContentValidator {
    
    private val db = FirebaseFirestore.getInstance()
    
    data class ContentIssue(
        val contentId: String,
        val contentTitle: String,
        val currentClass: String,
        val currentSubject: String,
        val currentUnit: String,
        val suggestedClass: String? = null,
        val suggestedSubject: String? = null,
        val suggestedUnit: String? = null,
        val issueType: String // "wrong_class", "wrong_subject", "wrong_unit", "duplicate"
    )
    
    /**
     * Scan all content and identify potential issues
     */
    suspend fun scanForIssues(): List<ContentIssue> {
        val issues = mutableListOf<ContentIssue>()
        
        // Scan classes 1-10
        for (classNum in 1..10) {
            val classId = "class_$classNum"
            
            // Scan subjects
            val subjects = listOf("english", "hindi", "maths", "math", "science", "social_science")
            for (subjectId in subjects) {
                
                // Scan units 1-20
                for (unitNum in 1..20) {
                    val unitId = "unit_$unitNum"
                    
                    // Check notes
                    try {
                        val notes = db.collection("classes").document(classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(unitId)
                            .collection("notes")
                            .get()
                            .await()
                        
                        for (doc in notes.documents) {
                            val title = doc.getString("title") ?: ""
                            val url = doc.getString("fileUrl") ?: ""
                            
                            // Check if title suggests wrong class
                            val titleLower = title.lowercase()
                            for (checkClass in 1..10) {
                                if (checkClass != classNum && 
                                    (titleLower.contains("class $checkClass") || 
                                     titleLower.contains("class$checkClass") ||
                                     titleLower.contains("grade $checkClass"))) {
                                    issues.add(ContentIssue(
                                        contentId = doc.id,
                                        contentTitle = title,
                                        currentClass = classId,
                                        currentSubject = subjectId,
                                        currentUnit = unitId,
                                        suggestedClass = "class_$checkClass",
                                        issueType = "wrong_class"
                                    ))
                                }
                            }
                            
                            // Check if title suggests wrong subject
                            if (subjectId == "maths" || subjectId == "math") {
                                if (titleLower.contains("english") || titleLower.contains("grammar")) {
                                    issues.add(ContentIssue(
                                        contentId = doc.id,
                                        contentTitle = title,
                                        currentClass = classId,
                                        currentSubject = subjectId,
                                        currentUnit = unitId,
                                        suggestedSubject = "english",
                                        issueType = "wrong_subject"
                                    ))
                                } else if (titleLower.contains("hindi")) {
                                    issues.add(ContentIssue(
                                        contentId = doc.id,
                                        contentTitle = title,
                                        currentClass = classId,
                                        currentSubject = subjectId,
                                        currentUnit = unitId,
                                        suggestedSubject = "hindi",
                                        issueType = "wrong_subject"
                                    ))
                                } else if (titleLower.contains("science") || titleLower.contains("physics") || 
                                          titleLower.contains("chemistry") || titleLower.contains("biology")) {
                                    issues.add(ContentIssue(
                                        contentId = doc.id,
                                        contentTitle = title,
                                        currentClass = classId,
                                        currentSubject = subjectId,
                                        currentUnit = unitId,
                                        suggestedSubject = "science",
                                        issueType = "wrong_subject"
                                    ))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Continue scanning
                    }
                    
                    // Check videos
                    try {
                        val videos = db.collection("classes").document(classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(unitId)
                            .collection("videos")
                            .get()
                            .await()
                        
                        for (doc in videos.documents) {
                            val title = doc.getString("title") ?: ""
                            val titleLower = title.lowercase()
                            
                            // Similar checks for videos
                            for (checkClass in 1..10) {
                                if (checkClass != classNum && 
                                    (titleLower.contains("class $checkClass") || 
                                     titleLower.contains("class$checkClass"))) {
                                    issues.add(ContentIssue(
                                        contentId = doc.id,
                                        contentTitle = title,
                                        currentClass = classId,
                                        currentSubject = subjectId,
                                        currentUnit = unitId,
                                        suggestedClass = "class_$checkClass",
                                        issueType = "wrong_class"
                                    ))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Continue scanning
                    }
                }
            }
        }
        
        return issues
    }
    
    /**
     * Move content to correct location
     */
    suspend fun moveContent(
        contentId: String,
        contentType: String, // "note" or "video"
        fromClass: String,
        fromSubject: String,
        fromUnit: String,
        toClass: String,
        toSubject: String,
        toUnit: String
    ): Boolean {
        return try {
            val collectionName = if (contentType == "note") "notes" else "videos"
            
            // Get the content
            val sourceDoc = db.collection("classes").document(fromClass)
                .collection("subjects").document(fromSubject)
                .collection("units").document(fromUnit)
                .collection(collectionName)
                .document(contentId)
                .get()
                .await()
            
            if (!sourceDoc.exists()) {
                return false
            }
            
            val data = sourceDoc.data ?: return false
            
            // Add to new location
            db.collection("classes").document(toClass)
                .collection("subjects").document(toSubject)
                .collection("units").document(toUnit)
                .collection(collectionName)
                .add(data)
                .await()
            
            // Delete from old location
            sourceDoc.reference.delete().await()
            
            true
        } catch (e: Exception) {
            android.util.Log.e("ContentValidator", "Error moving content", e)
            false
        }
    }
    
    /**
     * Delete duplicate content
     */
    suspend fun deleteDuplicate(
        contentId: String,
        contentType: String,
        classId: String,
        subjectId: String,
        unitId: String
    ): Boolean {
        return try {
            val collectionName = if (contentType == "note") "notes" else "videos"
            
            db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection(collectionName)
                .document(contentId)
                .delete()
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
}
