# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================================
# BuildConfig - Keep API keys and build constants
# ============================================
-keep class com.tannu.edureach.BuildConfig { *; }
-keepclassmembers class com.tannu.edureach.BuildConfig {
    public static final java.lang.String GEMINI_API_KEY;
}

# ============================================
# Gemini API - Keep all API-related classes
# ============================================
-keep class com.tannu.edureach.utils.Gemini** { *; }
-keep class com.tannu.edureach.utils.RetrofitClient { *; }
-keepclassmembers class com.tannu.edureach.utils.** { *; }

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data models for API
-keep class com.tannu.edureach.utils.GeminiRequest { *; }
-keep class com.tannu.edureach.utils.GeminiResponse { *; }
-keep class com.tannu.edureach.utils.Content { *; }
-keep class com.tannu.edureach.utils.Part { *; }
-keep class com.tannu.edureach.utils.GenerationConfig { *; }
-keep class com.tannu.edureach.utils.Candidate { *; }
-keep class com.tannu.edureach.utils.GeminiError { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**