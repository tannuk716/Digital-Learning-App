package com.tannu.edureach.utils

object Class2ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass2Content(): List<SubjectContent> {
        return listOf(
            SubjectContent("english", "English", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1Lu40YoeJslLocV0JUDH2PZN7BDwtCHHi"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1lBXVKgctQQw-0yorUasa50f6jjdnxtAF"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1FB74P8Vfyrea4Wzpwj6guuN3igTnaqco")
            )),
            SubjectContent("hindi", "Hindi", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1ZyYfqFuZMHglOi9nakI207Ga64RoqeR2"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1i7Z-TCamU7hGKTlhJLHGOdOJp_AnuvaY"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1CD7zdSLKBv07H7N2pbyeReBrbLza4UrQ")
            )),
            SubjectContent("evs", "EVS", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1wL1iZtwEzx5dITbPO9wRq46meAqq-jKt"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1Z5pMqBxuuWpNc04j9lNCRBiJqjEP-zMj"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1_UkesXfyo1IDfvKkpwh_qXuU9c1wxhKU")
            )),
            SubjectContent("maths", "Maths", listOf(
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=13eBkM9oH1LoadnTRx3JcTMOOO1GPzKKS"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1ZcBt8kzFGrwQWTnHytkUUCYMCRMbyWDC")
            ))
        )
    }

    fun getSubjectContent(subjectId: String): SubjectContent? {
        return getClass2Content().find { it.subjectId == subjectId }
    }
}
