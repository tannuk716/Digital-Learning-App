# Daily Questions with Weekly Difficulty Progression ✅

## Overview
The Communication Skills Daily Speaking Practice now features:
- ✅ Questions change daily automatically
- ✅ Difficulty increases weekly (4-week cycle)
- ✅ 40 total questions (10 per week)
- ✅ Automatic progression based on calendar

## How It Works

### Daily Rotation
- Questions change every day based on the calendar
- Uses `Calendar.DAY_OF_YEAR` to select question
- Students get a different question each day
- Cycles through all 10 questions in the current week's level

### Weekly Difficulty Progression

The system automatically progresses through 4 difficulty levels:

#### Week 1: Basic Level 📗
**Focus**: Simple present tense, personal information
**Questions**:
1. What is your name?
2. How old are you?
3. Where do you live?
4. What is your favorite color?
5. Do you like animals?
6. What do you eat for breakfast?
7. How many people are in your family?
8. What is your favorite game?
9. Do you have any pets?
10. What makes you happy?

**Expected Response**: 1-2 simple sentences

#### Week 2: Elementary Level 📘
**Focus**: Simple descriptions, basic past tense
**Questions**:
1. Describe your best friend.
2. What did you do yesterday?
3. Tell me about your favorite toy.
4. What is your favorite subject in school?
5. Describe your bedroom.
6. What do you like to do after school?
7. Tell me about your favorite food.
8. What games do you play with friends?
9. Describe your teacher.
10. What do you do on weekends?

**Expected Response**: 2-3 sentences with descriptions

#### Week 3: Intermediate Level 📙
**Focus**: Past tense, longer responses, storytelling
**Questions**:
1. Tell me about your last birthday.
2. What is the best gift you ever received?
3. Describe a fun day you had recently.
4. What do you want to be when you grow up and why?
5. Tell me about a book you read.
6. Describe your favorite place to visit.
7. What is something new you learned this week?
8. Tell me about a time you helped someone.
9. What makes a good friend?
10. Describe your dream vacation.

**Expected Response**: 3-4 sentences with details

#### Week 4: Advanced Level 📕
**Focus**: Complex sentences, opinions, hypothetical situations
**Questions**:
1. If you could have any superpower, what would it be and why?
2. What would you do if you found a lost puppy?
3. Explain why education is important.
4. Describe how you would spend a million rupees.
5. What changes would you make to your school?
6. Tell me about someone you admire and why.
7. How can we help protect the environment?
8. What is the most important lesson you've learned?
9. Describe your perfect day from morning to night.
10. If you could travel anywhere, where would you go and what would you do?

**Expected Response**: 4-5 sentences with reasoning

### Automatic Cycling
- After Week 4, the system automatically returns to Week 1
- Uses `Calendar.WEEK_OF_YEAR` to determine current week
- Formula: `((weekOfYear - 1) % 4) + 1`
- Ensures continuous progression

## UI Indicators

### Difficulty Display
Students can see their current level:
```
📊 Basic Level - Week 1
📊 Elementary Level - Week 2
📊 Intermediate Level - Week 3
📊 Advanced Level - Week 4
```

### Question Counter
Shows progress within the week:
```
Question 1 of 10
Question 2 of 10
...
Question 10 of 10
```

## AI Evaluation Adjustments

The AI feedback system adapts to difficulty level:

### Week 1 (Basic)
- Expects simple, short answers
- More lenient scoring
- Focuses on basic pronunciation
- Encourages any attempt

### Week 2 (Elementary)
- Expects simple descriptions
- Moderate scoring
- Checks for descriptive words
- Encourages detail

### Week 3 (Intermediate)
- Expects past tense usage
- Standard scoring
- Checks grammar more carefully
- Encourages longer responses

### Week 4 (Advanced)
- Expects complex sentences
- Stricter scoring
- Checks for opinions and reasoning
- Encourages sophisticated language

## Progress Tracking

### Saved Data
The system saves:
- `speakingScore`: Average score
- `lastPracticeDate`: Last practice timestamp
- `currentWeekLevel`: Current difficulty level (1-4)
- `streakCount`: Daily practice streak

### Firebase Structure
```
users/{userId}/communication_skills/progress/
├── speakingScore: number
├── pronunciationScore: number
├── vocabularyLearned: number
├── streakCount: number
├── lastPracticeDate: timestamp
└── currentWeekLevel: number (1-4)
```

## Student Experience

### Day 1 (Week 1)
- Opens app
- Sees: "📊 Basic Level - Week 1"
- Gets: "What is your name?"
- Practices basic response

### Day 8 (Week 2)
- Opens app
- Sees: "📊 Elementary Level - Week 2"
- Gets: "Describe your best friend."
- Practices descriptions

### Day 15 (Week 3)
- Opens app
- Sees: "📊 Intermediate Level - Week 3"
- Gets: "Tell me about your last birthday."
- Practices storytelling

### Day 22 (Week 4)
- Opens app
- Sees: "📊 Advanced Level - Week 4"
- Gets: "If you could have any superpower..."
- Practices complex thinking

### Day 29 (Back to Week 1)
- Cycle repeats
- Students practice basics again with improved skills
- Continuous learning loop

## Benefits

### For Students
✅ Gradual skill building
✅ No overwhelming difficulty jumps
✅ Clear progress indicators
✅ Variety in questions
✅ Continuous challenge

### For Teachers
✅ Automatic progression
✅ No manual intervention needed
✅ Structured curriculum
✅ Measurable progress
✅ Aligned with learning objectives

### For Learning
✅ Scaffolded difficulty
✅ Repetition with variation
✅ Skill reinforcement
✅ Confidence building
✅ Long-term retention

## Technical Implementation

### Calendar-Based Selection
```kotlin
// Calculate week level (1-4)
val weekOfYear = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
currentWeekLevel = ((weekOfYear - 1) % 4) + 1

// Select daily question
val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
val questions = getCurrentWeekQuestions()
currentQuestionIndex = dayOfYear % questions.size
```

### Difficulty Context for AI
```kotlin
val difficultyContext = when (currentWeekLevel) {
    1 -> "This is a basic level question. Expect simple, short answers."
    2 -> "This is an elementary level question. Expect simple descriptions."
    3 -> "This is an intermediate level question. Expect past tense and longer responses."
    4 -> "This is an advanced level question. Expect complex sentences and opinions."
}
```

## Testing the System

### To Test Different Weeks
You can manually test by changing the device date:
1. Week 1: Set date to any week 1, 5, 9, 13, etc.
2. Week 2: Set date to any week 2, 6, 10, 14, etc.
3. Week 3: Set date to any week 3, 7, 11, 15, etc.
4. Week 4: Set date to any week 4, 8, 12, 16, etc.

### To Test Different Days
Change the day of the month to see different questions within the same week.

## Future Enhancements (Optional)

### Possible Additions
- Custom difficulty levels per student
- Teacher-assigned questions
- Student performance-based progression
- More questions per level
- Audio pronunciation guides
- Video examples

## Status

✅ Daily question rotation implemented
✅ Weekly difficulty progression implemented
✅ 40 questions across 4 levels
✅ Automatic cycling
✅ AI evaluation adjustments
✅ Progress tracking
✅ UI indicators
✅ No diagnostic errors

**Ready for**: Testing and deployment

## Summary

The Daily Speaking Practice now provides a comprehensive, progressive learning experience:
- Questions change daily
- Difficulty increases weekly
- Automatic 4-week cycle
- AI adapts to difficulty level
- Clear progress indicators
- Engaging and educational

Students will experience continuous growth in their English speaking skills with this structured, automated progression system!
