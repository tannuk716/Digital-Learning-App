package com.tannu.edureach.utils

import android.content.Context

object Class6ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass6Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1FDH33H4BMBCLGYJwYb8T_VI6x3C6-C9m"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1qKJOdhPm6LcweVrYwgdSjz5rv0HaJ-xw"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1L-KM9WKYB8fg71Cww1tiCso53ka-izUE")
            )),
            SubjectContent("english", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1tRwgaCge6B-gok2ZeN6xLaBhkIsLiZPB"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1ui-Yk7k7d_oKYBcaniGgBI2xX3r5sFE2"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1OMKXqA00VzQe0-DHcJlj2-vgVoXHapeH")
            )),
            SubjectContent("maths", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1bKomsHO785_UczCiOTA94USMoWbRoS9F"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1SE5LlqPcUih8HS4g4J5gbOH56p0ifxSp"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1I7uCn93PpWZjtWqO8s9726IMiz5ufndi")
            )),
            SubjectContent("evs", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("evs")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1CO1zJswuq5zk8HaOtClCxIa86WUk5xOt"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1Ml9rsGLlwGdoVGSVBg6ogh__j3gRAJaV"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1ZsM1eGEcmBF6oqtxpWjhOrDI5nbNzJRB")
            )),
            SubjectContent("sst", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("sst")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1BR4WWVuYG_JQ-4itWoMJ7IbXcirhOhgR"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=19IJ-HIxMQnPkXC0NmGi_KypcKeSO2E5o"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1XbtU_2J8OTeqGsNalvjdmtATzIhdzDvX")
            ))
        )
    }

    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        return getClass6Content(context).find { it.subjectId == subjectId }
    }
}