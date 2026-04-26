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
import com.tannu.edureach.utils.Class10ContentProvider
import com.tannu.edureach.utils.DownloadHelper

class Class10UnitListActivity : AppCompatActivity() {

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
        android.util.Log.d("Class10Unit", "========================================")
        android.util.Log.d("Class10Unit", "loadUnits called with subjectId: '$subjectId'")
        
        if (subjectId.isEmpty()) {
            android.util.Log.e("Class10Unit", "❌ ERROR: subjectId is empty!")
            rvUnits.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "Error: No subject selected"
            return
        }
        
        val subjectContent = Class10ContentProvider.getSubjectContent(this, subjectId)

        if (subjectContent != null && subjectContent.units.isNotEmpty()) {
            android.util.Log.d("Class10Unit", "✅ Loaded ${subjectContent.units.size} units for ${subjectContent.subjectName}")
            
            val adapter = UnitAdapter(
                subjectContent.units,
                onUnitClick = { unit -> 
                    android.util.Log.d("Class10Unit", "Unit clicked: ${unit.unitName}")
                    android.util.Log.d("Class10Unit", "PDF URL: ${unit.pdfUrl}")
                    openPdf(unit.pdfUrl, unit.unitName) 
                },
                onDownloadClick = { unit -> 
                    android.util.Log.d("Class10Unit", "Download clicked: ${unit.unitName}")
                    downloadPdf(unit.pdfUrl, unit.unitName) 
                }
            )
            rvUnits.adapter = adapter
            rvUnits.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        } else {
            android.util.Log.e("Class10Unit", "❌ No content found for subjectId: '$subjectId'")
            rvUnits.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No units available for this subject."
        }
        android.util.Log.d("Class10Unit", "========================================")
    }
    
    private fun downloadPdf(pdfUrl: String, unitName: String) {
        if (pdfUrl.isEmpty()) {
            Toast.makeText(this, "PDF URL not available", Toast.LENGTH_SHORT).show()
            return
        }
        DownloadHelper.deleteOldDownloads(this, unitName, isVideo = false)
        DownloadHelper.downloadContent(this, pdfUrl, unitName, isVideo = false, subjectName = currentSubjectName)
    }

    private fun openPdf(pdfUrl: String, unitName: String) {
        android.util.Log.d("Class10Unit", "========================================")
        android.util.Log.d("Class10Unit", "Opening PDF: $unitName")
        android.util.Log.d("Class10Unit", "Subject: $currentSubjectName")
        android.util.Log.d("Class10Unit", "URL: $pdfUrl")
        android.util.Log.d("Class10Unit", "========================================")
        
        val localUri = DownloadHelper.getLocalFileUri(this, unitName, false, subjectName = currentSubjectName)
        if (localUri != null) {
            android.util.Log.d("Class10Unit", "✅ Found correct local file with subject prefix, opening offline")
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(localUri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(intent)
                return
            } catch (e: Exception) {
                android.util.Log.e("Class10Unit", "Error opening downloaded file", e)
                Toast.makeText(this, "Unable to open downloaded file. Opening online...", Toast.LENGTH_SHORT).show()
            }
        } else {
            android.util.Log.d("Class10Unit", "No local file found (or old format), opening online")
        }
        
        android.util.Log.d("Class10Unit", "Opening online via EducationalWebActivity")
        try {
            val webIntent = Intent(this, com.tannu.edureach.utils.EducationalWebActivity::class.java)
            webIntent.putExtra("WEB_URL", pdfUrl)
            webIntent.putExtra("WEB_TITLE", unitName)
            startActivity(webIntent)
        } catch (ex: Exception) {
            android.util.Log.e("Class10Unit", "Error opening PDF", ex)
            Toast.makeText(this, "Unable to open PDF: ${ex.message}", Toast.LENGTH_LONG).show()
        }
    }

    private class UnitAdapter(
        private val units: List<Class10ContentProvider.UnitContent>,
        private val onUnitClick: (Class10ContentProvider.UnitContent) -> Unit,
        private val onDownloadClick: (Class10ContentProvider.UnitContent) -> Unit
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
            
            android.util.Log.d("UnitAdapter", "Binding position=$position, unit=${unit.unitName}")
            
            holder.tvUnitName.text = unit.unitName
            holder.tvUnitIcon.text = "📄"

            holder.itemView.setOnClickListener {
                android.util.Log.d("UnitAdapter", "Item clicked: ${unit.unitName} at position=$position")
                onUnitClick(unit)
            }
            
            holder.btnDownload.setOnClickListener {
                android.util.Log.d("UnitAdapter", "Download clicked: ${unit.unitName} at position=$position")
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