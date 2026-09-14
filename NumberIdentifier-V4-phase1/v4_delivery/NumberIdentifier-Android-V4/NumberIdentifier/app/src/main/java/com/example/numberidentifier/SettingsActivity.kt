package com.example.numberidentifier

import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class SettingsActivity : BaseActivity() {

    override fun screenTitle() = "Settings"

    override fun buildContent(container: LinearLayout) {
        container.addView(
            settingRow(
                title = "Sound effects",
                subtitle = "Play a short sound when you tap buttons.",
                initialValue = AppPreferences.isSoundEnabled(this)
            ) { enabled -> AppPreferences.setSoundEnabled(this, enabled) },
            wrap().apply { bottomMargin = dp(14) }
        )

        container.addView(
            settingRow(
                title = "Ask my name every time",
                subtitle = "Off: set your name once, it's remembered. On: asked again each time you open the app.",
                initialValue = AppPreferences.isAskNameEveryTime(this)
            ) { enabled -> AppPreferences.setAskNameEveryTime(this, enabled) },
            wrap().apply { bottomMargin = dp(20) }
        )

        val nameCard = card().apply { setPadding(dp(16), dp(16), dp(16), dp(16)) }
        nameCard.addView(TextView(this).apply {
            text = "Your name"
            textSize = 15f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(theme.text)
        }, wrap())

        val nameInput = EditText(this).apply {
            hint = "Enter your name"
            setText(AppPreferences.getUserName(this@SettingsActivity) ?: "")
            textSize = 16f
            setTextColor(theme.text)
            setHintTextColor(theme.muted)
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
            setPadding(dp(12), 0, dp(12), 0)
            background = rounded(theme.surfaceAlt, 14, theme.line, dp(1))
        }
        nameCard.addView(nameInput, LinearLayout.LayoutParams(-1, dp(50)).apply { topMargin = dp(10) })

        val saveButton = TextView(this).apply {
            text = "Save name"
            textSize = 15f
            gravity = Gravity.CENTER
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.WHITE)
            background = rounded(Theme.ACCENT_SETTINGS, 14)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                sound.playClick()
                val name = nameInput.text.toString().trim()
                AppPreferences.setUserName(this@SettingsActivity, if (name.isEmpty()) null else name)
            }
        }
        nameCard.addView(saveButton, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(14) })

        container.addView(nameCard, wrap())
    }

    private fun settingRow(
        title: String,
        subtitle: String,
        initialValue: Boolean,
        onToggle: (Boolean) -> Unit
    ): LinearLayout {
        val row = card()
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(dp(16), dp(14), dp(16), dp(14))

        val textBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        textBox.addView(TextView(this).apply {
            text = title
            textSize = 15f
            setTextColor(theme.text)
        }, wrap())
        textBox.addView(TextView(this).apply {
            text = subtitle
            textSize = 12f
            setTextColor(theme.muted)
            setPadding(0, dp(4), 0, 0)
        }, wrap())
        row.addView(textBox, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val switch = Switch(this).apply {
            isChecked = initialValue
            setOnCheckedChangeListener { _, checked -> sound.playClick(); onToggle(checked) }
        }
        row.addView(
            switch,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { leftMargin = dp(10) }
        )
        return row
    }
}
