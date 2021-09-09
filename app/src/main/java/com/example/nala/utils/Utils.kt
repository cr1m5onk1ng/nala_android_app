package com.example.nala.utils

import android.util.Log
import android.webkit.URLUtil
import com.gargoylesoftware.htmlunit.util.UrlUtils

object Utils {

    fun parseInputString(input: String) : InputStringType {
        if((input.startsWith("https://youtu.be/") || input.startsWith("https://www.youtube.com/"))) {
            return InputStringType.YoutubeUrl
        } else if(URLUtil.isValidUrl(input)) {
            return InputStringType.ArticleUrl
        }
        else {
            return InputStringType.Sentence
        }
    }

    fun parseVideoIdFromUrl(url: String) : String {
        return url.split("//", "/").last()
    }

    fun parseDomainFromUrl(url: String) : String {
        val parts = url.split("://").last()
        var domain = parts.split("/").first()
        if(domain.contains("www.")){
            domain = domain.split("www.").last()
        }
        Log.d("URLDEBUG", "Domain is: $domain")
        return domain
    }
}