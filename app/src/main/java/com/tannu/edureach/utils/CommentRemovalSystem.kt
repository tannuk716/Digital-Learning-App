package com.tannu.edureach.utils

enum class TokenType {
    CODE,
    SINGLE_LINE_COMMENT,
    MULTI_LINE_COMMENT,
    KDOC_COMMENT,
    STRING_LITERAL,
    RAW_STRING_LITERAL
}

data class Token(
    val type: TokenType,
    val content: String,
    val startIndex: Int,
    val endIndex: Int
)

enum class LineEndingStyle {
    LF,
    CRLF
}

data class CompilationError(
    val file: String,
    val line: Int,
    val column: Int,
    val message: String
)

data class CompilationResult(
    val success: Boolean,
    val errors: List<CompilationError> = emptyList(),
    val output: String = ""
)

data class ProcessingResult(
    val filePath: String,
    val success: Boolean,
    val originalSize: Int,
    val newSize: Int,
    val commentsRemoved: Int,
    val error: String? = null
)

data class SystemReport(
    val totalFiles: Int,
    val processedFiles: Int,
    val failedFiles: Int,
    val totalCommentsRemoved: Int,
    val compilationSuccess: Boolean,
    val compilationErrors: List<CompilationError> = emptyList(),
    val processingResults: List<ProcessingResult> = emptyList()
)

interface FileDiscoveryEngine {
    fun discoverKotlinFiles(): List<String>
    fun isTargetFile(path: String): Boolean
}

interface CommentParser {
    fun parse(source: String): List<Token>
}

interface CommentRemover {
    fun removeComments(tokens: List<Token>): String
    fun preserveWhitespace(original: String, cleaned: String): String
}

interface FileWriter {
    fun writeFile(path: String, content: String, preserveLineEndings: Boolean = true)
    fun detectLineEndings(content: String): LineEndingStyle
}

interface CompilationVerifier {
    fun verifyCompilation(): CompilationResult
    fun parseErrors(buildOutput: String): List<CompilationError>
}

enum class LogLevel {
    INFO,
    WARNING,
    ERROR
}

interface Logger {
    fun log(level: LogLevel, message: String, context: Map<String, String> = emptyMap())
    fun logFileStart(filePath: String)
    fun logFileComplete(filePath: String, success: Boolean)
    fun generateSummary(totalFiles: Int, successCount: Int, failureCount: Int): String
}