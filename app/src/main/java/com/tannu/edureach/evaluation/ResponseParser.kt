package com.tannu.edureach.evaluation

import android.util.Log
import com.tannu.edureach.evaluation.models.AIDetectedMistake
import com.tannu.edureach.evaluation.models.DailySpeakingEvaluation
import com.tannu.edureach.evaluation.models.PronunciationEvaluation
import org.json.JSONArray
import org.json.JSONObject

class ResponseParser {
    
    companion object {
        private const val TAG = "ResponseParser"
    }
    
    fun parseDailySpeakingResponse(aiResponse: String): Result<DailySpeakingEvaluation> {
        return try {
            val jsonStr = extractJSON(aiResponse)
            if (jsonStr == null) {
                return Result.failure(Exception("No JSON found in response"))
            }
            
            val json = JSONObject(jsonStr)
            

            val requiredFields = listOf("score", "mistakes", "suggestions")
            if (!validateJSONStructure(json, requiredFields)) {
                return Result.failure(Exception("Missing required fields"))
            }
            

            val score = json.optInt("score", 70).coerceIn(0, 100)
            

            val mistakesArray = json.optJSONArray("mistakes") ?: JSONArray()
            val mistakes = mutableListOf<AIDetectedMistake>()
            
            for (i in 0 until mistakesArray.length()) {
                val mistakeObj = mistakesArray.getJSONObject(i)
                mistakes.add(AIDetectedMistake(
                    type = mistakeObj.optString("type", "unknown"),
                    wrongText = mistakeObj.optString("wrongText", ""),
                    correctText = mistakeObj.optString("correctText", ""),
                    explanation = mistakeObj.optString("explanation", "")
                ))
            }
            

            val pronunciation = json.optString("pronunciation", "Good")
            val fluency = json.optString("fluency", "Medium")
            val grammar = json.optString("grammar", "Minor Errors")
            val completeness = json.optString("completeness", "Complete")
            

            val suggestionsArray = json.optJSONArray("suggestions") ?: JSONArray()
            val suggestions = mutableListOf<String>()
            for (i in 0 until suggestionsArray.length()) {
                suggestions.add(suggestionsArray.getString(i))
            }
            
            Result.success(DailySpeakingEvaluation(
                score = score,
                mistakes = mistakes,
                pronunciation = pronunciation,
                fluency = fluency,
                grammar = grammar,
                completeness = completeness,
                suggestions = suggestions
            ))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response", e)
            Result.failure(e)
        }
    }
    
    fun parsePronunciationResponse(aiResponse: String): Result<PronunciationEvaluation> {
        return try {
            val jsonStr = extractJSON(aiResponse)
            if (jsonStr == null) {
                return Result.failure(Exception("No JSON found in response"))
            }
            
            val json = JSONObject(jsonStr)
            

            val requiredFields = listOf("score", "accuracy", "suggestion")
            if (!validateJSONStructure(json, requiredFields)) {
                return Result.failure(Exception("Missing required fields"))
            }
            

            val score = json.optInt("score", 70).coerceIn(0, 100)
            

            val accuracy = json.optString("accuracy", "Good")
            val phoneticMatch = json.optString("phoneticMatch", "Medium")
            val suggestion = json.optString("suggestion", "Keep practicing!")
            val example = json.optString("example", "")
            

            val issuesArray = json.optJSONArray("specificIssues") ?: JSONArray()
            val specificIssues = mutableListOf<String>()
            for (i in 0 until issuesArray.length()) {
                specificIssues.add(issuesArray.getString(i))
            }
            
            Result.success(PronunciationEvaluation(
                score = score,
                accuracy = accuracy,
                phoneticMatch = phoneticMatch,
                specificIssues = specificIssues,
                suggestion = suggestion,
                example = example
            ))
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing pronunciation response", e)
            Result.failure(e)
        }
    }
    
    private fun extractJSON(response: String): String? {
        val jsonStart = response.indexOf("{")
        val jsonEnd = response.lastIndexOf("}") + 1
        
        return if (jsonStart >= 0 && jsonEnd > jsonStart) {
            response.substring(jsonStart, jsonEnd)
        } else {
            null
        }
    }
    
    private fun validateJSONStructure(json: JSONObject, requiredFields: List<String>): Boolean {
        return requiredFields.all { json.has(it) }
    }
}