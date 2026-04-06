package com.tannu.edureach.utils

object Class3ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass3Content(): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", "Hindi", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1BHEeoRASMIJY8CZadTF4MxT1GyBdzftC"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1uawtNlAZi8X6uJAoo4TYF6xObEwJQyuF"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1U05y-rbVKJ0Iu8AUt2raCGK-V_S0qLer")
            )),
            SubjectContent("english", "English", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1ul0OU2haqQdj_sFPd-Aw2P7jguFDbrJ0"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1hwX9GlEgN3U_pU38nlHkowh3ttFa3CvH"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1B3-SFEs679xrdjsSbA1_LURI2o6XFasU")
            )),
            SubjectContent("maths", "Maths", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1N8EGiojwwz6xY7Wu-ss-LiIEfUcgu4VW"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=15kUdgMCGd_F5ozI1NlGfKDNumm08zqXC"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1AqIKuKY1gZ2XEApI6nkh_6mS8Wvx8O77")
            )),
            SubjectContent("evs", "EVS", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1ycksIDOcDZfDSsbTKXajzEnmzYSa_l6_"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1q_5JTiEORPSaSf27sivDiQnf8nrxXqI3")
            ))
        )
    }

    fun getSubjectContent(subjectId: String): SubjectContent? {
        return getClass3Content().find { it.subjectId == subjectId }
    }
}
