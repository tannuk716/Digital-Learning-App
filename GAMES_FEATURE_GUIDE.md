# Games Feature - Teacher Upload & Student Access

## Overview
Teachers can now add educational games organized by Class and Subject. Students can access these games through their dashboard, filtered by their class and chosen subject.

## Features Implemented

### For Teachers:
✅ Add games from Teacher Dashboard
✅ Select class (1-5)
✅ Select subject (English, Hindi, Maths, Science)
✅ Choose from 10 available games
✅ Auto-filled title and description

### For Students:
✅ Access games from Student Dashboard
✅ Select subject to see available games
✅ Games filtered by student's class
✅ Launch games directly

## Available Games

1. **Monkey Jump** - Jump and collect bananas
2. **Lion Math Challenge** - Solve math problems
3. **Parrot Spelling** - Learn spelling with parrot
4. **Snake Number Path** - Follow the number path
5. **Honey Count** - Count honey pots
6. **Animal Sound** - Learn animal sounds
7. **Shape Finder** - Find and match shapes
8. **Frog Jump Addition** - Addition with jumping frog
9. **Fruit Catch** - Catch falling fruits
10. **Memory Match** - Match pairs of cards

## Firebase Structure

```
classes/
  ├── class_1/
  │   ├── subjects/
  │   │   ├── english/
  │   │   │   ├── games/
  │   │   │   │   ├── {gameId}/
  │   │   │   │   │   ├── title: "Parrot Spelling"
  │   │   │   │   │   ├── description: "Learn spelling"
  │   │   │   │   │   ├── gameType: "parrot_spelling"
  │   │   │   │   │   ├── activityClass: "com.tannu.edureach.games.ParrotSpellingActivity"
  │   │   │   │   │   ├── classId: "class_1"
  │   │   │   │   │   ├── subjectId: "english"
  │   │   │   │   │   └── timestamp: 1234567890
  │   │   ├── maths/
  │   │   │   ├── games/
  │   │   │   │   ├── {gameId}/
  │   │   │   │   │   ├── title: "Lion Math Challenge"
  │   │   │   │   │   └── ...
  │   │   ├── hindi/
  │   │   └── science/
  ├── class_2/
  └── ...
```

## Teacher Workflow

### Step 1: Access Add Game
1. Login as Teacher
2. Go to Teacher Dashboard
3. Click "Add Game" button

### Step 2: Fill Game Details
1. **Select Class**: Choose Class 1-5
2. **Select Subject**: Choose English, Hindi, Maths, or Science
3. **Select Game Type**: Choose from dropdown
   - Title and description auto-fill
4. **Customize** (optional): Edit title/description
5. Click "Add Game"

### Step 3: Confirmation
- Success message appears
- Game is now available for students in that class/subject

## Student Workflow

### Step 1: Access Games
1. Login as Student
2. Go to Student Dashboard
3. Click "Games" card

### Step 2: Select Subject
1. Choose subject (English, Hindi, Maths, Science)
2. See all games available for that subject
3. Games are filtered by student's class automatically

### Step 3: Play Game
1. Click on a game card
2. Game launches immediately
3. Play and learn!

## Files Created

### Models
- `app/src/main/java/com/tannu/edureach/data/model/GameModel.kt`
  - GameModel data class
  - GameType data class

### Activities
- `app/src/main/java/com/tannu/edureach/AddGameActivity.kt`
  - Teacher interface to add games
  
- `app/src/main/java/com/tannu/edureach/SubjectGamesActivity.kt`
  - Student interface to select subject
  
- `app/src/main/java/com/tannu/edureach/GameListActivity.kt`
  - Display games for selected subject

### Layouts
- `app/src/main/res/layout/activity_add_game.xml`
  - Teacher add game form
  
- `app/src/main/res/layout/activity_subject_games.xml`
  - Subject selection grid
  
- `app/src/main/res/layout/activity_game_list.xml`
  - Game list display
  
- `app/src/main/res/layout/item_game.xml`
  - Individual game card

### Updated Files
- `TeacherDashboardActivity.kt` - Added "Add Game" button
- `StudentDashboardActivity.kt` - Updated Games card navigation
- `AndroidManifest.xml` - Registered new activities

## Example Usage

### Teacher Adds a Game

```kotlin
// Teacher selects:
Class: Class 1
Subject: Maths
Game Type: Lion Math Challenge
Title: "Lion Math Challenge" (auto-filled)
Description: "Solve math problems" (auto-filled)

// Saved to Firebase:
classes/class_1/subjects/maths/games/{gameId}
{
  "title": "Lion Math Challenge",
  "description": "Solve math problems",
  "gameType": "lion_math",
  "activityClass": "com.tannu.edureach.games.LionMathChallengeActivity",
  "classId": "class_1",
  "subjectId": "maths",
  "timestamp": 1234567890
}
```

### Student Accesses Game

```kotlin
// Student (Class 1) workflow:
1. Click "Games" on dashboard
2. Click "Maths" subject
3. See "Lion Math Challenge" card
4. Click to play
5. Game launches
```

## Game Assignment Examples

### Class 1 - English
- Parrot Spelling
- Animal Sound
- Memory Match

### Class 1 - Maths
- Honey Count
- Frog Jump Addition
- Snake Number Path

### Class 2 - Maths
- Lion Math Challenge
- Frog Jump Addition
- Fruit Catch

### Class 3 - Science
- Shape Finder
- Memory Match
- Animal Sound

## Testing

### Test as Teacher:
1. Login as teacher
2. Click "Add Game"
3. Select: Class 1, Maths, Lion Math Challenge
4. Click "Add Game"
5. Verify success message

### Test as Student:
1. Login as student (Class 1)
2. Click "Games"
3. Click "Maths"
4. Verify "Lion Math Challenge" appears
5. Click to launch game

### Verify in Firebase:
1. Open Firebase Console
2. Go to Firestore
3. Navigate: classes → class_1 → subjects → maths → games
4. Verify game document exists

## Troubleshooting

### Game doesn't appear for students
- Check class ID matches (class_1, class_2, etc.)
- Check subject ID matches (english, hindi, maths, science)
- Verify game was saved successfully in Firebase

### Game won't launch
- Check activityClass is correct
- Verify game activity exists in app
- Check AndroidManifest has game activity registered

### "Add Game" button not visible
- Check Teacher Dashboard layout has btnAddGame
- Verify TeacherDashboardActivity has click listener
- Rebuild the app

## Future Enhancements

Possible additions:
- [ ] Game difficulty levels
- [ ] Game completion tracking
- [ ] Leaderboards per game
- [ ] Game achievements/badges
- [ ] Custom game parameters
- [ ] Game preview/demo mode
- [ ] Bulk game upload
- [ ] Game categories/tags

## Database Queries

### Get games for a class/subject:
```kotlin
db.collection("classes").document("class_1")
  .collection("subjects").document("maths")
  .collection("games")
  .get()
```

### Add a new game:
```kotlin
val game = GameModel(
    title = "Lion Math Challenge",
    description = "Solve math problems",
    gameType = "lion_math",
    activityClass = "com.tannu.edureach.games.LionMathChallengeActivity",
    classId = "class_1",
    subjectId = "maths"
)

db.collection("classes").document("class_1")
  .collection("subjects").document("maths")
  .collection("games")
  .add(game)
```

## Benefits

### For Teachers:
- Easy game assignment
- Subject-specific games
- Class-appropriate content
- No technical knowledge needed

### For Students:
- Organized game access
- Subject-based learning
- Age-appropriate games
- Easy navigation

### For Learning:
- Gamified education
- Subject reinforcement
- Engaging content
- Skill practice

## Summary

The games feature is now fully integrated:
- ✅ Teachers can add games via dashboard
- ✅ Games organized by class and subject
- ✅ Students see games for their class
- ✅ Subject-wise game filtering
- ✅ 10 educational games available
- ✅ Easy to use interface
- ✅ Firebase integration complete

Students can now access educational games organized by their class and subject, making learning more engaging and structured!
