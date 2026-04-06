package com.tannu.edureach.utils

object Class8ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass8Content(): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", "Hindi", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1B0d2E5WK5LpK_qe3P38sggDEqkf66ZjN"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1f7TkT_oT8I4JedTBYFkGh9a2Fiu5kONd")
            )),
            SubjectContent("english", "English", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1a1-sVrVGS1IJipgeUCEbqntA3ZhTAZm2"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1P0MZxdmhutkrYrcna5erxJZBqVd9Z5Wk"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1TRkl6N9LNj4HPovMHdDMTgj_2B8JiT5E")
            )),
            SubjectContent("maths", "Maths", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1vuGL6uxYThCUodx55nTMJa1lewS3yl0d"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1cLrPnpeFmsbr8C2stHsqM6vWCnN7lkrl"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1lD3GG0bYbEz46h6piDYKog2EZZHUC54J")
            )),
            SubjectContent("science", "Science", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1B8rBG1C3pHxTcGYl0PPT9m4v_V0PDH15"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1IvUix3ktQVEEJ91NDkybhftDhKF93R0m"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1KwxsTbJtugGzcV3Qs4lI1jxyqehnKZSu")
            )),
            SubjectContent("sst", "SST", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1ipawkCjvY1_hq6iFigUxzQ8DxctUxlZz"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1QFb-eroo8OF28nC4IRC-pA1CcouOzo0X"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1SqYhytevM3GO2Pfg24m6RYStR8JZIz0a")
            ))
        )
    }

    fun getSubjectContent(subjectId: String): SubjectContent? {
        return getClass8Content().find { it.subjectId == subjectId }
    }
}
