package com.tannu.edureach.utils

import java.io.File

class FileDiscoveryEngineImpl(private val projectRoot: String = ".") : FileDiscoveryEngine {
    
    private val targetDirectories = listOf(
        "app/src/main/java/",
        "app/src/test/java/",
        "app/src/androidTest/java/"
    )
    
    override fun discoverKotlinFiles(): List<String> {
        val discoveredFiles = mutableListOf<String>()
        

        targetDirectories.forEach { dir ->
            val directory = File(projectRoot, dir)
            if (directory.exists() && directory.isDirectory) {
                discoveredFiles.addAll(scanDirectory(directory))
            }
        }
        

        val rootDir = File(projectRoot)
        if (rootDir.exists() && rootDir.isDirectory) {
            rootDir.listFiles()?.forEach { file ->
                if (file.isFile && file.name.matches(Regex("verify_.*\\.kt"))) {
                    discoveredFiles.add(file.path)
                }
            }
        }
        

        return discoveredFiles.sorted()
    }
    
    override fun isTargetFile(path: String): Boolean {
        val file = File(path)
        

        if (!file.name.endsWith(".kt")) {
            return false
        }
        

        val normalizedPath = file.path.replace("\\", "/")
        val isInTargetDir = targetDirectories.any { dir ->
            normalizedPath.contains(dir)
        }
        

        val isRootVerifyFile = file.parent == projectRoot && 
                               file.name.matches(Regex("verify_.*\\.kt"))
        
        return isInTargetDir || isRootVerifyFile
    }
    
    private fun scanDirectory(directory: File): List<String> {
        val files = mutableListOf<String>()
        
        directory.listFiles()?.forEach { file ->
            when {
                file.isDirectory -> {

                    files.addAll(scanDirectory(file))
                }
                file.isFile && file.name.endsWith(".kt") -> {

                    files.add(file.path)
                }
            }
        }
        
        return files
    }
}