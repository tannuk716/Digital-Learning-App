package com.tannu.edureach

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tannu.edureach.utils.Class3ContentProvider
import com.tannu.edureach.utils.DownloadHelper

class Class3UnitListActivity : AppCompatActivity() {

    private lateinit var rvUnits: RecyclerView
    private lateinit var tvSubjectTitle: TextView
    private lateinit var tvEmptyState: TextView
    
    private var currentSubjectName: String = ""
    
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_list)

        val subjectId = intent.getStringExtra("SUBJECT_ID") ?: ""
        currentSubjectName = intent.getStringExtra("SUBJECT_NAME") ?: "Subject"

        rvUnits = findViewById(R.id.rvUnits)
        tvSubjectTitle = findViewById(R.id.tvSubjectTitle)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        tvSubjectTitle.text = "$currentSubjectName - Units"

        findViewById<View>(R.id.btnBack)?.setOnClickListener { finish() }

        rvUnits.layoutManager = LinearLayoutManager(this)

        checkStoragePermission()
        
        loadUnits(subjectId)
    }
    
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage permission denied. Downloads may not work.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadUnits(subjectId: String) {
        val subjectContent = Class3ContentProvider.getSubjectContent(this, subjectId)

        if (subjectContent != null && subjectContent.units.isNotEmpty()) {
            val adapter = UnitAdapter(
                subjectContent.units,
                onUnitClick = { unit -> openPdf(unit.pdfUrl, unit.unitName) },
                onDownloadClick = { unit -> downloadPdf(unit.pdfUrl, unit.unitName) }
            )
            rvUnits.adapter = adapter
            rvUnits.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        } else {
            rvUnits.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No units available for this subject."
        }
    }
    
    private fun downloadPdf(pdfUrl: String, unitName: String) {
        if (pdfUrl.isEmpty()) {
            Toast.makeText(this, "PDF URL not available", Toast.LENGTH_SHORT).show()
            return
        }
        
        DownloadHelper.downloadContent(this, pdfUrl, unitName, isVideo = false, subjectName = currentSubjectName)
    }

    private fun openPdf(pdfUrl: String, unitName: String) {

        val localUri = DownloadHelper.getLocalFileUri(this, unitName, false, subjectName = currentSubjectName)
        if (localUri != null) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(localUri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
                return
            } catch (e: Exception) {
                android.util.Log.e("Class3Unit", "Error opening downloaded file", e)
                Toast.makeText(this, "Unable to open downloaded file. Trying fallback...", Toast.LENGTH_SHORT).show()
            }
        }
        

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(pdfUrl)
            startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(this, "Unable to open Google Drive Link. No browser found.", Toast.LENGTH_LONG).show()
        }
    }

    private class UnitAdapter(
        private val units: List<Class3ContentProvider.UnitContent>,
        private val onUnitClick: (Class3ContentProvider.UnitContent) -> Unit,
        private val onDownloadClick: (Class3ContentProvider.UnitContent) -> Unit
    ) : RecyclerView.Adapter<UnitAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvUnitName: TextView = view.findViewById(R.id.tvUnitName)
            val tvUnitIcon: TextView = view.findViewById(R.id.tvUnitIcon)
            val btnDownload: ImageView = view.findViewById(R.id.btnDownload)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_unit, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val unit = units[position]
            holder.tvUnitName.text = unit.unitName
            holder.tvUnitIcon.text = "📄"

            holder.itemView.setOnClickListener {
                onUnitClick(unit)
            }
            
            holder.btnDownload.setOnClickListener {
                onDownloadClick(unit)
            }

            holder.itemView.setOnTouchListener { view, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }
                }
                false
            }
        }

        override fun getItemCount() = units.size
    }
}