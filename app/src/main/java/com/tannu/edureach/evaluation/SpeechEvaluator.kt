package com.tannu.edureach.evaluation

import android.util.Log
import com.tannu.edureach.evaluation.models.ActivityType
import com.tannu.edureach.evaluation.models.EvaluationResult
import com.tannu.edureach.evaluation.models.Mistake
import com.tannu.edureach.evaluation.models.MistakeType
import com.tannu.edureach.evaluation.models.Severity
import com.tannu.edureach.evaluation.models.Suggestion
import com.tannu.edureach.tracking.ProgressTracker
import com.tannu.edureach.utils.GeminiApiHelper
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SpeechEvaluator(
    private val promptBuilder: PromptBuilder = PromptBuilder(),
    private val responseParser: ResponseParser = ResponseParser(),
    private val feedbackGenerator: FeedbackGenerator = FeedbackGenerator(),
    private val fallbackEvaluator: FallbackEvaluator = FallbackEvaluator(),
    private val progressTracker: ProgressTracker = ProgressTracker()
) {
    
    companion object {
        private const val TAG = "SpeechEvaluator"
        private const val API_TIMEOUT_MS = 10000L
    }
    
    suspend fun evaluateSpeech(
        question: String,
        studentResponse: String,
        difficultyLevel: Int,
        activityType: ActivityType
    ): EvaluationResult = withContext(Dispatchers.IO) {
        try {

            val commonMistakes = try {
                progressTracker.getCommonMistakes(activityType, 3)
                    .map { it.mistakeType }
            } catch (e: Exception) {
                Log.w(TAG, "Could not fetch common mistakes", e)
                emptyList()
            }
            

            val prompt = when (activityType) {
                ActivityType.DAILY_SPEAKING -> promptBuilder.buildDailySpeakingPrompt(
                    question, studentResponse, difficultyLevel, commonMistakes
                )
                ActivityType.PRONUNCIATION -> promptBuilder.buildPronunciationPrompt(
                    question, studentResponse
                )
                ActivityType.VOCABULARY -> {

                    return@withContext evaluateWithFallback(question, studentResponse, difficultyLevel)
                }
            }
            

            val aiResult = evaluateWithAI(prompt, API_TIMEOUT_MS)
            
            if (aiResult != null) {

                val parseResult = when (activityType) {
                    ActivityType.DAILY_SPEAKING -> responseParser.parseDailySpeakingResponse(aiResult)
                    ActivityType.PRONUNCIATION -> responseParser.parsePronunciationResponse(aiResult)
                    else -> Result.failure(Exception("Unsupported activity type"))
                }
                
                parseResult.onSuccess { evaluation ->

                    val result = when (activityType) {
                        ActivityType.DAILY_SPEAKING -> {
                            val dailyEval = evaluation as com.tannu.edureach.evaluation.models.DailySpeakingEvaluation
                            convertDailySpeakingEvaluation(dailyEval, difficultyLevel)
                        }
                        ActivityType.PRONUNCIATION -> {
                            val pronEval = evaluation as com.tannu.edureach.evaluation.models.PronunciationEvaluation
                            convertPronunciationEvaluation(pronEval)
                        }
                        else -> evaluateWithFallback(question, studentResponse, difficultyLevel)
                    }
                    

                    try {
                        progressTracker.saveMistakes(
                            result.mistakes,
                            result.score,
                            activityType,
                            question
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not save mistakes", e)
                    }
                    
                    return@withContext result
                }
                
                parseResult.onFailure { error ->
                    Log.e(TAG, "Failed to parse AI response", error)

                    val retryResult = retryWithSimplifiedPrompt(question, studentResponse, difficultyLevel)
                    if (retryResult != null) {
                        return@withContext retryResult
                    }
                }
            }
            

            Log.w(TAG, "Using fallback evaluation")
            evaluateWithFallback(question, studentResponse, difficultyLevel)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in evaluateSpeech", e)
            evaluateWithFallback(question, studentResponse, difficultyLevel)
        }
    }
    
    private suspend fun evaluateWithAI(
        prompt: String,
        timeout: Long = API_TIMEOUT_MS
    ): String? {
        return try {
            withTimeout(timeout) {
                val result = GeminiApiHelper.generateContent(prompt)
                result.onSuccess { response ->
                    Log.d(TAG, "AI evaluation successful")
                    return@withTimeout response
                }
                result.onFailure { error ->
                    Log.e(TAG, "AI evaluation failed", error)
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "AI evaluation timeout or error", e)
            null
        }
    }
    
    private suspend fun retryWithSimplifiedPrompt(
        question: String,
        studentResponse: String,
        difficultyLevel: Int
    ): EvaluationResult? {
        return try {
            Log.d(TAG, "Retrying with simplified prompt")
            val simplifiedPrompt = """
                Evaluate this student response briefly:
                Question: $question
                Response: $studentResponse
                
                Provide JSON with: score (0-100), mistakes array, suggestions array
            """.trimIndent()
            
            val aiResult = evaluateWithAI(simplifiedPrompt, 5000L)
            if (aiResult != null) {
                val parseResult = responseParser.parseDailySpeakingResponse(aiResult)
                parseResult.onSuccess { evaluation ->
                    return convertDailySpeakingEvaluation(evaluation, difficultyLevel)
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Retry failed", e)
            null
        }
    }
    
    private fun evaluateWithFallback(
        question: String,
        studentResponse: String,
        difficultyLevel: Int
    ): EvaluationResult {
        Log.d(TAG, "Using fallback evaluation")
        return fallbackEvaluator.evaluateSpeech(question, studentResponse, difficultyLevel)
    }
    
    private fun convertDailySpeakingEvaluation(
        evaluation: com.tannu.edureach.evaluation.models.DailySpeakingEvaluation,
        difficultyLevel: Int
    ): EvaluationResult {

        val mistakes = evaluation.mistakes.map { aiMistake ->
            Mistake(
                type = parseMistakeType(aiMistake.type),
                location = aiMistake.wrongText,
                description = "${aiMistake.explanation}. Try: ${aiMistake.correctText}",
                severity = Severity.MODERATE
            )
        }
        

        val suggestions = evaluation.suggestions.mapIndexed { index, suggestionText ->
            Suggestion(
                mistakeType = if (index < mistakes.size) mistakes[index].type else MistakeType.GRAMMAR_ERROR,
                suggestion = suggestionText,
                example = null
            )
        }
        

        val feedbackText = feedbackGenerator.generateDailySpeakingFeedback(evaluation, difficultyLevel)
        
        return EvaluationResult(
            score = evaluation.score,
            mistakes = mistakes,
            suggestions = suggestions,
            feedbackText = feedbackText,
            usedFallback = false
        )
    }
    
    private fun convertPronunciationEvaluation(
        evaluation: com.tannu.edureach.evaluation.models.PronunciationEvaluation
    ): EvaluationResult {

        val mistakes = evaluation.specificIssues.map { issue ->
            Mistake(
                type = MistakeType.PRONUNCIATION_ERROR,
                location = issue,
                description = issue,
                severity = Severity.MINOR
            )
        }
        
        val suggestions = listOf(
            Suggestion(
                mistakeType = MistakeType.PRONUNCIATION_ERROR,
                suggestion = evaluation.suggestion,
                example = evaluation.example
            )
        )
        
        val feedbackText = feedbackGenerator.generatePronunciationFeedback(evaluation)
        
        return EvaluationResult(
            score = evaluation.score,
            mistakes = mistakes,
            suggestions = suggestions,
            feedbackText = feedbackText,
            usedFallback = false
        )
    }
    
    private fun parseMistakeType(typeString: String): MistakeType {
        return when (typeString.lowercase()) {
            "wrong_word" -> MistakeType.WRONG_WORD
            "grammar_error" -> MistakeType.GRAMMAR_ERROR
            "incomplete_response" -> MistakeType.INCOMPLETE_RESPONSE
            "pronunciation_error" -> MistakeType.PRONUNCIATION_ERROR
            "tense_error" -> MistakeType.TENSE_ERROR
            "subject_verb_agreement" -> MistakeType.SUBJECT_VERB_AGREEMENT
            else -> MistakeType.GRAMMAR_ERROR
        }
    }
}