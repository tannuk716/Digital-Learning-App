package com.tannu.edureach.evaluation

class PromptBuilder {
    
    companion object {

        private val difficultyContextCache = mutableMapOf<Int, String>()
    }
    
    fun buildDailySpeakingPrompt(
        question: String,
        studentResponse: String,
        difficultyLevel: Int,
        commonMistakes: List<String>
    ): String {
        val levelName = getDifficultyLevelName(difficultyLevel)
        val difficultyContext = getDifficultyContext(difficultyLevel)
        val commonMistakesText = if (commonMistakes.isNotEmpty()) {
            commonMistakes.joinToString("\n- ", prefix = "- ")
        } else {
            "None identified yet"
        }
        
        return """
You are an English language teacher evaluating a student's speaking response.

CONTEXT:
- Student Level: Class 5-10
- Difficulty: Week $difficultyLevel - $levelName
- $difficultyContext

QUESTION: $question

STUDENT'S RESPONSE: $studentResponse

EVALUATION TASK:
Analyze the response for:
1. Vocabulary correctness (wrong words, inappropriate word choice)
2. Grammar errors (tense, subject-verb agreement, sentence structure)
3. Completeness (did they fully answer the question?)
4. Fluency and coherence

COMMON MISTAKES TO WATCH FOR:
$commonMistakesText

OUTPUT FORMAT (strict JSON):
{
  "score": <0-100>,
  "mistakes": [
    {
      "type": "wrong_word|grammar_error|incomplete_response|tense_error|subject_verb_agreement",
      "wrongText": "<exact text from student response>",
      "correctText": "<what it should be>",
      "explanation": "<simple explanation>"
    }
  ],
  "pronunciation": "Excellent|Good|Fair|Needs Improvement",
  "fluency": "High|Medium|Low",
  "grammar": "Correct|Minor Errors|Major Errors",
  "completeness": "Complete|Partial|Incomplete",
  "suggestions": [
    "<specific actionable suggestion 1>",
    "<specific actionable suggestion 2>"
  ]
}

SCORING GUIDELINES:
- 90-100: Excellent response with no or very minor errors
- 70-89: Good response with minor errors
- 50-69: Acceptable but needs improvement
- Below 50: Significant issues requiring attention

Be encouraging but honest. Adjust expectations based on difficulty level.
        """.trimIndent()
    }
    
    fun buildPronunciationPrompt(
        targetWord: String,
        studentSaid: String
    ): String {
        return """
You are a pronunciation coach evaluating a student's pronunciation.

TARGET WORD: $targetWord
STUDENT SAID: $studentSaid

EVALUATION TASK:
Compare the student's pronunciation attempt with the target word.

OUTPUT FORMAT (strict JSON):
{
  "score": <0-100>,
  "accuracy": "Excellent|Good|Fair|Needs Practice",
  "phoneticMatch": "High|Medium|Low",
  "specificIssues": [
    "<specific sound or syllable that needs work>"
  ],
  "suggestion": "<specific tip to improve pronunciation>",
  "example": "<example of correct usage or practice tip>"
}

SCORING GUIDELINES:
- 90-100: Nearly perfect pronunciation
- 70-89: Good pronunciation with minor issues
- 50-69: Understandable but needs improvement
- Below 50: Difficult to understand, needs significant practice

Be supportive and provide actionable advice.
        """.trimIndent()
    }
    
    fun buildVocabularyPrompt(
        word: String,
        meaning: String,
        isCorrect: Boolean,
        wrongAnswer: String = ""
    ): String {
        return if (isCorrect) {
            """
A Class 5-10 student correctly answered that "$word" means "$meaning".

Provide encouraging feedback with:
1. A congratulatory message (1 sentence)
2. An additional interesting fact or usage tip about the word (1-2 sentences)
3. A memory trick to remember this word (1 sentence)

Keep it simple, encouraging, and under 100 words.
Use emojis sparingly for engagement.
            """.trimIndent()
        } else {
            """
A Class 5-10 student thought "$word" means "$wrongAnswer", but it actually means "$meaning".

Provide supportive feedback with:
1. A gentle correction (1 sentence)
2. A simple explanation of why the correct answer is right (1-2 sentences)
3. A memory trick to remember this word (1 sentence)

Be encouraging and supportive. Keep it under 100 words.
Use emojis sparingly for engagement.
            """.trimIndent()
        }
    }
    
    private fun getDifficultyContext(level: Int): String {
        return difficultyContextCache.getOrPut(level) {
            when (level) {
                1 -> "This is a basic level question. Expect simple present tense and short answers (5-10 words). Students should use basic vocabulary."
                2 -> "This is an elementary level question. Expect simple descriptions with basic adjectives (10-20 words). Students should form complete sentences."
                3 -> "This is an intermediate level question. Expect past tense usage and longer responses (20-40 words). Students should provide details and explanations."
                4 -> "This is an advanced level question. Expect complex sentences, opinions, and reasoning (40+ words). Students should demonstrate critical thinking."
                else -> "Basic level question."
            }
        }
    }
    
    private fun getDifficultyLevelName(level: Int): String {
        return when (level) {
            1 -> "Basic Level"
            2 -> "Elementary Level"
            3 -> "Intermediate Level"
            4 -> "Advanced Level"
            else -> "Basic Level"
        }
    }
    
    private fun getExpectedResponseGuidelines(level: Int): String {
        return when (level) {
            1 -> "5-10 words, simple present tense, basic vocabulary"
            2 -> "10-20 words, simple descriptions, complete sentences"
            3 -> "20-40 words, past tense, detailed explanations"
            4 -> "40+ words, complex sentences, critical thinking"
            else -> "Basic response expected"
        }
    }
}