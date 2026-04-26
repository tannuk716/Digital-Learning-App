package com.tannu.edureach.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

object OfflineDataLoader {

    data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int
    )

    data class ContentUnit(
        val id: String,
        val title: String,
        val explanation: String,
        val notes: List<String>,
        val examples: List<String>,
        val videoUrl: String,
        val quiz: List<QuizQuestion>
    )

    data class Subject(
        val name: String,
        val icon: String,
        val units: List<ContentUnit>
    )

    data class ClassData(
        val id: Int,
        val name: String,
        val subjects: List<Subject>
    )

    private var cachedData: List<ClassData>? = null

    fun loadOfflineData(context: Context): List<ClassData> {
        if (cachedData != null) return cachedData!!
        
        val classesList = mutableListOf<ClassData>()
        try {
            val inputStream: InputStream = context.assets.open("offline_content.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            val parentObject = JSONObject(jsonString)
            val classesArray = parentObject.optJSONArray("classes") ?: JSONArray()
            
            for (i in 0 until classesArray.length()) {
                val classObj = classesArray.getJSONObject(i)
                val classId = classObj.getInt("id")
                val className = classObj.getString("name")
                
                val subjectsList = mutableListOf<Subject>()
                val subjectsArray = classObj.optJSONArray("subjects") ?: JSONArray()
                
                for (j in 0 until subjectsArray.length()) {
                    val subObj = subjectsArray.getJSONObject(j)
                    val subName = subObj.getString("name")
                    val subIcon = subObj.getString("icon")
                    
                    val unitsList = mutableListOf<ContentUnit>()
                    val unitsArray = subObj.optJSONArray("units") ?: JSONArray()
                    
                    for (k in 0 until unitsArray.length()) {
                        val unitObj = unitsArray.getJSONObject(k)
                        val uId = unitObj.getString("id")
                        val uTitle = unitObj.getString("title")
                        val uExpl = unitObj.getString("explanation")
                        val uVid = unitObj.getString("videoUrl")
                        
                        val notesList = mutableListOf<String>()
                        val notesArray = unitObj.optJSONArray("notes") ?: JSONArray()
                        for (n in 0 until notesArray.length()) notesList.add(notesArray.getString(n))
                        
                        val examplesList = mutableListOf<String>()
                        val exArray = unitObj.optJSONArray("examples") ?: JSONArray()
                        for (e in 0 until exArray.length()) examplesList.add(exArray.getString(e))
                        
                        val quizList = mutableListOf<QuizQuestion>()
                        val qzArray = unitObj.optJSONArray("quiz") ?: JSONArray()
                        for (q in 0 until qzArray.length()) {
                            val qObj = qzArray.getJSONObject(q)
                            val qText = qObj.getString("question")
                            val cIdx = qObj.getInt("correctIndex")
                            val optArray = qObj.getJSONArray("options")
                            val opList = mutableListOf<String>()
                            for (o in 0 until optArray.length()) opList.add(optArray.getString(o))
                            quizList.add(QuizQuestion(qText, opList, cIdx))
                        }
                        
                        unitsList.add(ContentUnit(uId, uTitle, uExpl, notesList, examplesList, uVid, quizList))
                    }
                    subjectsList.add(Subject(subName, subIcon, unitsList))
                }
                classesList.add(ClassData(classId, className, subjectsList))
            }
            cachedData = classesList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return cachedData ?: emptyList()
    }
}