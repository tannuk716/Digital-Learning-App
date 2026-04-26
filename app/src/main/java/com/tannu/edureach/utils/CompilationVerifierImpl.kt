package com.tannu.edureach.utils

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class CompilationVerifierImpl(
    private val projectRoot: String = System.getProperty("user.dir") ?: "."
) : CompilationVerifier {
    
    override fun verifyCompilation(): CompilationResult {
        return try {
            val gradleCommand = if (System.getProperty("os.name").lowercase().contains("windows")) {
                "gradlew.bat"
            } else {
                "./gradlew"
            }
            
            val processBuilder = ProcessBuilder(gradleCommand, "compileDebugKotlin")
            processBuilder.directory(File(projectRoot))
            processBuilder.redirectErrorStream(true)
            
            val process = processBuilder.start()
            
            val output = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }
            }
            
            val exitCode = process.waitFor()
            val outputString = output.toString()
            
            if (exitCode == 0) {
                CompilationResult(
                    success = true,
                    errors = emptyList(),
                    output = outputString
                )
            } else {
                val errors = parseErrors(outputString)
                CompilationResult(
                    success = false,
                    errors = errors,
                    output = outputString
                )
            }
        } catch (e: Exception) {
            CompilationResult(
                success = false,
                errors = listOf(
                    CompilationError(
                        file = "",
                        line = 0,
                        column = 0,
                        message = "Failed to execute Gradle build: ${e.message}"
                    )
                ),
                output = "Exception: ${e.message}"
            )
        }
    }
    
    override fun parseErrors(buildOutput: String): List<CompilationError> {
        val errors = mutableListOf<CompilationError>()
        val lines = buildOutput.split("\n")
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            
            val errorMatch = Regex("""^e: file://(.+?):(\d+):(\d+):\s*(.+)$""").find(line)
            if (errorMatch != null) {
                val (filePath, lineNum, colNum, message) = errorMatch.destructured
                
                val fullMessage = buildString {
                    append(message.trim())
                    
                    var j = i + 1
                    while (j < lines.size) {
                        val nextLine = lines[j].trim()
                        if (nextLine.isEmpty() || 
                            nextLine.startsWith("e: file://") ||
                            nextLine.startsWith("> Task") ||
                            nextLine.startsWith("FAILURE:") ||
                            nextLine.startsWith("*")) {
                            break
                        }
                        append(" ")
                        append(nextLine)
                        j++
                    }
                }
                
                errors.add(
                    CompilationError(
                        file = filePath,
                        line = lineNum.toIntOrNull() ?: 0,
                        column = colNum.toIntOrNull() ?: 0,
                        message = fullMessage
                    )
                )
            }
            
            i++
        }
        
        return errors
    }
}