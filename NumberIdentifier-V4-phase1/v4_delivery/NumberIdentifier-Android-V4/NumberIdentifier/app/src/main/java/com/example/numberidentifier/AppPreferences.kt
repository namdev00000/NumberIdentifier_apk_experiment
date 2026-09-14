package com.example.numberidentifier

import android.content.Context

/**
 * Centralized SharedPreferences access for Number Identifier V4.
 * Every screen reads/writes through here so settings stay consistent app-wide.
 */
object AppPreferences {
    private const val PREFS_NAME = "number_identifier_prefs"

    private const val KEY_THEME = "theme_choice"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_ASK_NAME_EVERY_TIME = "ask_name_every_time"
    private const val KEY_USER_NAME = "user_name"

    enum class ThemeChoice { SYSTEM, LIGHT, DARK }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadThemeChoice(context: Context): ThemeChoice {
        val value = prefs(context).getInt(KEY_THEME, ThemeChoice.SYSTEM.ordinal)
        return ThemeChoice.values().getOrElse(value) { ThemeChoice.SYSTEM }
    }

    fun saveThemeChoice(context: Context, choice: ThemeChoice) {
        prefs(context).edit().putInt(KEY_THEME, choice.ordinal).apply()
    }

    fun isSoundEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SOUND_ENABLED, true)

    fun setSoundEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    /** false (default): set your name once and it's remembered. true: asked again every open. */
    fun isAskNameEveryTime(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ASK_NAME_EVERY_TIME, false)

    fun setAskNameEveryTime(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ASK_NAME_EVERY_TIME, enabled).apply()
    }

    fun getUserName(context: Context): String? {
        val name = prefs(context).getString(KEY_USER_NAME, null)
        return if (name.isNullOrBlank()) null else name
    }

    fun setUserName(context: Context, name: String?) {
        prefs(context).edit().putString(KEY_USER_NAME, name?.trim()).apply()
    }
}
