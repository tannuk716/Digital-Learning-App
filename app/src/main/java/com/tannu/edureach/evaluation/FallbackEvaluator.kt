package com.tannu.edureach.evaluation

import com.tannu.edureach.evaluation.models.EvaluationResult
import com.tannu.edureach.evaluation.models.Mistake
import com.tannu.edureach.evaluation.models.MistakeType
import com.tannu.edureach.evaluation.models.Severity
import com.tannu.edureach.evaluation.models.Suggestion

class FallbackEvaluator {
    
    fun evaluateSpeech(
        question: String,
        studentResponse: String,
        difficultyLevel: Int
    ): EvaluationResult {
        val mistakes = detectBasicGrammarIssues(studentResponse)
        val wordCount = studentResponse.split(" ").filter { it.isNotBlank() }.size
        val expectedWordCount = getExpectedWordCount(difficultyLevel)
        
        val score = calculateBasicScore(studentResponse, difficultyLevel, mistakes.size)
        
        val suggestions = generateBasicSuggestions(mistakes, wordCount, expectedWordCount)
        
        val feedbackText = generateBasicFeedback(score, wordCount, difficultyLevel)
        
        return EvaluationResult(
            score = score,
            mistakes = mistakes,
            suggestions = suggestions,
            feedbackText = feedbackText,
            usedFallback = true
        )
    }
    
    fun detectBasicGrammarIssues(response: String): List<Mistake> {
        val mistakes = mutableListOf<Mistake>()
        

        val tenseMistakes = detectTenseErrors(response)
        mistakes.addAll(tenseMistakes)
        

        val agreementMistakes = detectSubjectVerbAgreement(response)
        mistakes.addAll(agreementMistakes)
        

        val incompleteMistakes = detectIncompleteSentences(response)
        mistakes.addAll(incompleteMistakes)
        
        return mistakes
    }
    
    private fun detectTenseErrors(response: String): List<Mistake> {
        val mistakes = mutableListOf<Mistake>()
        val words = response.lowercase().split(" ")
        

        if (words.contains("yesterday")) {
            val presentTenseVerbs = listOf("go", "eat", "play", "see", "do", "make", "take", "get")
            presentTenseVerbs.forEach { verb ->
                if (words.contains(verb)) {
                    mistakes.add(Mistake(
                        type = MistakeType.TENSE_ERROR,
                        location = verb,
                        description = "Use past tense with 'yesterday' (e.g., '$verb' should be '${getPastTense(verb)}')",
                        severity = Severity.MODERATE
                    ))
                }
            }
        }
        
        return mistakes
    }
    
    private fun detectSubjectVerbAgreement(response: String): List<Mistake> {
        val mistakes = mutableListOf<Mistake>()
        val patterns = listOf(
            Triple("he go", "he goes", "goes"),
            Triple("she go", "she goes", "goes"),
            Triple("it go", "it goes", "goes"),
            Triple("he do", "he does", "does"),
            Triple("she do", "she does", "does"),
            Triple("he have", "he has", "has"),
            Triple("she have", "she has", "has")
        )
        
        val lowerResponse = response.lowercase()
        patterns.forEach { (wrong, correct, correctVerb) ->
            if (lowerResponse.contains(wrong)) {
                mistakes.add(Mistake(
                    type = MistakeType.SUBJECT_VERB_AGREEMENT,
                    location = wrong,
                    description = "Should be '$correct' - use '$correctVerb' with he/she/it",
                    severity = Severity.MODERATE
                ))
            }
        }
        
        return mistakes
    }
    
    private fun detectIncompleteSentences(response: String): List<Mistake> {
        val mistakes = mutableListOf<Mistake>()
        val sentences = response.split(Regex("[.!?]"))
        
        sentences.forEach { sentence ->
            val words = sentence.trim().split(" ").filter { it.isNotBlank() }
            if (words.size in 1..2 && words.isNotEmpty()) {
                mistakes.add(Mistake(
                    type = MistakeType.INCOMPLETE_RESPONSE,
                    location = sentence.trim(),
                    description = "This sentence seems incomplete - try adding more details",
                    severity = Severity.MINOR
                ))
            }
        }
        
        return mistakes
    }
    
    fun checkMinimumWordCount(response: String, level: Int): Boolean {
        val wordCount = response.split(" ").filter { it.isNotBlank() }.size
        val minWords = when (level) {
            1 -> 5
            2 -> 10
            3 -> 20
            4 -> 40
            else -> 5
        }
        return wordCount >= minWords
    }
    
    fun calculateBasicScore(response: String, level: Int, mistakeCount: Int): Int {
        var baseScore = 100
        

        baseScore -= (mistakeCount * 10)
        

        val wordCount = response.split(" ").filter { it.isNotBlank() }.size
        val expectedWordCount = getExpectedWordCount(level)
        
        val completenessRatio = wordCount.toFloat() / expectedWordCount
        if (completenessRatio < 0.5) {
            baseScore -= 20
        } else if (completenessRatio < 0.75) {
            baseScore -= 10
        }
        

        return baseScore.coerceIn(0, 100)
    }
    
    private fun getExpectedWordCount(level: Int): Int {
        return when (level) {
            1 -> 7
            2 -> 15
            3 -> 30
            4 -> 50
            else -> 7
        }
    }
    
    private fun generateBasicSuggestions(
        mistakes: List<Mistake>,
        wordCount: Int,
        expectedWordCount: Int
    ): List<Suggestion> {
        val suggestions = mutableListOf<Suggestion>()
        

        mistakes.groupBy { it.type }.forEach { (type, mistakeList) ->
            val suggestion = when (type) {
                MistakeType.TENSE_ERROR -> Suggestion(
                    mistakeType = type,
                    suggestion = "Remember to use past tense when talking about things that already happened",
                    example = "Yesterday I went to school (not 'go')"
                )
                MistakeType.SUBJECT_VERB_AGREEMENT -> Suggestion(
                    mistakeType = type,
                    suggestion = "Use 's' or 'es' with he/she/it",
                    example = "He goes to school (not 'go')"
                )
                MistakeType.INCOMPLETE_RESPONSE -> Suggestion(
                    mistakeType = type,
                    suggestion = "Try to add more details to make complete sentences",
                    example = "Instead of 'I happy', say 'I am happy because...'"
                )
                else -> Suggestion(
                    mistakeType = type,
                    suggestion = "Keep practicing to improve!",
                    example = null
                )
            }
            suggestions.add(suggestion)
        }
        

        if (wordCount < expectedWordCount * 0.75) {
            suggestions.add(Suggestion(
                mistakeType = MistakeType.INCOMPLETE_RESPONSE,
                suggestion = "Try to speak more to fully answer the question",
                example = "Add more details about what, when, where, why, or how"
            ))
        }
        
        return suggestions
    }
    
    private fun generateBasicFeedback(score: Int, wordCount: Int, level: Int): String {
        val levelName = when (level) {
            1 -> "Basic"
            2 -> "Elementary"
            3 -> "Intermediate"
            4 -> "Advanced"
            else -> "Basic"
        }
        
        return when {
            score >= 90 -> "✅ Excellent work! Your response was clear and well-structured.\n\n📊 Score: $score%\n🗣️ Words: $wordCount\n🎯 Level: $levelName\n\n💡 Keep up the great work!"
            score >= 70 -> "✅ Good job! You're doing well.\n\n📊 Score: $score%\n🗣️ Words: $wordCount\n🎯 Level: $levelName\n\n💡 Review the suggestions below to improve further."
            score >= 50 -> "👍 Nice try! There's room for improvement.\n\n📊 Score: $score%\n🗣️ Words: $wordCount\n🎯 Level: $levelName\n\n💡 Focus on the suggestions below to get better."
            else -> "💪 Keep practicing! Everyone improves with practice.\n\n📊 Score: $score%\n🗣️ Words: $wordCount\n🎯 Level: $levelName\n\n💡 Review the suggestions and try again."
        }
    }
    
    private fun getPastTense(verb: String): String {
        return when (verb) {
            "go" -> "went"
            "eat" -> "ate"
            "play" -> "played"
            "see" -> "saw"
            "do" -> "did"
            "make" -> "made"
            "take" -> "took"
            "get" -> "got"
            else -> "${verb}ed"
        }
    }
}