package com.tannu.edureach.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermission

class FileWriterImpl : FileWriter {
    
    override fun writeFile(path: String, content: String, preserveLineEndings: Boolean) {
        val targetFile = File(path)
        
        if (!targetFile.exists()) {
            throw IllegalArgumentException("Target file does not exist: $path")
        }
        
        val originalPermissions = getFilePermissions(targetFile)
        
        val finalContent = if (preserveLineEndings) {
            val originalContent = targetFile.readText(Charsets.UTF_8)
            val lineEnding = detectLineEndings(originalContent)
            normalizeLineEndings(content, lineEnding)
        } else {
            content
        }
        
        writeAtomic(targetFile, finalContent, originalPermissions)
    }
    
    override fun detectLineEndings(content: String): LineEndingStyle {
        return if (content.contains("\r\n")) {
            LineEndingStyle.CRLF
        } else {
            LineEndingStyle.LF
        }
    }
    
    private fun normalizeLineEndings(content: String, style: LineEndingStyle): String {
        val normalized = content.replace("\r\n", "\n")
        
        return when (style) {
            LineEndingStyle.CRLF -> normalized.replace("\n", "\r\n")
            LineEndingStyle.LF -> normalized
        }
    }
    
    private fun writeAtomic(targetFile: File, content: String, permissions: Set<PosixFilePermission>?) {
        val tempFile = File.createTempFile(
            "filewriter_",
            ".tmp",
            targetFile.parentFile
        )
        
        try {
            tempFile.writeText(content, Charsets.UTF_8)
            
            val targetPath = targetFile.toPath()
            val tempPath = tempFile.toPath()
            
            Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            
            if (permissions != null) {
                try {
                    Files.setPosixFilePermissions(targetPath, permissions)
                } catch (e: UnsupportedOperationException) {
                }
            }
            
            validateWriteSuccess(targetFile, content)
            
        } catch (e: Exception) {
            tempFile.delete()
            throw e
        }
    }
    
    private fun getFilePermissions(file: File): Set<PosixFilePermission>? {
        return try {
            Files.getPosixFilePermissions(file.toPath())
        } catch (e: UnsupportedOperationException) {
            null
        }
    }
    
    private fun validateWriteSuccess(file: File, expectedContent: String) {
        if (!file.exists()) {
            throw IllegalStateException("File write validation failed: file does not exist after write")
        }
        
        val actualContent = file.readText(Charsets.UTF_8)
        if (actualContent != expectedContent) {
            throw IllegalStateException("File write validation failed: content mismatch")
        }
    }
}