package com.tannu.edureach.data.model

import com.google.firebase.firestore.PropertyName

data class NoteContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val fileUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class VideoContent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val videoUrl: String = "",
    @get:PropertyName("youtube") @set:PropertyName("youtube")
    var isYoutube: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuizModel(
    val id: String = "",
    val title: String = "",
    val questions: List<QuestionModel> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class RecentUploadModel(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val classId: String = "",
    val subjectId: String = "",
    val unitId: String = "",
    val url: String = "",
    @get:PropertyName("youtube") @set:PropertyName("youtube")
    var isYoutube: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class QuestionModel(
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val explanation: String = ""
)

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "",
    val avatarId: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class OfflineData(
    val classes: List<OfflineClass> = emptyList()
)

data class OfflineClass(
    val id: Int = 0,
    val name: String = "",
    val subjects: List<Subject> = emptyList()
)

data class Subject(
    val name: String = "",
    val icon: String = "",
    val units: List<com.tannu.edureach.data.OfflineDataLoader.ContentUnit> = emptyList()
)