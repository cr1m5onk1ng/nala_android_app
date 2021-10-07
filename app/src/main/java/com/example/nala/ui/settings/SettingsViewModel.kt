package com.example.nala.ui.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    val targetLangs: MutableState<Set<String>> = mutableStateOf(setOf())

    private fun enableLangInSettings(lang: String) {
        val editor = preferences.edit()
        editor.apply{
            putBoolean(lang, true)
            apply()
        }
    }

    private fun disableLangInSettings(lang: String) {
        val editor = preferences.edit()
        editor.apply{
            putBoolean(lang, false)
            apply()
        }
    }

    private fun addLangToAvailableLangs(lang: String) {
        val editor = preferences.edit()
        val updatedLangs = mutableSetOf(*targetLangs.value.toTypedArray())
        updatedLangs.add(lang)
        Log.d("SETTINGSDEBUG", "Target langs from settings: $updatedLangs")
        targetLangs.value = updatedLangs
        editor.apply{
            putStringSet("target_langs", targetLangs.value)
            apply()
        }
    }

    fun loadSharedPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            //val langsSet = mutableSetOf<String>()
            isJapaneseSelected.value = preferences.getBoolean("ja", false)
            //if(isJapaneseSelected.value) langsSet.add("ja")
            isEnglishSelected.value = preferences.getBoolean("en", false)
            //if(isEnglishSelected.value) langsSet.add("en")
            isFrenchSelected.value = preferences.getBoolean("fr", false)
            //if(isFrenchSelected.value) langsSet.add("fr")
            isSpanishSelected.value = preferences.getBoolean("es", false)
            //if(isSpanishSelected.value) langsSet.add("es")
            targetLangs.value = preferences.getStringSet("target_langs", setOf())?.toSet() ?: setOf()
        }
    }

    fun setLangSelected(lang: String, value: Boolean) {
        if(value){
            enableLangInSettings(lang)
            addLangToAvailableLangs(lang)
        } else {
            disableLangInSettings(lang)
        }
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