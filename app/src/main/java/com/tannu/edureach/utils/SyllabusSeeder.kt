package com.tannu.edureach.utils

import android.content.Context
import android.widget.Toast
import com.tannu.edureach.data.model.NoteContent
import com.tannu.edureach.data.model.VideoContent
import com.tannu.edureach.data.repository.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SyllabusSeeder {
    fun seedClass1(context: Context) {
        val repo = ContentRepository()
        
        Toast.makeText(context, "Seeding Capstone Syllabus to Firestore...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {

            val englishChapters = listOf(
                "A Happy Child", "Three Little Pigs", "After a Bath", "The Bubble, the Straw and the Shoe",
                "One Little Kitten", "Lalu and Peelu", "Once I Saw a Little Bird", "Mittu and the Yellow Mango",
                "Merry-Go-Round", "Circle"
            )
            for ((index, chapter) in englishChapters.withIndex()) {
                val unitId = "unit_${index + 1}"
                repo.uploadNote("class_1", "english", unitId, NoteContent(title = "Chapter ${index + 1}: $chapter", description = "Reading material for $chapter", fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
                repo.uploadVideo("class_1", "english", unitId, VideoContent(title = "$chapter Story Video", description = "Watch the story of $chapter.", videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ", isYoutube = true))
            }

            val mathsChapters = listOf(
                "Shapes and Space", "Numbers from One to Nine", "Addition", "Subtraction",
                "Numbers from Ten to Twenty", "Time", "Measurement", "Numbers from Twenty-One to Fifty",
                "Data Handling", "Patterns", "Numbers", "Money"
            )
            for ((index, chapter) in mathsChapters.withIndex()) {
                val unitId = "unit_${index + 1}"
                repo.uploadNote("class_1", "maths", unitId, NoteContent(title = "Chapter ${index + 1}: $chapter", description = "Worksheets for $chapter", fileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"))
            }

            val hindiChapters = listOf(
                "Jhoola", "Mithai", "Teen Saathi", "Wah, Mere Ghode!",
                "Khatre Mein Saanp", "Aalu Ki Sadak", "Jhoom-Jholi", "Mela"
            )
            val hindiIndexMap = listOf(1, 5, 6, 7, 8, 9, 10, 11)
            for ((i, chapter) in hindiChapters.withIndex()) {
                val realChapterNumber = hindiIndexMap[i]
                val unitId = "unit_$realChapterNumber"
                repo.uploadVideo("class_1", "hindi", unitId, VideoContent(title = "$chapter (Chapter $realChapterNumber)", description = "Explanation for $chapter", videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ", isYoutube = true))
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Syllabus Seeding Completed! 🎉", Toast.LENGTH_LONG).show()
            }
        }
    }
}