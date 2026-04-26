package com.tannu.edureach.utils

import android.content.Context

object Class10ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass10Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1eY61reHKqGe9P5frxChuzNAQwctX3UC3"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1S-_IhtSg7W7NmJa0PcEwFi5GrzSG04tn"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1XPg_YUuFZpXcV9YOn2WCmy3ByY2286TG")
            )),
            SubjectContent("english", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1pMvLl4pE1Q0Yn9CkXIUNUyozJggUpGIO"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1RA24hWdUmBnkiGqWDYptN1BsSH6yAkBJ"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1jTgNQDnnqvNOw01u6V1qORtZxkh8xvqp")
            )),
            SubjectContent("maths", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1C3tyiuaavzVQfhFo3ogDeas8u7NXVRRc"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1XIG3nkbIA1MJv0HMf4Y_kUvFHCWDbKT4"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1aC0vp0CNKxKhI1SEQsrgraTKJKDHvv5O")
            )),
            SubjectContent("science", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("science")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1s4ChUt_SinwlTHdx9cy3f2fMX_QrCXLZ"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1PIKPQEFNGn1u-pmkE5cU0WY3fk4bVbHx"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1ch-_Z8ISF3TU5NLdqovdo9uTC6uFzUtz")
            )),
            SubjectContent("sst", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("sst")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1YH8PTk3UkCOeaka8dItfjIUFtV-dKKqC"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1YqiPeba_vF9dyLDGyvDGrr_FrRHkUTpp"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=13qKBxAfGz3-VxP06G5rVuifZdQBADYfc")
            ))
        )
    }

    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        android.util.Log.d("ContentProvider", "========================================")
        android.util.Log.d("ContentProvider", "Searching for subjectId: '$subjectId'")
        
        val allSubjects = getClass10Content(context)
        android.util.Log.d("ContentProvider", "Available subjects: ${allSubjects.map { "'${it.subjectId}'" }}")
        
        val found = allSubjects.find { it.subjectId.equals(subjectId, ignoreCase = true) }
        
        if (found != null) {
            android.util.Log.d("ContentProvider", "✅ Found subject: ${found.subjectName}")
            android.util.Log.d("ContentProvider", "Units count: ${found.units.size}")
            found.units.forEachIndexed { index, unit ->
                android.util.Log.d("ContentProvider", "  Unit ${index + 1}: ${unit.unitName} - URL: ${unit.pdfUrl.take(50)}...")
            }
        } else {
            android.util.Log.e("ContentProvider", "❌ Subject NOT FOUND for id: '$subjectId'")
        }
        
        android.util.Log.d("ContentProvider", "========================================")
        return found
    }
}