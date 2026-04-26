package com.tannu.edureach.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        

        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("APP_LANG", languageCode).commit()
        

        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(
            androidx.core.os.LocaleListCompat.forLanguageTags(languageCode)
        )
    }

    fun getLocale(context: Context): String {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return prefs.getString("APP_LANG", "en") ?: "en"
    }
    
    fun applyLocaleAndRecreate(activity: Activity, languageCode: String) {
        setLocale(activity, languageCode)
        activity.recreate()
    }
    
    fun attachBaseContext(context: Context): Context {
        val languageCode = getLocale(context)
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        
        return context.createConfigurationContext(config)
    }
}