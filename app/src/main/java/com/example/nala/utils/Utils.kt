package com.example.nala.utils

import android.util.Log
import android.webkit.URLUtil
import com.example.nala.utils.types.InputStringType

object Utils {

    fun isUrl(text: String) : Boolean {
        return URLUtil.isValidUrl(text)
    }

    fun parseInputString(input: String) : InputStringType {
        return if((input.startsWith("https://youtu.be/") ||
                    input.startsWith("https://www.youtube.com/"))
        ) {
            InputStringType.YoutubeUrl
        } else if(URLUtil.isValidUrl(input)) {
            InputStringType.ArticleUrl
        } else {
            InputStringType.Sentence
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

    fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    fun cleanRecognizedTextForJapanese(text: String): String {
        return text.replace(" ", "").replace("\n", "")
    }
}