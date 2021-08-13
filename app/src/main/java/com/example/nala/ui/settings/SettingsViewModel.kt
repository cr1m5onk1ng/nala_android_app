package com.example.nala.ui.settings

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {
    val isJapaneseSelected = mutableStateOf(false)
    val isEnglishSelected = mutableStateOf(false)
    val isFrenchSelected = mutableStateOf(false)
    val isSpanishSelected = mutableStateOf(false)

    private val preferences = context.getSharedPreferences("langs", Context.MODE_PRIVATE)

    val targetLangs: MutableState<List<String>> = mutableStateOf(listOf())

    fun enableLangInSettings(lang: String) {
        val editor = preferences.edit()
        editor.apply{
            putBoolean(lang, true)
            apply()
        }
    }

    fun loadSharedPreferences() {
        isJapaneseSelected.value = preferences.getBoolean("ja", false)
        isEnglishSelected.value = preferences.getBoolean("en", false)
        isFrenchSelected.value = preferences.getBoolean("fr", false)
        isSpanishSelected.value = preferences.getBoolean("es", false)
    }

    fun setLangSelected(lang: String, value: Boolean) {
        if(value) enableLangInSettings(lang)
        when(lang) {
            "ja" -> {
                isJapaneseSelected.value = value
            }
            "en" -> {
                isEnglishSelected.value = value
            }
            "fr" ->{
                isFrenchSelected.value = value
            }
            "es" -> {
                isSpanishSelected.value = value
            }
        }
    }
}