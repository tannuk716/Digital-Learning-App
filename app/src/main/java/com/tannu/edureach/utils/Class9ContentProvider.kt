package com.tannu.edureach.utils

import android.content.Context

object Class9ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass9Content(context: Context): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("hindi")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1xlE-g_Z_Q9ga0ykpWdt7QwWEypqzdRUQ"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1e18_oeC-3KHQwz9rWEdpEeX4Qsk1QftN"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=12XNwpJVhq8vpJ4iZ8rvtogVSI5p7JyrY")
            )),
            SubjectContent("english", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("english")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1nhxwKiL7F_FZCczg7OSXazzKB9KjCClR"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=13kLRVdEhx7nrlgVi-Qv8MrjZEbs8VIBB"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1kBntSqujsntNc4_tNYbBYgeBSymF4Im7")
            )),
            SubjectContent("maths", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("maths")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1hqm_e_hR-gdSXSuHcGQTT0J7m3EykxZI"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1D8v2m_H5jfz6DO_qfFDnvKgNsb2XQXCA"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1QhojjDCD4syqxprcew85ZfVEk2Lejfg1")
            )),
            SubjectContent("science", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("science")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1m6iqcgrt_v6tOhs9zKKwl1WPDEfMDYoP"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1okkjenSJHlo2mivf8aKa2kc8KAeorFhg"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1cDymSEw1dKR7-XyViGF-v0-bo_yreIGY")
            )),
            SubjectContent("sst", context.getString(SubjectNameLocalizer.getSubjectNameResourceId("sst")), listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=16xd41lq5w1Hq0v4po3BJ58DN3OF7r5P0"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1mIdzy1wsY7viAACbDct9p9HAWyb2u3JS"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1zhDib2ccyV2aQnW15O5rr06fBWkME6dD")
            ))
        )
    }

    fun getSubjectContent(context: Context, subjectId: String): SubjectContent? {
        return getClass9Content(context).find { it.subjectId == subjectId }
    }
}