package com.tannu.edureach

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class NotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        val rvNotes = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvNotes)
        rvNotes.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        

        val notes = listOf(
            "Unit 1: The Alphabet (A-Z)",
            "Unit 2: Phonics Basics",
            "Unit 3: Numbers 1 to 100",
            "Unit 4: Simple Addition",
            "Unit 5: Human Body Parts",
            "Unit 6: Animals and Birds"
        )

        rvNotes.adapter = NotesAdapter(notes)
    }

    private class NotesAdapter(private val data: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
        class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = data[position]
            holder.itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Downloading notes for: ${data[position]}...", Toast.LENGTH_SHORT).show()
            }
        }
        override fun getItemCount() = data.size
    }
}