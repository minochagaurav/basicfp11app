package com.fp.devfantasypowerxi.app.view.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.databinding.ActivityWebviewBinding

class WebActivity : AppCompatActivity() {
   lateinit var mainBinding: ActivityWebviewBinding
    var title: String = ""
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview)
        mainBinding.webView.loadUrl(WEB_URL + intent.getStringExtra("type"))
        Log.e("final url:", WEB_URL + intent.getStringExtra("type"))
        title = intent.extras!!.getString("title")!!
        mainBinding.webView.settings.javaScriptEnabled = true
        initialize()
        mainBinding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
               // view.loadUrl(url)
                mainBinding.refreshing = true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                mainBinding.refreshing = false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initialize() {
        setSupportActionBar(mainBinding.mytoolbar)
        if (supportActionBar != null) {
            supportActionBar!!.title = title
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    companion object {
        const val WEB_URL = "https://fantasypower11.com/"
    }
}