package com.tannu.edureach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.data.model.RecentUploadModel
import java.text.SimpleDateFormat
import java.util.*

class RecentUploadsAdapter(
    private var items: List<RecentUploadModel>,
    private val onClick: (RecentUploadModel) -> Unit
) : RecyclerView.Adapter<RecentUploadsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecentTitle: TextView = view.findViewById(R.id.tvRecentTitle)
        val tvRecentPath: TextView = view.findViewById(R.id.tvRecentPath)
        val tvRecentDate: TextView = view.findViewById(R.id.tvRecentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_upload, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvRecentTitle.text = "${item.type}: ${item.title}"
        
        val classFriendly = item.classId.replace("_", " ").replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
        val subjectFriendly = item.subjectId.replace("_", " ").replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
        val unitFriendly = item.unitId.replace("_", " ").replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
        
        holder.tvRecentPath.text = "$classFriendly | $subjectFriendly | $unitFriendly"
        
        val formatter = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault())
        holder.tvRecentDate.text = formatter.format(Date(item.timestamp))

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<RecentUploadModel>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}