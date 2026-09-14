package com.example.numberidentifier

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class MainActivity : BaseActivity() {

    private data class HomeFeature(
        val icon: String,
        val title: String,
        val subtitle: String,
        val accent: Int,
        val target: Class<*>
    )

    private lateinit var welcomeTitle: TextView

    override fun screenTitle() = "Number Identifier"
    override fun showBackButton() = false
    override fun showThemeToggle() = true
    override fun topBarTrailingAction() = "⚙" to {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showWelcomeFlow()
    }

    override fun buildContent(container: LinearLayout) {
        val welcome = card().apply { setPadding(dp(20), dp(20), dp(20), dp(20)) }
        welcomeTitle = TextView(this).apply {
            text = "Hello!"
            textSize = 24f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(theme.text)
        }
        val welcomeSubtitle = TextView(this).apply {
            text = "Pick a feature below to get started. Tap the round button anytime for a quick calculator."
            textSize = 14f
            setTextColor(theme.muted)
            setPadding(0, dp(6), 0, 0)
        }
        welcome.addView(welcomeTitle, wrap())
        welcome.addView(welcomeSubtitle, wrap())
        container.addView(welcome, wrap().apply { bottomMargin = dp(20) })

        val features = listOf(
            HomeFeature("🔎", "Check the Number", "Prime, composite, rational & more", Theme.ACCENT_CHECK, CheckNumberActivity::class.java),
            HomeFeature("√", "Find the Number", "Square, cube, roots & tables", Theme.ACCENT_FIND, FindNumberActivity::class.java),
            HomeFeature("₹", "Currency Calculator", "Split amounts into coins & notes", Theme.ACCENT_CURRENCY, CurrencyActivity::class.java),
            HomeFeature("⇄", "Unit Conversion", "Distance and weight units", Theme.ACCENT_UNIT, UnitConversionActivity::class.java),
            HomeFeature("🎂", "Age Calculator", "Your exact age from your birth date", Theme.ACCENT_AGE, AgeCalculatorActivity::class.java),
            HomeFeature("📊", "Information Chart", "Browse number facts at a glance", Theme.ACCENT_INFO, InfoChartActivity::class.java)
        )

        features.chunked(2).forEach { rowItems ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowItems.forEachIndexed { index, feature ->
                val featureCard = card(accent = feature.accent).apply {
                    setPadding(dp(14), dp(16), dp(14), dp(14))
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        sound.playClick()
                        startActivity(Intent(this@MainActivity, feature.target))
                    }
                }
                val iconBox = TextView(this).apply {
                    text = feature.icon
                    textSize = 20f
                    gravity = Gravity.CENTER
                    background = GradientDrawable().apply {
                        setColor(feature.accent)
                        cornerRadius = dp(40).toFloat()
                    }
                }
                featureCard.addView(iconBox, LinearLayout.LayoutParams(dp(42), dp(42)))
                featureCard.addView(TextView(this).apply {
                    text = feature.title
                    textSize = 15f
                    setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                    setTextColor(theme.text)
                    setPadding(0, dp(10), 0, 0)
                }, wrap())
                featureCard.addView(TextView(this).apply {
                    text = feature.subtitle
                    textSize = 11.5f
                    setTextColor(theme.muted)
                    setPadding(0, dp(4), 0, 0)
                }, wrap())
                row.addView(featureCard, LinearLayout.LayoutParams(0, dp(152), 1f).apply {
                    if (index == 0) rightMargin = dp(8) else leftMargin = dp(8)
                    bottomMargin = dp(12)
                })
            }
            container.addView(row)
        }
    }

    override fun floatingActionView(): View {
        val fab = TextView(this).apply {
            text = "="
            textSize = 26f
            gravity = Gravity.CENTER
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.WHITE)
            background = GradientDrawable().apply {
                setColor(Theme.ACCENT_CALCULATOR)
                cornerRadius = dp(32).toFloat()
            }
            elevation = dp(6).toFloat()
            isClickable = true
            isFocusable = true
            contentDescription = "Open calculator"
        }
        fab.setOnClickListener {
            sound.playClick()
            CalculatorPopup.show(this, theme, fab)
        }
        return fab
    }

    private fun greetingForNow(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }

    private fun showWelcomeFlow() {
        val greeting = greetingForNow()
        val savedName = AppPreferences.getUserName(this)
        val askEveryTime = AppPreferences.isAskNameEveryTime(this)

        if (savedName != null && !askEveryTime) {
            applyGreeting(greeting, savedName)
            return
        }

        val input = EditText(this).apply {
            hint = "Your name (optional)"
            setText(savedName ?: "")
            inputType = InputType.TYPE_CLASS_TEXT
            setSingleLine(true)
            setPadding(dp(14), dp(12), dp(14), dp(12))
        }
        AlertDialog.Builder(this)
            .setTitle("$greeting!")
            .setMessage("What should we call you?")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                val finalName = if (name.isEmpty()) null else name
                AppPreferences.setUserName(this, finalName)
                applyGreeting(greeting, finalName)
            }
            .setNegativeButton("Skip") { _, _ -> applyGreeting(greeting, null) }
            .show()
    }

    private fun applyGreeting(greeting: String, name: String?) {
        welcomeTitle.text = if (name != null) "$greeting, $name!" else "$greeting!"
    }
}
