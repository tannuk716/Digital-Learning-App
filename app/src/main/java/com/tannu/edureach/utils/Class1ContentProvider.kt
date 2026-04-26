package com.tannu.edureach.utils

import android.content.Context

object Class1ContentProvider {
    
    data class UnitContent(
        val unitNumber: Int,
        val unitName: String,
        val pdfUrl: String
    )
    
    data class SubjectContent(
        val subjectId: String,
        val subjectName: String,
        val units: List<UnitContent>
    )
    
    fun getClass1Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent(
                subjectId = "english",
                subjectName = context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")),
                units = listOf(
                    UnitContent(1, "Unit 1", "https://drive.google.com/uc?export=download&id=17-3ohSQHGG1JQA72SiOGcASGET0qFS86"),
                    UnitContent(2, "Unit 2", "https://drive.google.com/uc?export=download&id=1vQDGA4EoP-32BM8WOt5lEQN69eEURHEs"),
                    UnitContent(3, "Unit 3", "https://drive.google.com/uc?export=download&id=1WgsNVtdL1LMqR1Hr8PuENA7oIGO_Gtvv"),
                    UnitContent(4, "Unit 4", "https://drive.google.com/uc?export=download&id=1hupuJLZzuBPWjlAoVI7IGxAlzPxnulG0")
                )
            ),
            SubjectContent(
                subjectId = "hindi",
                subjectName = context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")),
                units = listOf(
                    UnitContent(1, "Unit 1", "https://drive.google.com/uc?export=download&id=1sqXW7vQXrsS1_a8er3HHFNVUU1T_il12"),
                    UnitContent(2, "Unit 2", "https://drive.google.com/uc?export=download&id=1DmHwqSNS-uRfSyprl-OWEEUe4B-NXjm6"),
                    UnitContent(3, "Unit 3", "https://drive.google.com/uc?export=download&id=16RqxAl2NVzq_ezjNjsDgPQrY0DXTYFuL")
                )
            ),
            SubjectContent(
                subjectId = "maths",
                subjectName = context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")),
                units = listOf(
                    UnitContent(1, "Unit 1", "https://drive.google.com/uc?export=download&id=1Ste1DhbDfEgXqDcGVsBHuM4Sg0bwvdov"),
                    UnitContent(2, "Unit 2", "https://drive.google.com/uc?export=download&id=1zWUCuANl7T5dhyJCIUmK0wNH_iht3Kr6"),
                    UnitContent(3, "Unit 3", "https://drive.google.com/uc?export=download&id=17JVLUdR6WjyLZvpQQRMTgVKisBKWCHeV")
                )
            )
        )
    }
    
    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        return getClass1Content(context).find { it.subjectId == subjectId }
    }
}