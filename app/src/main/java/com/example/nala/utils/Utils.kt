package com.example.nala.utils

import android.webkit.URLUtil

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
}