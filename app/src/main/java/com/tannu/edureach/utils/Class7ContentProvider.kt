package com.tannu.edureach.utils

import android.content.Context

object Class7ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass7Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1bmxE2tWujIeI8AYOK_O5nJdjTJvM-Y5Y"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1opsPNy4ItiUs_w39nEnUV0Z5EYyPuc4r"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1X83CYcM5GD7kAJcqTrIBOOEyqGNfz8ql")
            )),
            SubjectContent("english", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1u4cYfBoOdCQw3X3CgGPlaZT3dBjDWl2z"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1Oi_DL2wDoijhWz0P4Sd0FKXWBKZhVyRS"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1IUqpcfSTN3GFnOBAuIy_pyfxv98EmU0A")
            )),
            SubjectContent("maths", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1XUGsImLsy9Fa3RXRDR_FiDrXZkA2ZPNU"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=11UjdXobb3S74c3XsEpkLeNQW6l9QwBCx"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1WXgMIbJkMYROthupjR_HaNPQ-HZ76MRg")
            )),
            SubjectContent("evs", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("evs")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1Bv8NGDmPtO9ltb5c-5-6SL8hkoqWhXtU"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1-HUT5UhUZBUhkgXL8wLn2vA--ddc92tr"),
                UnitContent("Unit 4", "https://drive.google.com/uc?export=download&id=1EBZakEDz8_M8pi2bkoB-auwAaRcsrdF3")
            )),
            SubjectContent("sst", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("sst")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1tNmUzkW0R2iajztw0c1MPmXuLuuQcRzz"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1anUFNgx-AkP_Y3ccpdpJogn54DmvahBX"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1XUWtHqWlsmGiRE0EDCvyfnM9rw1ZPW8K")
            ))
        )
    }

    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        return getClass7Content(context).find { it.subjectId == subjectId }
    }
}