package com.example.numberidentifier

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color

/**
 * Colorful palette shared by every V4 screen, so the whole app looks
 * consistent instead of each feature inventing its own colors.
 */
data class Theme(
    val isDark: Boolean,
    val bg: Int,
    val surface: Int,
    val surfaceAlt: Int,
    val text: Int,
    val muted: Int,
    val line: Int
) {
    companion object {
        // One accent color per main feature — same shade in light and dark mode.
        const val ACCENT_CHECK = 0xFF5B6EF5.toInt()      // indigo  — Check the Number
        const val ACCENT_FIND = 0xFF12B886.toInt()       // teal    — Find the Number
        const val ACCENT_CALCULATOR = 0xFFFF922B.toInt() // orange  — Calculator
        const val ACCENT_CURRENCY = 0xFFF0A202.toInt()   // gold    — Currency Calculator
        const val ACCENT_UNIT = 0xFF8B5CF6.toInt()       // purple  — Unit Conversion
        const val ACCENT_AGE = 0xFFF06595.toInt()        // pink    — Age Calculator
        const val ACCENT_INFO = 0xFF22B8CF.toInt()       // cyan    — Information Chart
        const val ACCENT_SETTINGS = 0xFF868E96.toInt()   // slate   — Settings

        fun systemIsDark(context: Context): Boolean =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        fun resolve(context: Context): Theme {
            val choice = AppPreferences.loadThemeChoice(context)
            val dark = when (choice) {
                AppPreferences.ThemeChoice.DARK -> true
                AppPreferences.ThemeChoice.LIGHT -> false
                AppPreferences.ThemeChoice.SYSTEM -> systemIsDark(context)
            }
            return if (dark) {
                Theme(
                    isDark = true,
                    bg = Color.rgb(18, 18, 24),
                    surface = Color.rgb(28, 28, 36),
                    surfaceAlt = Color.rgb(36, 36, 46),
                    text = Color.WHITE,
                    muted = Color.rgb(178, 178, 190),
                    line = Color.rgb(54, 54, 66)
                )
            } else {
                Theme(
                    isDark = false,
                    bg = Color.rgb(248, 249, 252),
                    surface = Color.WHITE,
                    surfaceAlt = Color.rgb(241, 243, 248),
                    text = Color.rgb(24, 24, 32),
                    muted = Color.rgb(104, 104, 118),
                    line = Color.rgb(228, 230, 238)
                )
            }
        }
    }
}
