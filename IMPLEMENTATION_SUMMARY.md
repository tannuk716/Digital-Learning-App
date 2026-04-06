# Student Dashboard - Subject-wise Quizzes and Notes Implementation

## Overview
Implemented a hierarchical navigation system for the Student Dashboard where students first select a subject, then view subject-specific quizzes and notes uploaded by teachers.

## Changes Made

### 1. New Activities Created

#### SubjectQuizzesActivity.kt
- Displays a grid of available subjects (Maths, English, Hindi, Science)
- Loads the student's current class from Firebase
- Navigates to QuizListActivity when a subject is selected

#### SubjectNotesActivity.kt
- Displays a grid of available subjects (Maths, English, Hindi, Science)
- Loads the student's current class from Firebase
- Navigates to NoteListActivity when a subject is selected

#### QuizListActivity.kt
- Shows all quizzes for a specific subject across all units
- Fetches quizzes from Firebase for the selected class and subject
- Displays quiz title and number of questions
- Navigates to QuizActivity when a quiz is selected

#### NoteListActivity.kt
- Shows all notes for a specific subject across all units
- Fetches notes from Firebase for the selected class and subject
- Displays note title and description
- Opens notes in EducationalWebActivity when selected

### 2. Layout Files Created

- `activity_subject_quizzes.xml` - Subject selection screen for quizzes
- `activity_subject_notes.xml` - Subject selection screen for notes
- `activity_quiz_list.xml` - List of quizzes for a subject
- `activity_note_list.xml` - List of notes for a subject
- `item_subject_card.xml` - Card view for subject items
- `item_quiz.xml` - Card view for quiz items
- `item_note.xml` - Card view for note items

### 3. Resources Added

#### colors.xml
- `primary` - Primary app color
- `background_light` - Light background color
- `text_primary` - Primary text color
- `text_secondary` - Secondary text color

#### drawable/ic_back.xml
- Back arrow icon for navigation

### 4. Updated Files

#### StudentDashboardActivity.kt
- Updated "Notes" card click listener to navigate to SubjectNotesActivity
- Updated "Quizzes" card click listener to navigate to SubjectQuizzesActivity

#### AndroidManifest.xml
- Registered all 4 new activities

## Data Flow

### For Quizzes:
1. Student clicks "Quizzes" card on dashboard
2. SubjectQuizzesActivity shows available subjects
3. Student selects a subject (e.g., Maths)
4. QuizListActivity fetches and displays all quizzes for that subject
5. Student selects a quiz
6. QuizActivity opens with the selected quiz

### For Notes:
1. Student clicks "Notes" card on dashboard
2. SubjectNotesActivity shows available subjects
3. Student selects a subject (e.g., Science)
4. NoteListActivity fetches and displays all notes for that subject
5. Student selects a note
6. EducationalWebActivity opens the note URL

## Firebase Structure
The implementation works with the existing Firebase structure:
```
classes/{classId}/subjects/{subjectId}/units/{unitId}/
  ├── quizzes/
  │   └── {quizId}
  └── notes/
      └── {noteId}
```

## Teacher Upload Flow
When teachers upload content using AddLearningContentActivity or AddQuizActivity:
1. They select Class, Subject, and Unit
2. Content is stored in Firebase under the appropriate path
3. Students can then access this content through the subject-wise navigation

## Features
- Automatic class detection from student profile
- Empty state handling when no content is available
- Clean card-based UI for easy navigation
- Aggregates content from all units within a subject
- Maintains existing functionality while adding new navigation paths
