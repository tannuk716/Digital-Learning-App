package com.tannu.edureach.utils

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
        val issueType: String
    )
    
    

    suspend fun scanForIssues(): List<ContentIssue> {
        val issues = mutableListOf<ContentIssue>()
        

        for (classNum in 1..10) {
            val classId = "class_$classNum"
            

            val subjects = listOf("english", "hindi", "maths", "math", "science", "social_science")
            for (subjectId in subjects) {
                

                for (unitNum in 1..20) {
                    val unitId = "unit_$unitNum"
                    

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

                    }
                    

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

                    }
                }
            }
        }
        
        return issues
    }
    
    

    suspend fun moveContent(
        contentId: String,
        contentType: String,
        fromClass: String,
        fromSubject: String,
        fromUnit: String,
        toClass: String,
        toSubject: String,
        toUnit: String
    ): Boolean {
        return try {
            val collectionName = if (contentType == "note") "notes" else "videos"
            

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
            

            db.collection("classes").document(toClass)
                .collection("subjects").document(toSubject)
                .collection("units").document(toUnit)
                .collection(collectionName)
                .add(data)
                .await()
            

            sourceDoc.reference.delete().await()
            
            true
        } catch (e: Exception) {
            android.util.Log.e("ContentValidator", "Error moving content", e)
            false
        }
    }
    
    

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