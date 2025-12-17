package dev.boni.techhelpdesk.data.local

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "theme_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_THEME = "selected_theme"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    /**
     * Guarda la preferencia de tema
     * @param theme "light", "dark", o "system"
     */
    fun setTheme(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
    }

    /**
     * Obtiene la preferencia de tema guardada
     * @return "light", "dark", o "system" (por defecto "system")
     */
    fun getTheme(): String {
        return prefs.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
    }
}