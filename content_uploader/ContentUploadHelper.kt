package com.tannu.edureach.content_uploader

import com.google.firebase.firestore.FirebaseFirestore
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.data.model.QuestionModel
import kotlinx.coroutines.tasks.await

/**
 * Helper class to upload educational content to Firebase
 * Organized by: Class → Subject → Unit → Content Type
 */
class ContentUploadHelper {
    
    private val db = FirebaseFirestore.getInstance()
    
    data class ContentItem(
        val classId: String,      // e.g., "class_1", "class_2"
        val subjectId: String,    // e.g., "english", "hindi", "maths", "science"
        val unitId: String,       // e.g., "unit_1", "unit_2"
        val type: ContentType,    // VIDEO, NOTE, or QUIZ
        val title: String,
        val description: String = "",
        val url: String = "",
        val isYoutube: Boolean = false,
        val questions: List<QuestionModel> = emptyList()
    )
    
    enum class ContentType {
        VIDEO, NOTE, QUIZ
    }
    
    /**
     * Upload a single content item
     */
    suspend fun uploadContent(item: ContentItem): Result<String> {
        return try {
            val docRef = when (item.type) {
                ContentType.VIDEO -> {
                    val video = VideoContent(
                        title = item.title,
                        description = item.description,
                        videoUrl = item.url,
                        isYoutube = item.isYoutube
                    )
                    db.collection("classes").document(item.classId)
                        .collection("subjects").document(item.subjectId)
                        .collection("units").document(item.unitId)
                        .collection("videos")
                        .add(video).await()
                }
                ContentType.NOTE -> {
                    val note = NoteContent(
                        title = item.title,
                        description = item.description,
                        fileUrl = item.url
                    )
                    db.collection("classes").document(item.classId)
                        .collection("subjects").document(item.subjectId)
                        .collection("units").document(item.unitId)
                        .collection("notes")
                        .add(note).await()
                }
                ContentType.QUIZ -> {
                    val quiz = QuizModel(
                        title = item.title,
                        questions = item.questions
                    )
                    db.collection("classes").document(item.classId)
                        .collection("subjects").document(item.subjectId)
                        .collection("units").document(item.unitId)
                        .collection("quizzes")
                        .add(quiz).await()
                }
            }
            
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Upload multiple content items in batch
     */
    suspend fun uploadBatch(items: List<ContentItem>): BatchUploadResult {
        val results = mutableListOf<UploadResult>()
        var successCount = 0
        var failureCount = 0
        
        items.forEach { item ->
            val result = uploadContent(item)
            if (result.isSuccess) {
                successCount++
                results.add(UploadResult(item, true, result.getOrNull()))
            } else {
                failureCount++
                results.add(UploadResult(item, false, null, result.exceptionOrNull()?.message))
            }
        }
        
        return BatchUploadResult(
            total = items.size,
            success = successCount,
            failed = failureCount,
            results = results
        )
    }
    
    /**
     * Remove duplicate content based on title and URL
     */
    suspend fun removeDuplicates(classId: String, subjectId: String, unitId: String): DuplicateRemovalResult {
        val duplicatesRemoved = mutableListOf<String>()
        
        try {
            // Check videos
            val videos = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("videos")
                .get().await()
            
            val videoMap = mutableMapOf<String, MutableList<String>>()
            videos.documents.forEach { doc ->
                val key = "${doc.getString("title")}_${doc.getString("videoUrl")}"
                videoMap.getOrPut(key) { mutableListOf() }.add(doc.id)
            }
            
            videoMap.values.forEach { ids ->
                if (ids.size > 1) {
                    // Keep first, delete rest
                    ids.drop(1).forEach { id ->
                        db.collection("classes").document(classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(unitId)
                            .collection("videos").document(id)
                            .delete().await()
                        duplicatesRemoved.add("Video: $id")
                    }
                }
            }
            
            // Check notes
            val notes = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("notes")
                .get().await()
            
            val noteMap = mutableMapOf<String, MutableList<String>>()
            notes.documents.forEach { doc ->
                val key = "${doc.getString("title")}_${doc.getString("fileUrl")}"
                noteMap.getOrPut(key) { mutableListOf() }.add(doc.id)
            }
            
            noteMap.values.forEach { ids ->
                if (ids.size > 1) {
                    ids.drop(1).forEach { id ->
                        db.collection("classes").document(classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(unitId)
                            .collection("notes").document(id)
                            .delete().await()
                        duplicatesRemoved.add("Note: $id")
                    }
                }
            }
            
            // Check quizzes
            val quizzes = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("quizzes")
                .get().await()
            
            val quizMap = mutableMapOf<String, MutableList<String>>()
            quizzes.documents.forEach { doc ->
                val key = doc.getString("title")
                quizMap.getOrPut(key ?: "") { mutableListOf() }.add(doc.id)
            }
            
            quizMap.values.forEach { ids ->
                if (ids.size > 1) {
                    ids.drop(1).forEach { id ->
                        db.collection("classes").document(classId)
                            .collection("subjects").document(subjectId)
                            .collection("units").document(unitId)
                            .collection("quizzes").document(id)
                            .delete().await()
                        duplicatesRemoved.add("Quiz: $id")
                    }
                }
            }
            
            return DuplicateRemovalResult(
                success = true,
                duplicatesRemoved = duplicatesRemoved.size,
                details = duplicatesRemoved
            )
        } catch (e: Exception) {
            return DuplicateRemovalResult(
                success = false,
                duplicatesRemoved = 0,
                details = emptyList(),
                error = e.message
            )
        }
    }
    
    data class UploadResult(
        val item: ContentItem,
        val success: Boolean,
        val documentId: String? = null,
        val error: String? = null
    )
    
    data class BatchUploadResult(
        val total: Int,
        val success: Int,
        val failed: Int,
        val results: List<UploadResult>
    )
    
    data class DuplicateRemovalResult(
        val success: Boolean,
        val duplicatesRemoved: Int,
        val details: List<String>,
        val error: String? = null
    )
}
