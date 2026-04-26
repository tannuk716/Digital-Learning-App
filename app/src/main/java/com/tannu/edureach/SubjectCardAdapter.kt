package com.tannu.edureach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R
import com.tannu.edureach.data.model.Subject

class SubjectCardAdapter(
    private val subjects: List<Subject>,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectCardAdapter.ViewHolder>() {

    private val colorGradients = listOf(
        R.drawable.bg_gradient_blue,
        R.drawable.bg_gradient_orange,
        R.drawable.bg_gradient_green,
        R.drawable.bg_gradient_light_green,
        R.drawable.bg_gradient_purple,
        R.drawable.bg_gradient_dark_blue
    )

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootLayout: View = view
        val tvSubjectName: TextView = view.findViewById(R.id.tvSubjectName)
        val tvSubjectIcon: TextView = view.findViewById(R.id.tvSubjectIcon)
        val tvSubjectDesc: View = view.findViewById(R.id.tvSubjectDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subject = subjects[position]
        
        holder.tvSubjectName.text = subject.name
        holder.tvSubjectIcon.text = subject.icon ?: "📚"

        val bgRes = colorGradients[position % colorGradients.size]
        holder.rootLayout.setBackgroundResource(bgRes)

        holder.rootLayout.setOnClickListener {
            onSubjectClick(subject)
        }
        

        holder.rootLayout.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(150).start()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
                }
            }
            false
        }
    }

    override fun getItemCount() = subjects.size
}