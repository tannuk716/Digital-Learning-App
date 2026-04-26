package com.tannu.edureach.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoggerImpl : Logger {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    private var successCount = 0
    private var failureCount = 0
    
    override fun log(level: LogLevel, message: String, context: Map<String, String>) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val contextStr = if (context.isNotEmpty()) {
            context.entries.joinToString(", ", prefix = " [", postfix = "]") { "${it.key}: ${it.value}" }
        } else {
            ""
        }
        println("[$timestamp] [${level.name}]$contextStr $message")
    }
    
    override fun logFileStart(filePath: String) {
        log(LogLevel.INFO, "Processing file: $filePath", mapOf("file" to filePath))
    }
    
    override fun logFileComplete(filePath: String, success: Boolean) {
        if (success) {
            successCount++
            log(LogLevel.INFO, "Successfully processed file: $filePath", mapOf("file" to filePath, "status" to "success"))
        } else {
            failureCount++
            log(LogLevel.ERROR, "Failed to process file: $filePath", mapOf("file" to filePath, "status" to "failure"))
        }
    }
    
    override fun generateSummary(totalFiles: Int, successCount: Int, failureCount: Int): String {
        val summary = buildString {
            appendLine()
            appendLine("=" .repeat(60))
            appendLine("COMMENT REMOVAL SUMMARY")
            appendLine("=" .repeat(60))
            appendLine("Total files discovered: $totalFiles")
            appendLine("Successfully processed: $successCount")
            appendLine("Failed to process: $failureCount")
            appendLine("Success rate: ${if (totalFiles > 0) String.format("%.1f%%", (successCount.toDouble() / totalFiles) * 100) else "N/A"}")
            appendLine("=" .repeat(60))
        }
        return summary.toString()
    }
}