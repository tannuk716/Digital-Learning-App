package com.tannu.edureach.evaluation.models

data class EvaluationResult(
    val score: Int,
    val mistakes: List<Mistake>,
    val suggestions: List<Suggestion>,
    val feedbackText: String,
    val usedFallback: Boolean = false
)

data class Mistake(
    val type: MistakeType,
    val location: String,
    val description: String,
    val severity: Severity
)

enum class MistakeType {
    WRONG_WORD,
    GRAMMAR_ERROR,
    INCOMPLETE_RESPONSE,
    PRONUNCIATION_ERROR,
    TENSE_ERROR,
    SUBJECT_VERB_AGREEMENT
}

enum class Severity {
    MINOR,
    MODERATE,
    MAJOR
}

data class Suggestion(
    val mistakeType: MistakeType,
    val suggestion: String,
    val example: String?
)

enum class ActivityType {
    DAILY_SPEAKING,
    PRONUNCIATION,
    VOCABULARY
}