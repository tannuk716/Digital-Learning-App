package com.tannu.edureach.utils

import android.app.Activity
import android.content.Context
import com.tannu.edureach.R

object ThemeManager {
    
    fun applyTheme(activity: Activity) {
        val prefs = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userClass = prefs.getInt("USER_CLASS", 1)
        
        val themeResId = when (userClass) {
            1 -> R.style.Theme_RuralLearningApp_Jungle
            2 -> R.style.Theme_RuralLearningApp_Cartoon
            3 -> R.style.Theme_RuralLearningApp_Space
            4 -> R.style.Theme_RuralLearningApp_Ocean
            5 -> R.style.Theme_RuralLearningApp_Nature
            6 -> R.style.Theme_RuralLearningApp_Tech
            7 -> R.style.Theme_RuralLearningApp_Lab
            8 -> R.style.Theme_RuralLearningApp_Galaxy
            9 -> R.style.Theme_RuralLearningApp_Modern
            10 -> R.style.Theme_RuralLearningApp_Professional
            else -> R.style.Theme_RuralLearningApp
        }
        
        activity.setTheme(themeResId)
    }
}