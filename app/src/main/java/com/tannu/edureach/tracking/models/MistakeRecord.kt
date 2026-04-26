package com.tannu.edureach.tracking.models

data class MistakeRecord(
    val timestamp: Long,
    val activityType: String,
    val mistakeType: String,
    val description: String,
    val score: Int,
    val questionText: String = ""
)

data class MistakePattern(
    val mistakeType: String,
    val frequency: Int,
    val lastOccurrence: Long,
    val isCommon: Boolean
)

data class ProgressData(
    val speakingScore: Int = 0,
    val pronunciationScore: Int = 0,
    val vocabularyLearned: Int = 0,
    val lastPracticeDate: Long = 0,
    val currentWeekLevel: Int = 1,
    val streakCount: Int = 0
)