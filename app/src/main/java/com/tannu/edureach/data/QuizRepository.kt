package com.tannu.edureach.data

object QuizRepository {

    data class Question(
        val category: String,
        val difficulty: Int,
        val text: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        val imageUrl: String? = null
    )

    fun getAllQuestions(): List<Question> {
        val list = mutableListOf<Question>()

        list.add(Question("English", 1, "A is for?", listOf("Apple", "Dog", "Car", "Ball"), 0))
        list.add(Question("English", 1, "B is for?", listOf("Fish", "Ball", "Tree", "Cat"), 1))
        list.add(Question("English", 1, "C is for?", listOf("Dog", "Cat", "Sun", "Bat"), 1))
        list.add(Question("English", 1, "D is for?", listOf("Dog", "Apple", "Egg", "Fish"), 0))
        list.add(Question("English", 1, "E is for?", listOf("Car", "Dog", "Elephant", "Ball"), 2))
        
        list.add(Question("English", 2, "Which letter comes after C?", listOf("B", "D", "E", "F"), 1))
        list.add(Question("English", 2, "Which letter comes before M?", listOf("N", "L", "K", "O"), 1))
        list.add(Question("English", 2, "Fill missing letter: C _ T", listOf("A", "O", "E", "I"), 0))
        list.add(Question("English", 2, "Fill missing letter: D _ G", listOf("A", "E", "O", "U"), 2))
        list.add(Question("English", 2, "Fill missing letter: B _ T", listOf("A", "O", "I", "U"), 0))
        
        list.add(Question("English", 2, "Identify the vowel", listOf("B", "C", "A", "D"), 2))
        list.add(Question("English", 2, "Identify the vowel", listOf("X", "Y", "Z", "E"), 3))
        list.add(Question("English", 2, "Identify the vowel", listOf("I", "T", "G", "H"), 0))
        list.add(Question("English", 2, "Identify the vowel", listOf("P", "Q", "O", "R"), 2))
        list.add(Question("English", 2, "Identify the vowel", listOf("M", "N", "U", "V"), 2))
        
        list.add(Question("English", 3, "What is the opposite of Hot?", listOf("Warm", "Cold", "Big", "Tall"), 1))
        list.add(Question("English", 3, "What is the opposite of Big?", listOf("Huge", "Small", "Tall", "Short"), 1))
        list.add(Question("English", 3, "What is the opposite of Tall?", listOf("Short", "Long", "Big", "Cold"), 0))
        list.add(Question("English", 3, "What is the opposite of Day?", listOf("Morning", "Night", "Evening", "Sun"), 1))
        list.add(Question("English", 3, "What is the opposite of Happy?", listOf("Sad", "Angry", "Smile", "Cry"), 0))
        
        list.add(Question("English", 3, "Which is a naming word (noun)?", listOf("Run", "Eat", "Cat", "Jump"), 2))
        list.add(Question("English", 3, "Which is a naming word (noun)?", listOf("Sleep", "Boy", "Play", "Sing"), 1))
        list.add(Question("English", 3, "Which is an action word (verb)?", listOf("Dog", "Run", "Table", "House"), 1))
        list.add(Question("English", 3, "Complete: I am a ___", listOf("Boy", "Run", "Eat", "Cold"), 0))
        list.add(Question("English", 3, "Complete: This ___ a cat.", listOf("am", "are", "is", "be"), 2))

        list.add(Question("Math", 1, "What comes after 1?", listOf("0", "2", "3", "4"), 1))
        list.add(Question("Math", 1, "What comes after 5?", listOf("4", "5", "6", "7"), 2))
        list.add(Question("Math", 1, "What comes before 4?", listOf("2", "3", "5", "6"), 1))
        list.add(Question("Math", 1, "What comes before 10?", listOf("8", "9", "11", "0"), 1))
        list.add(Question("Math", 1, "What is between 7 and 9!", listOf("6", "8", "10", "11"), 1))
        
        list.add(Question("Math", 2, "2 + 2 = ?", listOf("3", "4", "5", "6"), 1))
        list.add(Question("Math", 2, "3 + 1 = ?", listOf("2", "3", "4", "5"), 2))
        list.add(Question("Math", 2, "4 + 3 = ?", listOf("6", "7", "8", "9"), 1))
        list.add(Question("Math", 2, "5 + 0 = ?", listOf("0", "4", "5", "6"), 2))
        list.add(Question("Math", 2, "1 + 9 = ?", listOf("8", "9", "10", "11"), 2))
        
        list.add(Question("Math", 2, "5 - 2 = ?", listOf("1", "2", "3", "4"), 2))
        list.add(Question("Math", 2, "4 - 1 = ?", listOf("2", "3", "4", "5"), 1))
        list.add(Question("Math", 2, "7 - 3 = ?", listOf("3", "4", "5", "6"), 1))
        list.add(Question("Math", 2, "10 - 5 = ?", listOf("4", "5", "6", "7"), 1))
        list.add(Question("Math", 2, "8 - 8 = ?", listOf("0", "1", "8", "16"), 0))
        
        list.add(Question("Math", 3, "Which number is bigger?", listOf("5", "2", "3", "1"), 0))
        list.add(Question("Math", 3, "Which number is bigger?", listOf("7", "9", "6", "8"), 1))
        list.add(Question("Math", 3, "Which number is smaller?", listOf("10", "8", "4", "6"), 2))
        list.add(Question("Math", 3, "Which number is smaller?", listOf("15", "12", "18", "20"), 1))
        list.add(Question("Math", 3, "Complete pattern: 2, 4, 6, ?", listOf("7", "8", "9", "10"), 1))
        
        list.add(Question("Math", 3, "Complete pattern: 1, 3, 5, ?", listOf("6", "7", "8", "9"), 1))
        list.add(Question("Math", 2, "What shape is a ball?", listOf("Square", "Circle", "Triangle", "Rectangle"), 1))
        list.add(Question("Math", 2, "What shape is a door?", listOf("Square", "Circle", "Triangle", "Rectangle"), 3))
        list.add(Question("Math", 2, "How many sides does a triangle have?", listOf("1", "2", "3", "4"), 2))
        list.add(Question("Math", 2, "How many sides does a square have?", listOf("2", "3", "4", "5"), 2))

        list.add(Question("EVS", 1, "We see with our?", listOf("Nose", "Eyes", "Ears", "Hands"), 1))
        list.add(Question("EVS", 1, "We hear with our?", listOf("Eyes", "Ears", "Nose", "Legs"), 1))
        list.add(Question("EVS", 1, "We smell with our?", listOf("Mouth", "Ears", "Eyes", "Nose"), 3))
        list.add(Question("EVS", 1, "We walk with our?", listOf("Hands", "Legs", "Ears", "Eyes"), 1))
        list.add(Question("EVS", 1, "How many fingers on one hand?", listOf("4", "5", "6", "10"), 1))
        
        list.add(Question("EVS", 2, "Which animal gives us milk?", listOf("Dog", "Lion", "Cow", "Tiger"), 2))
        list.add(Question("EVS", 2, "Which animal barks?", listOf("Cat", "Dog", "Cow", "Horse"), 1))
        list.add(Question("EVS", 2, "Which animal meows?", listOf("Dog", "Cat", "Cow", "Pig"), 1))
        list.add(Question("EVS", 2, "King of the jungle is?", listOf("Elephant", "Monkey", "Tiger", "Lion"), 3))
        list.add(Question("EVS", 2, "Which is a pet animal?", listOf("Lion", "Tiger", "Dog", "Bear"), 2))
        
        list.add(Question("EVS", 2, "Which bird can talk like us?", listOf("Crow", "Sparrow", "Parrot", "Eagle"), 2))
        list.add(Question("EVS", 2, "Which bird dances in rain?", listOf("Sparrow", "Peacock", "Crow", "Parrot"), 1))
        list.add(Question("EVS", 2, "Which bird is black?", listOf("Parrot", "Peacock", "Swan", "Crow"), 3))
        list.add(Question("EVS", 2, "Which of these is a fruit?", listOf("Potato", "Apple", "Onion", "Carrot"), 1))
        list.add(Question("EVS", 2, "Which of these is a fruit?", listOf("Banana", "Brinjal", "Tomato", "Peas"), 0))
        
        list.add(Question("EVS", 3, "Which fruit is yellow and long?", listOf("Apple", "Grape", "Banana", "Mango"), 2))
        list.add(Question("EVS", 3, "Which of these is a vegetable?", listOf("Apple", "Mango", "Carrot", "Banana"), 2))
        list.add(Question("EVS", 3, "Which vegetable makes you cry?", listOf("Potato", "Onion", "Tomato", "Carrot"), 1))
        list.add(Question("EVS", 3, "You should brush your teeth?", listOf("Once a month", "Twice a day", "Never", "Once a year"), 1))
        list.add(Question("EVS", 3, "We should wash our hands?", listOf("Before eating", "After eating", "Both", "Never"), 2))
        
        list.add(Question("EVS", 1, "Father's brother is called?", listOf("Uncle", "Aunt", "Grandfather", "Cousin"), 0))
        list.add(Question("EVS", 1, "Mother's sister is called?", listOf("Uncle", "Aunt", "Grandmother", "Cousin"), 1))
        list.add(Question("EVS", 1, "Where do we go to study?", listOf("Hospital", "Park", "School", "Market"), 2))
        list.add(Question("EVS", 1, "Who teaches us in school?", listOf("Doctor", "Police", "Teacher", "Farmer"), 2))
        list.add(Question("EVS", 1, "Where do we play in school?", listOf("Classroom", "Library", "Playground", "Office"), 2))

        list.add(Question("GK", 1, "National Animal of India?", listOf("Lion", "Elephant", "Tiger", "Cow"), 2))
        list.add(Question("GK", 1, "National Bird of India?", listOf("Parrot", "Crow", "Peacock", "Sparrow"), 2))
        list.add(Question("GK", 1, "National Flower of India?", listOf("Rose", "Lotus", "Sunflower", "Lily"), 1))
        list.add(Question("GK", 1, "What colors are in our National Flag?", listOf("Red, Green, Blue", "Saffron, White, Green", "Yellow, Red, Blue", "Black, White, Red"), 1))
        list.add(Question("GK", 1, "How many spokes in the Ashoka Chakra?", listOf("20", "22", "24", "26"), 2))
        
        list.add(Question("GK", 2, "Who treats us when we are sick?", listOf("Teacher", "Farmer", "Doctor", "Police"), 2))
        list.add(Question("GK", 2, "Who catches thieves?", listOf("Doctor", "Teacher", "Farmer", "Police"), 3))
        list.add(Question("GK", 2, "Who grows food for us?", listOf("Farmer", "Pilot", "Doctor", "Police"), 0))
        list.add(Question("GK", 2, "Who brings our letters?", listOf("Doctor", "Postman", "Police", "Teacher"), 1))
        list.add(Question("GK", 2, "Who drives a bus?", listOf("Pilot", "Driver", "Doctor", "Farmer"), 1))
        
        list.add(Question("GK", 2, "Which vehicle flies in the sky?", listOf("Car", "Train", "Airplane", "Boat"), 2))
        list.add(Question("GK", 2, "Which vehicle runs on tracks?", listOf("Bus", "Car", "Train", "Bicycle"), 2))
        list.add(Question("GK", 2, "Which vehicle moves on water?", listOf("Car", "Train", "Boat", "Airplane"), 2))
        list.add(Question("GK", 2, "Which vehicle has 2 wheels?", listOf("Car", "Bus", "Train", "Bicycle"), 3))
        list.add(Question("GK", 2, "Which vehicle has 4 wheels?", listOf("Bicycle", "Motorcycle", "Car", "Auto"), 2))
        
        list.add(Question("GK", 3, "Festival of lights is?", listOf("Holi", "Diwali", "Eid", "Christmas"), 1))
        list.add(Question("GK", 3, "Festival of colors is?", listOf("Holi", "Diwali", "Eid", "Christmas"), 0))
        list.add(Question("GK", 3, "Santa Claus comes on?", listOf("Holi", "Diwali", "Eid", "Christmas"), 3))
        list.add(Question("GK", 3, "How many days in a week?", listOf("5", "6", "7", "8"), 2))
        list.add(Question("GK", 3, "How many months in a year?", listOf("10", "11", "12", "13"), 2))
        
        list.add(Question("GK", 1, "Which day comes after Monday?", listOf("Sunday", "Tuesday", "Wednesday", "Friday"), 1))
        list.add(Question("GK", 1, "What shines in the sky during the day?", listOf("Moon", "Stars", "Sun", "Cloud"), 2))
        list.add(Question("GK", 1, "What shines in the sky at night?", listOf("Sun", "Rainbow", "Moon and Stars", "Kite"), 2))
        list.add(Question("GK", 1, "What do we drink?", listOf("Bread", "Water", "Rice", "Apple"), 1))
        list.add(Question("GK", 1, "What do we eat?", listOf("Water", "Juice", "Milk", "Bread"), 3))

        return list
    }

    fun generateRevisionQuiz(weakTopics: List<String>, count: Int = 10): List<Question> {
        val all = getAllQuestions()
        val pool = mutableListOf<Question>()
        

        pool.addAll(all.filter { weakTopics.contains(it.category) })
        pool.addAll(all.filter { weakTopics.contains(it.category) })
        

        pool.addAll(all)

        return pool.shuffled().take(count)
    }
}