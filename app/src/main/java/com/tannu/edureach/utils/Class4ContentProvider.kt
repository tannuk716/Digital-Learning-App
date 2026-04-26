package com.tannu.edureach.utils

import android.content.Context

object Class4ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass4Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1GZQ42RUOXbSYGFjNXNGGYUVJwdm26gPJ"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1sSB7McwZOUiSh4V5QYY7G87UfmUzMnuN"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1rBE5fnapiLQmAmzQrvB-H1YogJay1nbf")
            )),
            SubjectContent("english", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1R8mVLDAK24zimBBRo0mJVRgGnhldDML0"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=143r92jNuTC_d15Lydm6DOLnRaYEouId2"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=16Hfjzs9W9tuEFP6LN0WcbHkLeD3GT6V7")
            )),
            SubjectContent("maths", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1paioMHVCIG4uN2jH7FUUF_smlO_3-TuO"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1FR1Pjka8gzVLgpufFRe7xGxsprprWU3_"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1xu4YA1q3fLo5Qqguhbk9Dzpv9anEMZZX")
            )),
            SubjectContent("evs", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("evs")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1J4Kzks6fRv7NID_NJivjqxrqYhNGVLil"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1A_XyUX_uCJOj1Mw3kCvrR2WaSP7iPKtk"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1FB5Pia2aeO_9r2xmQDrs97Xnn_2Ga4_q")
            ))
        )
    }

    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        return getClass4Content(context).find { it.subjectId == subjectId }
    }
}