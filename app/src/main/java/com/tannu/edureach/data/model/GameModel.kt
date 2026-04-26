package com.tannu.edureach.data.model

data class GameModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val gameType: String = "",
    val activityClass: String = "",
    val iconUrl: String = "",
    val classId: String = "",
    val subjectId: String = "",
    val unitId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class GameType(
    val id: String,
    val name: String,
    val description: String,
    val activityClass: String,
    val iconResId: Int = 0
)