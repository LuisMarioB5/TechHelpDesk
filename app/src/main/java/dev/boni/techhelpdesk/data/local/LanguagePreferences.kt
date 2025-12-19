package dev.boni.techhelpdesk.data.local

import android.content.Context
import android.content.SharedPreferences

class LanguagePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LANGUAGE = "app_language"
        const val DEFAULT_LANGUAGE = "system" // 'es', 'en' o 'system'
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
}