package com.tannu.edureach.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.QuizModel
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.repository.ContentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

data class UnifiedContent(
    val id: String,
    val title: String,
    val type: String,
    val urlOrData: String,
    val timestamp: Long
)

@OptIn(ExperimentalCoroutinesApi::class)
class StudentViewModel : ViewModel() {
    private val repository = ContentRepository()

    private val _classId = MutableStateFlow("class_1")
    private val _subjectId = MutableStateFlow("english")
    private val _unitId = MutableStateFlow("unit_1")

    fun updateFilters(classId: String, subjectId: String, unitId: String) {
        _classId.value = classId
        _subjectId.value = subjectId
        _unitId.value = unitId
    }
    

    val contentList: Flow<List<UnifiedContent>> = combine(
        _classId, _subjectId, _unitId
    ) { cId, sId, uId ->
        FilterParams(cId, sId, uId)
    }.flatMapLatest { params ->
        combine(
            repository.getNotes(params.classId, params.subjectId, params.unitId).catch { emit(emptyList()) },
            repository.getVideos(params.classId, params.subjectId, params.unitId).catch { emit(emptyList()) },
            repository.getQuizzes(params.classId, params.subjectId, params.unitId).catch { emit(emptyList()) }
        ) { notes, videos, quizzes ->
            val list = mutableListOf<UnifiedContent>()
            list.addAll(notes.map { UnifiedContent(it.id, it.title, "Note", it.fileUrl, it.timestamp) })
            list.addAll(videos.map { UnifiedContent(it.id, it.title, "Video", it.videoUrl, it.timestamp) })
            list.addAll(quizzes.map { UnifiedContent(it.id, it.title, "Quiz", it.id, it.timestamp) })
            list.sortedByDescending { it.timestamp }
        }
    }

    private data class FilterParams(val classId: String, val subjectId: String, val unitId: String)
}