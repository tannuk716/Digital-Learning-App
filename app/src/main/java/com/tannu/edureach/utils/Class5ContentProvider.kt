package com.tannu.edureach.utils

object Class5ContentProvider {
    data class UnitContent(val unitName: String, val pdfUrl: String)
    data class SubjectContent(val subjectId: String, val subjectName: String, val units: List<UnitContent>)

    fun getClass5Content(): List<SubjectContent> {
        return listOf(
            SubjectContent("hindi", "Hindi", listOf(
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1uP5sm2kwFoWQiQj0skeI4hfWLXgeEH7o"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=17tGpx4TW2AOxuJA7ShnXCEGeV2ZB7Ri4")
            )),
            SubjectContent("english", "English", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=189Ih08h0W6W-i2SEW9bLhfWdOn8I3-NU"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1IkoSPbWCC6P2Aph0twVg50dHN9P9e9qL"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1oGV9eOYRXLzl8PPYUfj7MOdAV2lmzH5f")
            )),
            SubjectContent("maths", "Maths", listOf(
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=1CFLNCBzxykWK0vaKoRqmCeMfy_fXPsJW"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=17BZu8wzK9xjMpDhet_xm_otImoYYYDYw"),
                UnitContent("Unit 4", "https://drive.google.com/uc?export=download&id=1IoiUbEiwjCci8IVt-QkOYp1ZvXjbVJ3Q")
            )),
            SubjectContent("evs", "EVS", listOf(
                UnitContent("Unit 1", "https://drive.google.com/uc?export=download&id=1iLjJjGHhsK9MbAFesJsD1ADfmKzEHrPH"),
                UnitContent("Unit 2", "https://drive.google.com/uc?export=download&id=126Yn4LzUXIM5gumcN4BoH-An56GGrL9Z"),
                UnitContent("Unit 3", "https://drive.google.com/uc?export=download&id=1y-aw697rfIWjblLpNhQC22sNOvdEuMyY")
            ))
        )
    }

    fun getSubjectContent(subjectId: String): SubjectContent? {
        return getClass5Content().find { it.subjectId == subjectId }
    }
}
