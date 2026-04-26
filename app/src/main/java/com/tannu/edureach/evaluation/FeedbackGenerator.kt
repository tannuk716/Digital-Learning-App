package com.tannu.edureach.evaluation

import android.graphics.Color
import com.tannu.edureach.evaluation.models.DailySpeakingEvaluation
import com.tannu.edureach.evaluation.models.Mistake
import com.tannu.edureach.evaluation.models.MistakeType
import com.tannu.edureach.evaluation.models.PronunciationEvaluation
import com.tannu.edureach.evaluation.models.Suggestion

class FeedbackGenerator {
    
    fun generateDailySpeakingFeedback(
        evaluation: DailySpeakingEvaluation,
        difficultyLevel: Int
    ): String {
        val levelName = getDifficultyLevelName(difficultyLevel)
        val scoreEmoji = getScoreEmoji(evaluation.score)
        
        val feedback = StringBuilder()
        feedback.append("$scoreEmoji ${getScoreMessage(evaluation.score)}\n\n")
        feedback.append("📊 Score: ${evaluation.score}%\n")
        feedback.append("🗣️ Pronunciation: ${evaluation.pronunciation}\n")
        feedback.append("💬 Fluency: ${evaluation.fluency}\n")
        feedback.append("📝 Grammar: ${evaluation.grammar}\n")
        feedback.append("✅ Completeness: ${evaluation.completeness}\n")
        feedback.append("🎯 Level: $levelName\n\n")
        
        if (evaluation.mistakes.isNotEmpty()) {
            feedback.append("📋 Areas to improve:\n")
            evaluation.mistakes.take(3).forEach { mistake ->
                feedback.append("• ${mistake.explanation}\n")
            }
            feedback.append("\n")
        }
        
        if (evaluation.suggestions.isNotEmpty()) {
            feedback.append("💡 Tips:\n")
            evaluation.suggestions.forEach { suggestion ->
                feedback.append("• $suggestion\n")
            }
        }
        
        return feedback.toString()
    }
    
    fun generatePronunciationFeedback(
        evaluation: PronunciationEvaluation
    ): String {
        val scoreEmoji = getScoreEmoji(evaluation.score)
        
        val feedback = StringBuilder()
        feedback.append("$scoreEmoji ${getScoreMessage(evaluation.score)}\n\n")
        feedback.append("📊 Score: ${evaluation.score}%\n")
        feedback.append("🎯 Accuracy: ${evaluation.accuracy}\n")
        feedback.append("🔊 Phonetic Match: ${evaluation.phoneticMatch}\n\n")
        
        if (evaluation.specificIssues.isNotEmpty()) {
            feedback.append("📋 Focus on:\n")
            evaluation.specificIssues.forEach { issue ->
                feedback.append("• $issue\n")
            }
            feedback.append("\n")
        }
        
        feedback.append("💡 Tip: ${evaluation.suggestion}\n")
        
        if (evaluation.example.isNotBlank()) {
            feedback.append("\n📖 Example: ${evaluation.example}")
        }
        
        return feedback.toString()
    }
    
    fun generateMistakeSummary(mistakes: List<Mistake>): String {
        if (mistakes.isEmpty()) {
            return "✅ Excellent! No mistakes found."
        }
        
        val grouped = mistakes.groupBy { it.type }
        val parts = grouped.map { (type, list) ->
            "${list.size} ${type.displayName}${if (list.size > 1) "s" else ""}"
        }
        
        return "Found: ${parts.joinToString(", ")}"
    }
    
    fun formatMistakeList(mistakes: List<Mistake>): String {
        if (mistakes.isEmpty()) return ""
        
        val formatted = StringBuilder()
        mistakes.forEachIndexed { index, mistake ->
            formatted.append("${index + 1}. ${mistake.type.displayName}\n")
            formatted.append("   ❌ Wrong: ${mistake.location}\n")
            formatted.append("   💡 ${mistake.description}\n")
            if (index < mistakes.size - 1) {
                formatted.append("\n")
            }
        }
        
        return formatted.toString()
    }
    
    fun getScoreColor(score: Int): Int {
        return when {
            score >= 70 -> Color.parseColor("#4CAF50")
            score >= 50 -> Color.parseColor("#FFC107")
            else -> Color.parseColor("#F44336")
        }
    }
    
    private fun getScoreEmoji(score: Int): String {
        return when {
            score >= 90 -> "🌟"
            score >= 70 -> "✅"
            score >= 50 -> "👍"
            else -> "💪"
        }
    }
    
    private fun getScoreMessage(score: Int): String {
        return when {
            score >= 90 -> "Excellent work!"
            score >= 70 -> "Great job!"
            score >= 50 -> "Good effort!"
            else -> "Keep practicing!"
        }
    }
    
    private fun getDifficultyLevelName(level: Int): String {
        return when (level) {
            1 -> "Basic Level"
            2 -> "Elementary Level"
            3 -> "Intermediate Level"
            4 -> "Advanced Level"
            else -> "Basic Level"
        }
    }
    
    private val MistakeType.displayName: String
        get() = when (this) {
            MistakeType.WRONG_WORD -> "Wrong Word"
            MistakeType.GRAMMAR_ERROR -> "Grammar Error"
            MistakeType.INCOMPLETE_RESPONSE -> "Incomplete Response"
            MistakeType.PRONUNCIATION_ERROR -> "Pronunciation Error"
            MistakeType.TENSE_ERROR -> "Tense Error"
            MistakeType.SUBJECT_VERB_AGREEMENT -> "Subject-Verb Agreement"
        }
}