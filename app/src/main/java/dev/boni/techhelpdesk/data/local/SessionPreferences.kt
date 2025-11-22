package dev.boni.techhelpdesk.data.local

import android.content.Context

class SessionPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun setRememberMe(remember: Boolean) {
        prefs.edit().putBoolean("remember_me", remember).apply()
    }

    fun shouldRememberMe(): Boolean {
        return prefs.getBoolean("remember_me", true)
    }
}