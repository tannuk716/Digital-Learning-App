package com.tannu.edureach.learn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.R
import com.tannu.edureach.data.OfflineDataLoader

class UnitListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_list)
        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        val subjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"
        
        val tvSubjectTitle = findViewById<TextView>(R.id.tvSubjectTitle)
        tvSubjectTitle.text = "$subjectName Units"

        val rvUnits = findViewById<RecyclerView>(R.id.rvUnits)
        rvUnits.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userClass = prefs.getInt("USER_CLASS", 1)

        val classes = OfflineDataLoader.loadOfflineData(this)
        val currentClassObj = classes.find { it.id == userClass }
        val subjects = currentClassObj?.subjects ?: classes.find { it.id == 1 }?.subjects ?: emptyList()
        val currentSubject = subjects.find { it.name == subjectName }
        
        if (currentSubject != null) {
            rvUnits.adapter = UnitAdapter(currentSubject.units) { unit ->
                val intent = Intent(this@UnitListActivity, LearnContentActivity::class.java)

                intent.putExtra("CLASS_ID", userClass)
                intent.putExtra("SUBJECT_NAME", subjectName)
                intent.putExtra("UNIT_ID", unit.id)
                startActivity(intent)
            }
        }
    }

    inner class UnitAdapter(
        private val units: List<OfflineDataLoader.ContentUnit>,
        private val onClick: (OfflineDataLoader.ContentUnit) -> Unit
    ) : RecyclerView.Adapter<UnitAdapter.UnitViewHolder>() {

        inner class UnitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val title: TextView = view.findViewById(R.id.tvUnitName)
            val btnDownload: ImageView
            init {

                btnDownload = view.findViewById(R.id.btnDownload) ?: ImageView(view.context)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_unit, parent, false)
            return UnitViewHolder(view)
        }

        override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
            val unit = units[position]
            holder.title.text = unit.title
            holder.itemView.setOnClickListener { onClick(unit) }
            

            holder.btnDownload.setOnClickListener {
                android.widget.Toast.makeText(holder.itemView.context, "Downloading ${unit.title} for offline access...", android.widget.Toast.LENGTH_SHORT).show()
                holder.btnDownload.setImageResource(android.R.drawable.stat_sys_download_done)
            }
        }

        override fun getItemCount() = units.size
    }
}