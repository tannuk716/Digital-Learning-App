package com.tannu.edureach.utils

import android.content.Context
import com.tannu.edureach.R

object SubjectNameHelper {
    fun getSubjectNameResourceId(subjectId: String): Int {
        return when (subjectId.lowercase()) {
            "english" -> R.string.subject_english
            "maths" -> R.string.subject_maths
            "hindi" -> R.string.subject_hindi
            "science" -> R.string.subject_science
            "evs" -> R.string.subject_evs
            "sst" -> R.string.subject_sst
            "punjabi" -> R.string.subject_punjabi
            else -> R.string.subject_english
        }
    }
    
    fun getLocalizedSubjectName(context: Context, subjectId: String): String {
        return context.getString(getSubjectNameResourceId(subjectId))
    }
}