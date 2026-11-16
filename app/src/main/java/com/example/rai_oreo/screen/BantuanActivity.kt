/**
 * Penerapan praktikum: Modul 5 (WebView memuat aset lokal assets/bantuan.html).
 */
package com.example.rai_oreo.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.rai_oreo.databinding.ActivityBantuanBinding

@Suppress("SetJavaScriptEnabled")
class BantuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBantuanBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBantuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        with(binding.webView.settings) {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progress.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progress.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false
        }

        binding.webView.loadUrl("file:///android_asset/bantuan.html")
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }
}
