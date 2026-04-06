package com.tannu.edureach.utils

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tannu.edureach.R

class EducationalWebActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var pbWebLoading: ProgressBar
    private lateinit var tvWebTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_educational_web)

        webView = findViewById(R.id.webView)
        pbWebLoading = findViewById(R.id.pbWebLoading)
        tvWebTitle = findViewById(R.id.tvWebTitle)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { 
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish() 
            }
        }

        var url = intent.getStringExtra("WEB_URL") ?: "https://en.wikipedia.org/wiki/Education"
        val title = intent.getStringExtra("WEB_TITLE") ?: "Educational Resource"

        android.util.Log.d("EducationalWeb", "========================================")
        android.util.Log.d("EducationalWeb", "Loading: $title")
        android.util.Log.d("EducationalWeb", "Original URL: $url")

        // Convert Google Drive URLs to direct download format first
        if (GoogleDriveUrlHelper.isGoogleDriveUrl(url)) {
            val convertedUrl = GoogleDriveUrlHelper.convertToDirectUrl(url)
            android.util.Log.d("EducationalWeb", "Converted URL: $convertedUrl")
            url = convertedUrl
        }

        // If the URL is a direct PDF link, use Google Drive's preview mode instead of download
        if (url.lowercase().endsWith(".pdf") || url.contains(".pdf?alt=media", ignoreCase = true) || 
            url.contains("drive.google.com/uc?export=download", ignoreCase = true)) {
            
            // Extract file ID from Google Drive URL
            val fileId = GoogleDriveUrlHelper.extractFileId(url)
            if (fileId != null) {
                // Use Google Drive's built-in preview instead of download
                url = "https://drive.google.com/file/d/$fileId/preview"
                android.util.Log.d("EducationalWeb", "Using Google Drive preview: $url")
            } else {
                // Fallback to Google Docs viewer
                url = "https://docs.google.com/gview?embedded=true&url=$url"
                android.util.Log.d("EducationalWeb", "Using Google Docs viewer: $url")
            }
        }

        android.util.Log.d("EducationalWeb", "Final URL: $url")
        android.util.Log.d("EducationalWeb", "========================================")

        // Update learning streak
        com.tannu.edureach.utils.ProgressManager.updateStreak()

        tvWebTitle.text = title
        setupWebView()
        webView.loadUrl(url)
    }

    private fun setupWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        
        // Enable caching for better performance
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                pbWebLoading.visibility = android.view.View.VISIBLE
                android.util.Log.d("EducationalWeb", "Page started loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pbWebLoading.visibility = android.view.View.GONE
                android.util.Log.d("EducationalWeb", "Page finished loading: $url")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                android.util.Log.d("EducationalWeb", "Navigating to: $url")
                view?.loadUrl(url)
                return true
            }
            
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                android.util.Log.e("EducationalWeb", "Error loading page: $description (Code: $errorCode)")
                android.util.Log.e("EducationalWeb", "Failing URL: $failingUrl")
            }
        }

        webView.webChromeClient = WebChromeClient()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
