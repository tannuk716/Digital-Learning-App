package com.tannu.edureach.data

object SyllabusProvider {

    data class Topic(
        val unit: String, 
        val title: String, 
        val details: List<String>,
        val videoUrl: String = ""
    )
    
    data class Subject(val name: String, val icon: String, val topics: List<Topic>)

    fun getClass1Syllabus(): List<Subject> {
        return listOf(
            Subject(
                name = "English",
                icon = "📘",
                topics = listOf(
                    Topic("Unit 1", "Alphabet", listOf("A–Z recognition", "Capital and small letters"), "https://www.youtube.com/watch?v=hq3yfQnllfQ"),
                    Topic("Unit 2", "Phonics", listOf("Letter sounds", "A for Apple sound"), "https://www.youtube.com/watch?v=vVjX6uG9x1k"),
                    Topic("Unit 3", "Two Letter Words", listOf("am, an, at, is, in"), "https://www.youtube.com/watch?v=R9XwX1_T-8Q"),
                    Topic("Unit 4", "Three Letter Words", listOf("cat, bat, mat, rat, hat"), "https://www.youtube.com/watch?v=0WvpS2XvMhk"),
                    Topic("Unit 5", "Vowels", listOf("a, e, i, o, u"), "https://www.youtube.com/watch?v=arQxkdRY_Is"),
                    Topic("Unit 6", "Opposites", listOf("big / small", "hot / cold", "tall / short"), "https://www.youtube.com/watch?v=TW4O6R8T46E"),
                    Topic("Unit 7", "Naming Words", listOf("cat, dog, boy, girl"), "https://www.youtube.com/watch?v=8mG_6_Y_V-8"),
                    Topic("Unit 8", "Simple Sentences", listOf("I am a boy", "This is a cat"), "https://www.youtube.com/watch?v=7uU_3m78e9I"),
                    Topic("Unit 9", "Rhymes", listOf("Twinkle Twinkle", "Johny Johny", "Rain Rain Go Away"), "https://www.youtube.com/watch?v=yCjJyiqpAuU")
                )
            ),
            Subject(
                name = "Mathematics",
                icon = "📗",
                topics = listOf(
                    Topic("Unit 1", "Numbers", listOf("Numbers 1–100"), "https://www.youtube.com/watch?v=D0Ajq682yrA"),
                    Topic("Unit 2", "Counting", listOf("Count objects (1-20)"), "https://www.youtube.com/watch?v=By2hmo323xM"),
                    Topic("Unit 3", "Before After Between", listOf("Before 5 → 4", "After 6 → 7"), "https://www.youtube.com/watch?v=P2L-vBfE9_w"),
                    Topic("Unit 4", "Addition", listOf("Single digit addition: 2 + 3 = 5"), "https://www.youtube.com/watch?v=uK7V3fG6O5E"),
                    Topic("Unit 5", "Subtraction", listOf("Single digit subtraction: 5 − 2 = 3"), "https://www.youtube.com/watch?v=68mD8tq-P8U"),
                    Topic("Unit 6", "Shapes", listOf("Circle", "Square", "Triangle", "Rectangle"), "https://www.youtube.com/watch?v=TJhfl533l7w"),
                    Topic("Unit 7", "Patterns", listOf("🔵🔴🔵🔴 ?"), "https://www.youtube.com/watch?v=HoR_V_i6L-U"),
                    Topic("Unit 8", "Comparison", listOf("Greater than (>)", "Smaller than (<)"), "https://www.youtube.com/watch?v=KN_OitM9U5U")
                )
            ),
            Subject(
                name = "EVS",
                icon = "🌿",
                topics = listOf(
                    Topic("Unit 1", "My Body", listOf("Eyes", "Nose", "Hands", "Legs"), "https://www.youtube.com/watch?v=SUt8q0EKbms"),
                    Topic("Unit 2", "My Family", listOf("Mother", "Father", "Sister", "Brother"), "https://www.youtube.com/watch?v=FHaObkHEkHQ"),
                    Topic("Unit 3", "My School", listOf("Teacher", "Classroom", "Playground"), "https://www.youtube.com/watch?v=L2G73y5-7o0"),
                    Topic("Unit 4", "Animals", listOf("Dog", "Cow", "Lion", "Tiger"), "https://www.youtube.com/watch?v=5oYKonuEJKM"),
                    Topic("Unit 5", "Birds", listOf("Sparrow", "Crow", "Parrot", "Peacock"), "https://www.youtube.com/watch?v=jFIOu8uTclw"),
                    Topic("Unit 6", "Fruits", listOf("Apple", "Mango", "Banana", "Orange"), "https://www.youtube.com/watch?v=5tB9VLUXm6U"),
                    Topic("Unit 7", "Vegetables", listOf("Carrot", "Potato", "Tomato", "Onion"), "https://www.youtube.com/watch?v=vL47d2yW7_8"),
                    Topic("Unit 8", "Good Habits", listOf("Brush teeth", "Wash hands", "Take bath daily"), "https://www.youtube.com/watch?v=7uU_3m78e9I")
                )
            ),
            Subject(
                name = "General Knowledge",
                icon = "🧠",
                topics = listOf(
                    Topic("Unit 1", "National Symbols", listOf("National Flag", "National Animal (Tiger)", "National Bird (Peacock)"), "https://www.youtube.com/watch?v=2vU207h7m_w"),
                    Topic("Unit 2", "Community Helpers", listOf("Doctor", "Teacher", "Police", "Farmer"), "https://www.youtube.com/watch?v=jt2q1c_89_E"),
                    Topic("Unit 3", "Vehicles", listOf("Car", "Bus", "Train", "Airplane"), "https://www.youtube.com/watch?v=fS_YQhYqU98"),
                    Topic("Unit 4", "Festivals", listOf("Diwali", "Holi", "Eid", "Christmas"), "https://www.youtube.com/watch?v=6K_h9p6Hj8o")
                )
            )
        )
    }
}