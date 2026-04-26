package com.tannu.edureach.evaluation.models

data class DailySpeakingEvaluation(
    val score: Int,
    val mistakes: List<AIDetectedMistake>,
    val pronunciation: String,
    val fluency: String,
    val grammar: String,
    val completeness: String,
    val suggestions: List<String>
)

data class AIDetectedMistake(
    val type: String,
    val wrongText: String,
    val correctText: String,
    val explanation: String
)

data class PronunciationEvaluation(
    val score: Int,
    val accuracy: String,
    val phoneticMatch: String,
    val specificIssues: List<String>,
    val suggestion: String,
    val example: String
)