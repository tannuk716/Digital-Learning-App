package com.tannu.edureach.data.repository

import com.tannu.edureach.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ContentRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getNotes(classId: String, subjectId: String, unitId: String): Flow<List<NoteContent>> = callbackFlow {
        val collectionName = "notes"
        val ref = db.collection("classes").document(classId)
            .collection("subjects").document(subjectId)
            .collection("units").document(unitId)
            .collection(collectionName)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val notes = snapshot.documents.mapNotNull { it.toObject(NoteContent::class.java)?.copy(id = it.id) }
                trySend(notes).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    fun getVideos(classId: String, subjectId: String, unitId: String): Flow<List<VideoContent>> = callbackFlow {
        val ref = db.collection("classes").document(classId)
            .collection("subjects").document(subjectId)
            .collection("units").document(unitId)
            .collection("videos")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val videos = snapshot.documents.mapNotNull { it.toObject(VideoContent::class.java)?.copy(id = it.id) }
                trySend(videos).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    fun getQuizzes(classId: String, subjectId: String, unitId: String): Flow<List<QuizModel>> = callbackFlow {
        val collectionName = "quizzes"
        val ref = db.collection("classes").document(classId)
            .collection("subjects").document(subjectId)
            .collection("units").document(unitId)
            .collection(collectionName)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val quizzes = snapshot.documents.mapNotNull { it.toObject(QuizModel::class.java)?.copy(id = it.id) }
                trySend(quizzes).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addRecentUpload(title: String, type: String, classId: String, subjectId: String, unitId: String, url: String = "", isYoutube: Boolean = false) {
        try {
            val recent = RecentUploadModel(title = title, type = type, classId = classId, subjectId = subjectId, unitId = unitId, url = url, isYoutube = isYoutube)
            db.collection("recent_uploads").add(recent).await()
        } catch (e: Exception) {

        }
    }

    suspend fun uploadNote(classId: String, subjectId: String, unitId: String, note: NoteContent): Boolean {
        return try {
            val collectionName = "notes"
            val collectionRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection(collectionName)
            

            val existingDocs = collectionRef
                .whereEqualTo("title", note.title)
                .whereEqualTo("fileUrl", note.fileUrl)
                .get()
                .await()
            
            if (!existingDocs.isEmpty) {

                return false
            }
            

            collectionRef.add(note).await()
            addRecentUpload(note.title, "Note", classId, subjectId, unitId, note.fileUrl, false)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun uploadVideo(classId: String, subjectId: String, unitId: String, video: VideoContent): Boolean {
        return try {
            val collectionRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection("videos")
            

            val existingDocs = collectionRef
                .whereEqualTo("title", video.title)
                .whereEqualTo("videoUrl", video.videoUrl)
                .get()
                .await()
            
            if (!existingDocs.isEmpty) {

                return false
            }
            

            collectionRef.add(video).await()
            addRecentUpload(video.title, "Video", classId, subjectId, unitId, video.videoUrl, video.isYoutube)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun uploadQuiz(classId: String, subjectId: String, unitId: String, quiz: QuizModel): Boolean {
        return try {
            val collectionName = "quizzes"
            val collectionRef = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection(collectionName)
            

            val existingDocs = collectionRef
                .whereEqualTo("title", quiz.title)
                .get()
                .await()
            
            if (!existingDocs.isEmpty) {

                return false
            }
            

            collectionRef.add(quiz).await()
            addRecentUpload(quiz.title, "Quiz", classId, subjectId, unitId)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getRecentUploads(): Flow<List<RecentUploadModel>> = callbackFlow {
        val ref = db.collection("recent_uploads")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
        
        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val recents = snapshot.documents.mapNotNull { it.toObject(RecentUploadModel::class.java)?.copy(id = it.id) }
                trySend(recents).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    fun getRecentUploadsByClass(classId: String): Flow<List<RecentUploadModel>> = callbackFlow {

        val ref = db.collection("recent_uploads")
                    .whereEqualTo("classId", classId)
        
        val listener = ref.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val recents = snapshot.documents.mapNotNull { it.toObject(RecentUploadModel::class.java)?.copy(id = it.id) }

                val sortedRecents = recents.sortedByDescending { it.timestamp }
                trySend(sortedRecents).isSuccess
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getQuizById(classId: String, subjectId: String, unitId: String, quizId: String): QuizModel? {
        return try {
            val collectionName = "quizzes"
            val doc = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("units").document(unitId)
                .collection(collectionName).document(quizId)
                .get().await()
            doc.toObject(QuizModel::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun checkDuplicateGame(classId: String, subjectId: String, title: String, url: String): Boolean {
        return try {
            val existingDocs = db.collection("classes").document(classId)
                .collection("subjects").document(subjectId)
                .collection("games")
                .whereEqualTo("title", title)
                .whereEqualTo("activityClass", url)
                .get()
                .await()
            
            !existingDocs.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}