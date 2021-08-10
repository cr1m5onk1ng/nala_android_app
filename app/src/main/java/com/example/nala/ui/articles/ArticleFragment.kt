package com.example.nala.ui.articles

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.nala.R

class ArticleFragment : Fragment(R.layout.article_view) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<WebView>(R.id.articleView).apply{
            webViewClient = WebViewClient()
            loadUrl("PASS URL HERE")
        }

    }
}