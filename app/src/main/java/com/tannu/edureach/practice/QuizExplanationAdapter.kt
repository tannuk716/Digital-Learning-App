package com.tannu.edureach.practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R

class QuizExplanationAdapter(
    private val correctAnswers: List<String>,
    private val explanations: List<String>
) : RecyclerView.Adapter<QuizExplanationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestionNumber: TextView = view.findViewById(R.id.tvQuestionNumber)
        val tvCorrectAnswer: TextView = view.findViewById(R.id.tvCorrectAnswer)
        val tvExplanation: TextView = view.findViewById(R.id.tvExplanation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz_explanation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvQuestionNumber.text = "Question ${position + 1}"
        holder.tvCorrectAnswer.text = "Correct Answer: ${correctAnswers[position]}"
        holder.tvExplanation.text = "Explanation: ${explanations[position]}"
    }

    override fun getItemCount() = correctAnswers.size
}