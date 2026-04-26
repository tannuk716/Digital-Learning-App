package com.tannu.edureach

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: List<ChatMessage>,
    private val onRetry: (String) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llSender: LinearLayout = itemView.findViewById(R.id.llSender)
        val tvSenderMessage: TextView = itemView.findViewById(R.id.tvSenderMessage)
        val llReceiver: LinearLayout = itemView.findViewById(R.id.llReceiver)
        val tvReceiverMessage: TextView = itemView.findViewById(R.id.tvReceiverMessage)
        val llLoading: LinearLayout = itemView.findViewById(R.id.llLoading)
        val llError: LinearLayout = itemView.findViewById(R.id.llError)
        val tvErrorMessage: TextView = itemView.findViewById(R.id.tvErrorMessage)
        val btnRetry: android.widget.Button = itemView.findViewById(R.id.btnRetry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val messageItem = messages[position]

        holder.llSender.visibility = View.GONE
        holder.llReceiver.visibility = View.GONE
        holder.llLoading.visibility = View.GONE
        holder.llError.visibility = View.GONE

        if (messageItem.isLoading) {
            holder.llLoading.visibility = View.VISIBLE
        } else if (messageItem.isError) {
            holder.llError.visibility = View.VISIBLE
            holder.tvErrorMessage.text = messageItem.message
            holder.btnRetry.setOnClickListener {
                onRetry(messageItem.originalQuery)
            }
        } else if (messageItem.isSender) {
            holder.llSender.visibility = View.VISIBLE
            holder.tvSenderMessage.text = messageItem.message
        } else {
            holder.llReceiver.visibility = View.VISIBLE
            holder.tvReceiverMessage.text = messageItem.message
        }
    }

    override fun getItemCount() = messages.size
}