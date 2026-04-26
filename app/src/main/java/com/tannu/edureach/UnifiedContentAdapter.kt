package com.tannu.edureach

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.ui.viewmodel.UnifiedContent

class UnifiedContentAdapter(
    private var items: List<UnifiedContent>,
    private val onOpenClick: (UnifiedContent) -> Unit,
    private val onDownloadClick: (UnifiedContent) -> Unit
) : RecyclerView.Adapter<UnifiedContentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llHeaderBg: LinearLayout = view.findViewById(R.id.llHeaderBg)
        val tvContentIcon: TextView = view.findViewById(R.id.tvContentIcon)
        val tvContentTitle: TextView = view.findViewById(R.id.tvContentTitle)
        val tvContentType: TextView = view.findViewById(R.id.tvContentType)
        val btnOpen: Button = view.findViewById(R.id.btnOpen)
        val btnDownload: Button = view.findViewById(R.id.btnDownload)
        val llActionArea: LinearLayout = view.findViewById(R.id.llActionArea)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unified_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvContentTitle.text = item.title
        holder.tvContentType.text = item.type

        when (item.type) {
            "Video" -> {
                holder.tvContentIcon.visibility = View.VISIBLE
                holder.tvContentType.visibility = View.VISIBLE
                holder.llActionArea.visibility = View.VISIBLE
                holder.tvContentIcon.text = "🎥"
                holder.llHeaderBg.setBackgroundColor(Color.parseColor("#FFF3E0"))
                holder.tvContentTitle.setTextColor(Color.parseColor("#E65100"))
                holder.tvContentType.setTextColor(Color.parseColor("#EF6C00"))
                holder.btnDownload.visibility = View.VISIBLE
                holder.btnOpen.text = "Play Video ▶️"
            }
            "Note" -> {
                holder.tvContentIcon.visibility = View.VISIBLE
                holder.tvContentType.visibility = View.VISIBLE
                holder.llActionArea.visibility = View.VISIBLE
                holder.tvContentIcon.text = "📝"
                holder.llHeaderBg.setBackgroundColor(Color.parseColor("#E0F7FA"))
                holder.tvContentTitle.setTextColor(Color.parseColor("#006064"))
                holder.tvContentType.setTextColor(Color.parseColor("#00838F"))
                holder.btnDownload.visibility = View.VISIBLE
                holder.btnOpen.text = "Read Note 📖"
            }
            "Quiz" -> {
                holder.tvContentIcon.visibility = View.VISIBLE
                holder.tvContentType.visibility = View.VISIBLE
                holder.llActionArea.visibility = View.VISIBLE
                holder.tvContentIcon.text = "❓"
                holder.llHeaderBg.setBackgroundColor(Color.parseColor("#F3E5F5"))
                holder.tvContentTitle.setTextColor(Color.parseColor("#4A148C"))
                holder.tvContentType.setTextColor(Color.parseColor("#6A1B9A"))
                holder.btnDownload.visibility = View.GONE
                holder.btnOpen.text = "Take Quiz 🎯"
            }
            "Header" -> {
                holder.tvContentIcon.visibility = View.GONE
                holder.tvContentType.visibility = View.GONE
                holder.llActionArea.visibility = View.GONE
                holder.llHeaderBg.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.tvContentTitle.setTextColor(Color.WHITE)
                holder.tvContentTitle.textSize = 20f
            }
        }

        holder.btnOpen.setOnClickListener { onOpenClick(item) }
        holder.btnDownload.setOnClickListener { onDownloadClick(item) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<UnifiedContent>) {
        items = newItems
        notifyDataSetChanged()
    }
}