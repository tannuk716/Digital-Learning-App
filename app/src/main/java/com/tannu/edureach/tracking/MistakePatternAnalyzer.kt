package com.tannu.edureach.tracking

import com.tannu.edureach.tracking.models.MistakePattern
import com.tannu.edureach.tracking.models.MistakeRecord

class MistakePatternAnalyzer {
    
    suspend fun analyzePatterns(
        mistakeHistory: List<MistakeRecord>
    ): List<MistakePattern> {

        val grouped = mistakeHistory.groupBy { it.mistakeType }
        
        return grouped.map { (type, records) ->
            val frequency = records.size
            val lastOccurrence = records.maxOfOrNull { it.timestamp } ?: 0L
            val isCommon = frequency > 3
            
            MistakePattern(
                mistakeType = type,
                frequency = frequency,
                lastOccurrence = lastOccurrence,
                isCommon = isCommon
            )
        }.sortedByDescending { it.frequency }
    }
    
    fun detectImprovement(
        currentPatterns: List<MistakePattern>,
        previousPatterns: List<MistakePattern>
    ): Map<String, Int> {
        val improvements = mutableMapOf<String, Int>()
        
        currentPatterns.forEach { current ->
            val previous = previousPatterns.find { it.mistakeType == current.mistakeType }
            if (previous != null) {
                val change = previous.frequency - current.frequency
                if (change > 0) {
                    improvements[current.mistakeType] = change
                }
            }
        }
        
        return improvements
    }
    
    fun getTopMistakes(patterns: List<MistakePattern>, limit: Int = 3): List<MistakePattern> {
        return patterns
            .filter { it.isCommon }
            .sortedByDescending { it.frequency }
            .take(limit)
    }
    
    fun getMistakeTypeDisplayName(mistakeType: String): String {
        return mistakeType
            .split("_")
            .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
    }
}