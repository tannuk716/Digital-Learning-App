package com.tannu.edureach.data.model

data class GameModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val gameType: String = "", // e.g., "monkey_jump", "lion_math", "parrot_spelling"
    val activityClass: String = "", // Full class name to launch
    val iconUrl: String = "",
    val classId: String = "",
    val subjectId: String = "",
    val unitId: String = "", // Unit 1-20
    val timestamp: Long = System.currentTimeMillis()
)

data class GameType(
    val id: String,
    val name: String,
    val description: String,
    val activityClass: String,
    val iconResId: Int = 0
)
