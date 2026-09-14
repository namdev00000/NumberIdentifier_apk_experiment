package com.example.numberidentifier

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.content.res.Configuration
import android.media.AudioManager
import android.media.ToneGenerator
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.sqrt

class MainActivity : Activity() {

    private enum class ThemeChoice { SYSTEM, LIGHT, DARK }
    private enum class NameMode { REMEMBER, ASK_EVERY_TIME }

    private lateinit var root: FrameLayout
    private lateinit var content: LinearLayout
    private lateinit var titleView: TextView
    private lateinit var subtitleView: TextView
    private lateinit var themeButton: TextView
    private var menuWindow: PopupWindow? = null
    private var tone: ToneGenerator? = null

    private var bg = Color.WHITE
    private var surface = Color.WHITE
    private var text = Color.rgb(25, 25, 25)
    private var muted = Color.rgb(92, 92, 92)
    private var line = Color.rgb(220, 220, 220)
    private var soft = Color.rgb(248, 248, 250)
    private var isDark = false

    private val accents = listOf(
        Color.rgb(34, 197, 94),   // green
        Color.rgb(59, 130, 246),  // blue
        Color.rgb(249, 115, 22),  // orange
        Color.rgb(219, 39, 119),  // pink
        Color.rgb(124, 58, 237),  // purple
        Color.rgb(13, 148, 136),  // teal
        Color.rgb(234, 179, 8)    // yellow
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resolveTheme()
        tone = ToneGenerator(AudioManager.STREAM_MUSIC, 45)
        buildUi()
        showWelcomePopup()
    }

    override fun onResume() {
        super.onResume()
        if (loadThemeChoice() == ThemeChoice.SYSTEM) {
            val desiredDark = systemAutoDark()
            if (desiredDark != isDark) recreate()
        }
    }

    override fun onDestroy() {
        tone?.release()
        tone = null
        super.onDestroy()
    }

    private fun resolveTheme() {
        val choice = loadThemeChoice()
        isDark = when (choice) {
            ThemeChoice.DARK -> true
            ThemeChoice.LIGHT -> false
            ThemeChoice.SYSTEM -> systemAutoDark()
        }
        bg = if (isDark) Color.rgb(15, 18, 24) else Color.rgb(248, 250, 252)
        surface = if (isDark) Color.rgb(26, 31, 40) else Color.WHITE
        text = if (isDark) Color.WHITE else Color.rgb(25, 25, 25)
        muted = if (isDark) Color.rgb(190, 198, 210) else Color.rgb(92, 92, 92)
        line = if (isDark) Color.rgb(62, 70, 84) else Color.rgb(222, 226, 232)
        soft = if (isDark) Color.rgb(33, 39, 49) else Color.rgb(245, 247, 250)

        window.statusBarColor = bg
        window.navigationBarColor = bg
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = if (isDark) 0 else {
            var flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= 26) flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            flags
        }
    }

    private fun systemAutoDark(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 19 || hour < 6
    }

    private fun buildUi() {
        root = FrameLayout(this).apply { setBackgroundColor(bg) }

        val scroll = ScrollView(this).apply {
            clipToPadding = false
            setBackgroundColor(bg)
        }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(10), dp(14), dp(100))
            setBackgroundColor(bg)
        }
        scroll.addView(content)
        root.addView(scroll, FrameLayout.LayoutParams(-1, -1))

        val topBar = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(10), dp(10))
            background = rounded(surface, 22, line, 1)
            elevation = dp(3).toFloat()
        }

        val menu = circleButton("☰", 27f).apply {
            contentDescription = "Open menu"
            setOnClickListener { beep(); showMenu() }
        }
        topBar.addView(menu, LinearLayout.LayoutParams(dp(50), dp(50)))

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, dp(6), 0)
        }
        titleView = TextView(this).apply {
            text = headingName()
            textSize = 20f
            setTextColor(text)
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            maxLines = 1
        }
        subtitleView = TextView(this).apply {
            text = "Numbers Made Simple • Learn • Calculate • Explore"
            textSize = 10.5f
            setTextColor(muted)
        }
        brand.addView(titleView, wrap())
        brand.addView(subtitleView, wrap())
        topBar.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))

        themeButton = circleButton(themeGlyph(), 24f).apply {
            contentDescription = "Choose theme"
            setOnClickListener { beep(); showThemeDialog() }
        }
        topBar.addView(themeButton, LinearLayout.LayoutParams(dp(50), dp(50)))

        content.addView(topBar, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })

        val greeting = TextView(this).apply {
            text = "Welcome${savedName()?.let { ", $it" } ?: ""}!"
            textSize = 26f
            setTextColor(text)
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setPadding(dp(4), dp(10), dp(4), dp(14))
        }
        content.addView(greeting)

        val subtitle = TextView(this).apply {
            text = "Choose a tool below. Every result is explained in simple words."
            textSize = 14f
            setTextColor(muted)
            setPadding(dp(4), 0, dp(4), dp(16))
        }
        content.addView(subtitle)

        val cards = listOf(
            MainFeature("1", "123", "Check the Number", "Classify a number, count divisors, show multiples.") { showCheckNumber() },
            MainFeature("2", "⌕", "Find the Number", "Square, roots, cube, cube root, and tables.") { showFindNumber() },
            MainFeature("3", "÷", "Calculate Numbers", "Open the floating calculator.") { showCalculatorDialog() },
            MainFeature("4", "₹", "Currency Calculator", "Break an Indian rupee amount into notes and coins.") { showCurrencyCalculator() },
            MainFeature("5", "↔", "Unit Conversion", "Convert distance and weight units.") { showUnitConversion() },
            MainFeature("6", "🎂", "Age Calculation", "Calculate age from date of birth.") { showAgeCalculator() },
            MainFeature("7", "▦", "Information Chart", "Quick charts for number facts, units, currency, and formulas.") { showInformationChart() }
        )

        cards.chunked(2).forEachIndexed { rowIndex, rowItems ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            rowItems.forEachIndexed { index, feature ->
                row.addView(mainFeatureCard(feature, accents[rowIndex * 2 + index]), LinearLayout.LayoutParams(0, dp(164), 1f).apply {
                    if (index == 0 && rowItems.size == 2) rightMargin = dp(6)
                    if (index == 1 && rowItems.size == 2) leftMargin = dp(6)
                    bottomMargin = dp(12)
                })
            }
            if (rowItems.size == 1) {
                row.layoutParams = LinearLayout.LayoutParams(-1, dp(164)).apply { bottomMargin = dp(12) }
            }
            content.addView(row)
        }

        val settingsCard = makeCard().apply {
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            setOnClickListener { beep(); showSettingsDialog() }
        }
        settingsCard.addView(TextView(this).apply {
            text = "⚙  Settings"
            textSize = 16f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(text)
        }, wrap())
        settingsCard.addView(TextView(this).apply {
            text = "Theme • Name • Sound"
            textSize = 12.5f
            setTextColor(muted)
            setPadding(0, dp(4), 0, 0)
        }, wrap())
        content.addView(settingsCard, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(4) })

        val calcFab = TextView(this).apply {
            text = "⌨"
            textSize = 24f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(17, 24, 39), 50)
            elevation = dp(10).toFloat()
            isClickable = true
            isFocusable = true
            contentDescription = "Open calculator"
            setOnClickListener { beep(); showCalculatorDialog() }
        }
        root.addView(calcFab, FrameLayout.LayoutParams(dp(60), dp(60), Gravity.END or Gravity.BOTTOM).apply {
            setMargins(0, 0, dp(18), dp(22))
        })

        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener { _, insets ->
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                content.setPadding(dp(14), bars.top + dp(10), dp(14), bars.bottom + dp(100))
                insets
            }
        }
        setContentView(root)
        if (Build.VERSION.SDK_INT >= 30) root.requestApplyInsets()
    }

    private data class MainFeature(val number: String, val icon: String, val title: String, val desc: String, val action: () -> Unit)

    private fun mainFeatureCard(feature: MainFeature, accent: Int): LinearLayout {
        val card = makeCard().apply {
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(surface, 24, line, 1)
            isClickable = true
            isFocusable = true
            setOnClickListener { beep(); feature.action() }
        }
        val badge = TextView(this).apply {
            text = feature.icon
            textSize = if (feature.icon.length > 2) 17f else 21f
            gravity = Gravity.CENTER
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.WHITE)
            background = rounded(accent, 18)
        }
        card.addView(badge, LinearLayout.LayoutParams(dp(44), dp(44)))
        card.addView(TextView(this).apply {
            text = feature.title
            textSize = 15.5f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(text)
            setPadding(0, dp(10), 0, 0)
        }, wrap())
        card.addView(TextView(this).apply {
            text = feature.desc
            textSize = 12f
            setTextColor(muted)
            setPadding(0, dp(5), 0, 0)
        }, wrap())
        return card
    }

    private fun showWelcomePopup() {
        val greeting = greetingText()
        val nameInput = EditText(this).apply {
            hint = "Your name"
            setSingleLine(true)
            textSize = 17f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setTextColor(text)
            setHintTextColor(muted)
            background = rounded(surface, 16, line, 1)
            setPadding(dp(14), 0, dp(14), 0)
            if (loadNameMode() == NameMode.REMEMBER) setText(savedName().orEmpty())
        }

        val remember = android.widget.RadioButton(this).apply { text = "Add and use forever"; isChecked = loadNameMode() == NameMode.REMEMBER }
        val askEvery = android.widget.RadioButton(this).apply { text = "Ask me every time"; isChecked = loadNameMode() == NameMode.ASK_EVERY_TIME }
        val group = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
            addView(remember)
            addView(askEvery)
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), 0, dp(4), 0)
            addView(TextView(this@MainActivity).apply {
                text = "$greeting\nWelcome to Number Identifier!"
                textSize = 20f
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(this@MainActivity.text)
                setPadding(0, 0, 0, dp(12))
            }, wrap())
            addView(nameInput, LinearLayout.LayoutParams(-1, dp(54)))
            addView(group, wrap().apply { topMargin = dp(8) })
        }

        AlertDialog.Builder(this)
            .setTitle(greeting)
            .setView(box)
            .setPositiveButton("Continue") { _, _ ->
                val name = nameInput.text.toString().trim()
                if (remember.isChecked) {
                    saveNameMode(NameMode.REMEMBER)
                    saveName(name)
                } else {
                    saveNameMode(NameMode.ASK_EVERY_TIME)
                    saveName(name)
                }
                refreshHeader()
            }
            .setNeutralButton("Skip") { _, _ -> }
            .show()
    }

    private fun refreshHeader() {
        titleView.text = headingName()
    }

    private fun headingName(): String = savedName()?.let { "Number Identifier • $it" } ?: "Number Identifier"

    private fun greetingText(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning ☀"
            in 12..16 -> "Good afternoon 🌤"
            in 17..20 -> "Good evening 🌇"
            else -> "Good night 🌙"
        }
    }

    private fun showThemeDialog() {
        val names = arrayOf("System / Auto", "Light", "Dark")
        val checked = loadThemeChoice().ordinal
        AlertDialog.Builder(this)
            .setTitle("Choose theme")
            .setSingleChoiceItems(names, checked) { dialog, which ->
                saveThemeChoice(ThemeChoice.values()[which])
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showMenu() {
        menuWindow?.dismiss()
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(18), dp(16), dp(18))
            background = rounded(surface, 0)
        }
        panel.addView(TextView(this).apply {
            text = "Number Identifier"
            textSize = 23f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(text)
        }, wrap())
        panel.addView(TextView(this).apply {
            text = "Choose a tool"
            textSize = 13f
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(14))
        }, wrap())

        val items = listOf(
            "123  Check the Number" to { showCheckNumber() },
            "⌕  Find the Number" to { showFindNumber() },
            "÷  Calculate Numbers" to { showCalculatorDialog() },
            "₹  Currency Calculator" to { showCurrencyCalculator() },
            "↔  Unit Conversion" to { showUnitConversion() },
            "🎂  Age Calculation" to { showAgeCalculator() },
            "▦  Information Chart" to { showInformationChart() },
            "⚙  Settings" to { showSettingsDialog() }
        )
        items.forEach { (label, action) ->
            val row = TextView(this).apply {
                text = label
                textSize = 15.5f
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(text)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), 0, dp(14), 0)
                background = rounded(soft, 16, line, 1)
                isClickable = true
                setOnClickListener {
                    menuWindow?.dismiss()
                    beep()
                    action()
                }
            }
            panel.addView(row, LinearLayout.LayoutParams(-1, dp(50)).apply { bottomMargin = dp(8) })
        }

        menuWindow = PopupWindow(panel, dp(320), ViewGroup.LayoutParams.MATCH_PARENT, true).apply {
            elevation = dp(12).toFloat()
            isOutsideTouchable = true
            setBackgroundDrawable(rounded(surface, 0))
            setOnDismissListener { menuWindow = null }
        }
        menuWindow?.showAtLocation(root, Gravity.START or Gravity.TOP, 0, 0)
    }

    private fun showCheckNumber() {
        val input = numberInput("Enter a number, letter, or symbol")
        val resultBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(8), dp(4), 0)
        }
        val status = TextView(this).apply {
            text = "Enter something and tap Check."
            textSize = 14f
            setTextColor(muted)
        }
        resultBox.addView(status, wrap())

        val divisorsButton = Button(this).apply { text = "Show divisors"; visibility = View.GONE }
        val multiplesButton = Button(this).apply { text = "Show multiples"; visibility = View.GONE }
        resultBox.addView(divisorsButton, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(10) })
        resultBox.addView(multiplesButton, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(6) })

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(this@MainActivity).apply {
                text = "We check number sets, properties, perfect squares/cubes, divisors, and multiples."
                textSize = 14f
                setTextColor(muted)
                setPadding(dp(2), 0, dp(2), dp(10))
            }, wrap())
            addView(input, LinearLayout.LayoutParams(-1, dp(54)))
            addView(resultBox, wrap())
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Check the Number")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Check", null)
            .create()

        var lastResult: NumberClassifier.Result? = null
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val raw = input.text.toString().trim()
                val result = NumberClassifier.classify(raw)
                lastResult = result
                if (raw.isEmpty()) {
                    status.text = "Please enter something."
                    return@setOnClickListener
                }
                if (result == null) {
                    status.text = when {
                        raw.any { it.isLetter() } && raw.all { it.isLetter() || it.isWhitespace() } -> "ℹ This is alphabetic text, not a number."
                        raw.any { !it.isDigit() && it !in ".+-" } -> "ℹ This input contains special characters, so it is not a pure number."
                        else -> "Please enter a valid number."
                    }
                    divisorsButton.visibility = View.GONE
                    multiplesButton.visibility = View.GONE
                    return@setOnClickListener
                }
                status.text = buildClassificationBlocks(result)
                divisorsButton.visibility = if (result.divisorCount != null) View.VISIBLE else View.GONE
                multiplesButton.visibility = if (result.isInteger) View.VISIBLE else View.GONE
            }
        }
        divisorsButton.setOnClickListener { lastResult?.let { showListDialog("Divisors of ${it.raw}", it.divisors.joinToString(", ")) } }
        multiplesButton.setOnClickListener { lastResult?.let { showListDialog("First 10 multiples of ${it.raw}", it.multiples.joinToString("\n")) } }
        dialog.show()
    }

    private fun buildClassificationBlocks(r: NumberClassifier.Result): LinearLayout {
        val wrap = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(10), 0, 0)
        }
        val rows = listOf(
            "Whole number" to r.isWhole,
            "Natural number" to r.isNatural,
            "Prime number" to r.isPrime,
            "Composite number" to r.isComposite,
            "Even number" to r.isEven,
            "Odd number" to r.isOdd,
            "Rational number" to r.isRational,
            "Irrational number" to r.isIrrational,
            "Integer" to r.isInteger,
            "Complex number" to r.isComplex,
            "Perfect square" to r.isPerfectSquare,
            "Real square root exists" to r.hasRealSquareRoot,
            "Perfect cube" to r.isPerfectCube,
            "Real cube root exists" to r.hasRealCubeRoot
        )
        rows.forEach { (label, value) ->
            val yes = value == true
            val row = LinearLayout(this).apply {
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(if (isDark) Color.rgb(30, 36, 46) else Color.rgb(249, 250, 252), 14, line, 1)
                setPadding(dp(12), 0, dp(10), 0)
            }
            row.addView(TextView(this).apply {
                text = label
                textSize = 13.5f
                setTextColor(text)
            }, LinearLayout.LayoutParams(0, dp(42), 1f))
            row.addView(TextView(this).apply {
                text = if (yes) "✓ YES" else "✗ NO"
                textSize = 12f
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                background = rounded(if (yes) Color.rgb(22, 163, 74) else Color.rgb(220, 38, 38), 14)
            }, LinearLayout.LayoutParams(dp(72), dp(30)))
            wrap.addView(row, LinearLayout.LayoutParams(-1, dp(42)).apply { bottomMargin = dp(6) })
        }
        wrap.addView(TextView(this).apply {
            val sign = r.sign
            text = "Sign: $sign"
            textSize = 13f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(muted)
            setPadding(dp(2), dp(4), 0, 0)
        }, wrap())
        return wrap
    }

    private fun showFindNumber() {
        val input = numberInput("Enter a number")
        val out = outputText("Choose a tool below.")
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val buttons = listOf(
            "Find Square" to { out.text = squareResult(input.text.toString()) },
            "Find Square Root" to { out.text = squareRootResult(input.text.toString()) },
            "Find Cube" to { out.text = cubeResult(input.text.toString()) },
            "Find Cube Root" to { out.text = cubeRootResult(input.text.toString()) },
            "Create Table" to { out.text = tableResult(input.text.toString()) }
        )
        buttons.forEach { (label, action) ->
            val b = Button(this).apply { text = label; setOnClickListener { beep(); action() } }
            actions.addView(b, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(6) })
        }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(this@MainActivity).apply {
                text = "Use one input and choose the operation you need."
                textSize = 14f
                setTextColor(muted)
                setPadding(0, 0, 0, dp(10))
            }, wrap())
            addView(input, LinearLayout.LayoutParams(-1, dp(54)))
            addView(actions, wrap())
            addView(out, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(8) })
        }
        AlertDialog.Builder(this)
            .setTitle("Find the Number")
            .setView(box)
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCalculatorDialog() {
        val display = TextView(this).apply {
            text = "0"
            textSize = 31f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(17, 24, 39), 18)
            setPadding(dp(14), 0, dp(14), 0)
        }
        var current = ""
        var stored: Double? = null
        var op: String? = null
        var resetNext = false

        fun setDisplay(s: String) { display.text = s }
        fun calculate(a: Double, b: Double, symbol: String): Double? = when (symbol) {
            "+" -> a + b
            "−" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) null else a / b
            else -> b
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(4), dp(4), 0)
            addView(display, LinearLayout.LayoutParams(-1, dp(72)))
        }
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        fun keyRow(labels: List<String>) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            labels.forEach { label ->
                val button = Button(this).apply {
                    text = label
                    textSize = 18f
                    setOnClickListener {
                        beep()
                        when {
                            label == "C" -> { current = ""; stored = null; op = null; resetNext = false; setDisplay("0") }
                            label == "⌫" -> { current = current.dropLast(1); setDisplay(current.ifEmpty { "0" }) }
                            label == "." -> if (!current.contains('.')) { current += "."; setDisplay(current) }
                            label in listOf("+", "−", "×", "÷") -> {
                                if (current.isNotEmpty()) {
                                    val number = current.toDoubleOrNull()
                                    if (number != null) {
                                        if (stored != null && op != null) {
                                            val result = calculate(stored!!, number, op!!)
                                            if (result == null) { setDisplay("Cannot divide by 0"); return@setOnClickListener }
                                            stored = result
                                            setDisplay(formatNumber(result))
                                        } else stored = number
                                        current = ""
                                    }
                                }
                                op = label
                                resetNext = false
                            }
                            label == "=" -> {
                                if (stored != null && op != null && current.isNotEmpty()) {
                                    val number = current.toDoubleOrNull()
                                    if (number != null) {
                                        val result = calculate(stored!!, number, op!!)
                                        if (result == null) { setDisplay("Cannot divide by 0"); return@setOnClickListener }
                                        setDisplay(formatNumber(result))
                                        current = formatNumber(result)
                                        stored = null
                                        op = null
                                    }
                                }
                            }
                            label == "%" -> {
                                current.toDoubleOrNull()?.let { current = formatNumber(it / 100.0); setDisplay(current) }
                            }
                            else -> {
                                if (resetNext) { current = ""; resetNext = false }
                                current += label
                                setDisplay(current)
                            }
                        }
                    }
                }
                row.addView(button, LinearLayout.LayoutParams(0, dp(54), 1f).apply { marginStart = dp(2); marginEnd = dp(2); topMargin = dp(4) })
            }
            grid.addView(row)
        }
        keyRow(listOf("C", "⌫", "%", "÷"))
        keyRow(listOf("7", "8", "9", "×"))
        keyRow(listOf("4", "5", "6", "−"))
        keyRow(listOf("1", "2", "3", "+"))
        keyRow(listOf("0", ".", "=", ""))
        box.addView(grid, wrap())

        AlertDialog.Builder(this)
            .setTitle("Calculator")
            .setView(box)
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCurrencyCalculator() {
        val input = numberInput("Enter amount, e.g. 987")
        val output = outputText("Enter an amount and tap Calculate.")
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(this@MainActivity).apply {
                text = "Greedy breakdown using requested Indian denominations. ₹2,000 notes are shown as a special legacy denomination because RBI withdrew them from circulation but they remain legal tender."
                textSize = 13.5f
                setTextColor(muted)
                setPadding(0, 0, 0, dp(10))
            }, wrap())
            addView(input, LinearLayout.LayoutParams(-1, dp(54)))
            addView(output, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10) })
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Indian Currency Calculator")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Calculate", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val amountText = input.text.toString().trim()
                val amount = amountText.toDoubleOrNull()
                output.text = if (amount == null || amount < 0 || !amount.isFinite()) {
                    "Please enter a valid non-negative amount."
                } else {
                    currencyBreakdown(amount)
                }
            }
        }
        dialog.show()
    }

    private fun currencyBreakdown(amount: Double): String {
        val paiseTotal = kotlin.math.round(amount * 100.0).toLong()
        val denominations = listOf(
            200000L to "₹2,000 note*",
            50000L to "₹500 note",
            20000L to "₹200 note",
            10000L to "₹100 note",
            5000L to "₹50 note",
            2000L to "₹20 note",
            1000L to "₹10 note",
            500L to "₹5 note",
            100L to "₹1 note",
            2000L / 2 to "₹20 coin"
        )
        // Explicitly keep the requested coin list separate to avoid confusing note/coin denominations.
        val all = listOf(
            200000L to "₹2,000 note*", 50000L to "₹500 note", 20000L to "₹200 note", 10000L to "₹100 note",
            5000L to "₹50 note", 2000L to "₹20 note", 1000L to "₹10 note", 500L to "₹5 note", 100L to "₹1 note",
            2000L to "₹20 coin", 1000L to "₹10 coin", 500L to "₹5 coin", 200L to "₹2 coin", 100L to "₹1 coin"
        )
        var remaining = paiseTotal
        val lines = mutableListOf<String>()
        all.forEach { (value, label) ->
            if (remaining >= value) {
                val count = remaining / value
                remaining %= value
                lines.add("$label × $count")
            }
        }
        if (remaining > 0) lines.add("Remaining: ₹${String.format(Locale.US, "%.2f", remaining / 100.0)}")
        if (lines.isEmpty()) lines.add("₹0.00 — no denominations needed.")
        lines.add(0, "Amount: ₹${String.format(Locale.US, "%.2f", amount)}")
        lines.add("* ₹2,000 note is withdrawn from circulation but remains legal tender.")
        return lines.joinToString("\n")
    }

    private fun showUnitConversion() {
        val mode = Spinner(this)
        mode.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("Distance", "Weight"))
        val from = Spinner(this)
        val to = Spinner(this)
        val input = numberInput("Enter value")
        val output = outputText("Choose units and tap Convert.")

        fun units(category: String): List<String> = if (category == "Distance") {
            listOf("Millimeter (mm)", "Centimeter (cm)", "Meter (m)", "Kilometer (km)", "Inch (in)", "Foot (ft)", "Mile (mi)")
        } else {
            listOf("Milligram (mg)", "Gram (g)", "Kilogram (kg)", "Tonne (t)", "Ounce (oz)", "Pound (lb)", "Quintal (q)")
        }

        fun refreshUnits(category: String) {
            val list = units(category)
            from.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            to.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            if (list.size > 1) to.setSelection(1)
        }
        refreshUnits("Distance")
        mode.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) { refreshUnits(mode.selectedItem.toString()) }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(mode, LinearLayout.LayoutParams(-1, dp(50)))
            addView(TextView(this@MainActivity).apply { text = "From"; setTextColor(muted); setPadding(0, dp(8), 0, dp(4)) }, wrap())
            addView(from, LinearLayout.LayoutParams(-1, dp(50)))
            addView(TextView(this@MainActivity).apply { text = "To"; setTextColor(muted); setPadding(0, dp(8), 0, dp(4)) }, wrap())
            addView(to, LinearLayout.LayoutParams(-1, dp(50)))
            addView(input, LinearLayout.LayoutParams(-1, dp(54)).apply { topMargin = dp(10) })
            addView(output, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10) })
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Unit Conversion")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Convert", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val value = input.text.toString().trim().toDoubleOrNull()
                if (value == null || !value.isFinite()) {
                    output.text = "Please enter a valid number."
                    return@setOnClickListener
                }
                val category = mode.selectedItem.toString()
                val result = if (category == "Distance") convertDistance(value, from.selectedItem.toString(), to.selectedItem.toString())
                else convertWeight(value, from.selectedItem.toString(), to.selectedItem.toString())
                output.text = "${formatNumber(value)} ${from.selectedItem} = ${formatNumber(result)} ${to.selectedItem}"
            }
        }
        dialog.show()
    }

    private fun convertDistance(value: Double, from: String, to: String): Double {
        val meters = when {
            from.startsWith("Millimeter") -> value / 1000.0
            from.startsWith("Centimeter") -> value / 100.0
            from.startsWith("Meter") -> value
            from.startsWith("Kilometer") -> value * 1000.0
            from.startsWith("Inch") -> value * 0.0254
            from.startsWith("Foot") -> value * 0.3048
            else -> value * 1609.344
        }
        return when {
            to.startsWith("Millimeter") -> meters * 1000.0
            to.startsWith("Centimeter") -> meters * 100.0
            to.startsWith("Meter") -> meters
            to.startsWith("Kilometer") -> meters / 1000.0
            to.startsWith("Inch") -> meters / 0.0254
            to.startsWith("Foot") -> meters / 0.3048
            else -> meters / 1609.344
        }
    }

    private fun convertWeight(value: Double, from: String, to: String): Double {
        val grams = when {
            from.startsWith("Milligram") -> value / 1000.0
            from.startsWith("Gram") -> value
            from.startsWith("Kilogram") -> value * 1000.0
            from.startsWith("Tonne") -> value * 1_000_000.0
            from.startsWith("Ounce") -> value * 28.349523125
            from.startsWith("Pound") -> value * 453.59237
            else -> value * 100_000.0
        }
        return when {
            to.startsWith("Milligram") -> grams * 1000.0
            to.startsWith("Gram") -> grams
            to.startsWith("Kilogram") -> grams / 1000.0
            to.startsWith("Tonne") -> grams / 1_000_000.0
            to.startsWith("Ounce") -> grams / 28.349523125
            to.startsWith("Pound") -> grams / 453.59237
            else -> grams / 100_000.0
        }
    }

    private fun showAgeCalculator() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dob = EditText(this).apply {
            hint = "DD-MM-YYYY"
            inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            setSingleLine(true)
            textSize = 18f
            setTextColor(text)
            setHintTextColor(muted)
            background = rounded(surface, 16, line, 1)
            setPadding(dp(14), 0, dp(14), 0)
        }
        val out = outputText("Enter your date of birth and tap Calculate.")
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(this@MainActivity).apply {
                text = "Your age is calculated from your date of birth to today's date."
                textSize = 14f
                setTextColor(muted)
                setPadding(0, 0, 0, dp(10))
            }, wrap())
            addView(dob, LinearLayout.LayoutParams(-1, dp(54)))
            addView(out, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(10) })
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Age Calculation")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Calculate", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                try {
                    val date = dateFormat.parse(dob.text.toString().trim()) ?: throw IllegalArgumentException()
                    val birth = Calendar.getInstance().apply { time = date }
                    val today = Calendar.getInstance()
                    if (birth.after(today)) throw IllegalArgumentException()
                    val age = calculateAge(birth, today)
                    out.text = "Age: ${age.first} years, ${age.second} months, ${age.third} days\n\nDate of birth: ${dob.text}\nToday: ${dateFormat.format(today.time)}"
                } catch (_: Exception) {
                    out.text = "Please enter a valid past date in DD-MM-YYYY format."
                }
            }
        }
        dialog.show()
    }

    private fun calculateAge(birth: Calendar, today: Calendar): Triple<Int, Int, Int> {
        var years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        var days = today.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)
        if (days < 0) {
            months--
            val previousMonth = (today.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
            }
            days += previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }
        return Triple(years, months, days)
    }

    private fun showInformationChart() {
        val items = listOf(
            "Prime number" to "A whole number greater than 1 with exactly two positive factors: 1 and itself.",
            "Composite number" to "A whole number greater than 1 with more than two positive factors.",
            "Natural number" to "This app uses {1, 2, 3, ...}.",
            "Whole number" to "This app uses {0, 1, 2, 3, ...}.",
            "Rational number" to "A number that can be written as p/q where q is not zero.",
            "Irrational number" to "A real number that cannot be written as p/q.",
            "Integer" to "..., -2, -1, 0, 1, 2, ...",
            "Complex number" to "A real number can be represented as a + 0i, so every real number is also complex.",
            "Distance units" to "mm, cm, m, km, inch, foot, mile.",
            "Weight units" to "mg, g, kg, tonne, ounce, pound, quintal.",
            "Indian currency" to "₹1, ₹2, ₹5, ₹10, ₹20 coins and common note denominations.",
            "System theme" to "Auto mode uses local time: 06:00–18:59 light, otherwise dark."
        )
        val scroll = ScrollView(this)
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(4), 0, dp(4), 0) }
        items.forEach { (heading, detail) ->
            val card = makeCard().apply { setPadding(dp(14), dp(12), dp(14), dp(12)) }
            card.addView(TextView(this).apply {
                text = heading
                textSize = 15.5f
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(text)
            }, wrap())
            card.addView(TextView(this).apply {
                text = detail
                textSize = 13f
                setTextColor(muted)
                setPadding(0, dp(4), 0, 0)
            }, wrap())
            panel.addView(card, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(8) })
        }
        scroll.addView(panel)
        AlertDialog.Builder(this)
            .setTitle("Information Chart")
            .setView(scroll)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showSettingsDialog() {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(4), 0, dp(4), 0) }
        val sound = android.widget.Switch(this).apply {
            text = "Sound"
            textSize = 16f
            setTextColor(text)
            isChecked = soundOn()
        }
        sound.setOnCheckedChangeListener { _, checked -> saveSound(checked) }

        val nameRemember = android.widget.Switch(this).apply {
            text = "Remember my name"
            textSize = 16f
            setTextColor(text)
            isChecked = loadNameMode() == NameMode.REMEMBER
        }
        nameRemember.setOnCheckedChangeListener { _, checked -> saveNameMode(if (checked) NameMode.REMEMBER else NameMode.ASK_EVERY_TIME) }

        val theme = Button(this).apply { text = "Theme: ${loadThemeChoice().name.replace('_', ' ')}"; setOnClickListener { showThemeDialog() } }
        panel.addView(sound, wrap())
        panel.addView(nameRemember, wrap())
        panel.addView(theme, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(10) })
        panel.addView(TextView(this).apply {
            text = "Your welcome message appears each time the app opens. Turn off Remember my name to be asked every time."
            textSize = 13f
            setTextColor(muted)
            setPadding(0, dp(10), 0, 0)
        }, wrap())

        AlertDialog.Builder(this)
            .setTitle("Settings")
            .setView(panel)
            .setPositiveButton("Done", null)
            .show()
    }

    // ---------- Small reusable dialogs / helpers ----------

    private fun showSingleResult(title: String, expression: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(expression).setPositiveButton("Close", null).show()
    }

    private fun squareResult(raw: String): String {
        val n = raw.toDoubleOrNull() ?: return "Please enter a valid number."
        return "Square = ${formatNumber(n * n)}\n\n$n² = $n × $n"
    }

    private fun squareRootResult(raw: String): String {
        val n = raw.toDoubleOrNull() ?: return "Please enter a valid number."
        return if (n < 0) "No real square root exists for a negative number." else "Square root = ${formatNumber(sqrt(n))}"
    }

    private fun cubeResult(raw: String): String {
        val n = raw.toDoubleOrNull() ?: return "Please enter a valid number."
        return "Cube = ${formatNumber(n * n * n)}"
    }

    private fun cubeRootResult(raw: String): String {
        val n = raw.toDoubleOrNull() ?: return "Please enter a valid number."
        return "Cube root = ${formatNumber(cbrt(n))}"
    }

    private fun tableResult(raw: String): String {
        val n = raw.toBigDecimalOrNull() ?: return "Please enter a valid number."
        return (1..10).joinToString("\n") { "$n × $it = ${n.multiply(BigDecimal(it)).stripTrailingZeros().toPlainString()}" }
    }

    private fun outputText(message: String): TextView = TextView(this).apply {
        text = message
        textSize = 14f
        setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(text)
        background = rounded(soft, 16, line, 1)
        setPadding(dp(14), dp(14), dp(14), dp(14))
    }

    private fun numberInput(hint: String): EditText = EditText(this).apply {
        this.hint = hint
        textSize = 18f
        setSingleLine(true)
        inputType = InputType.TYPE_CLASS_TEXT
        setTextColor(this@MainActivity.text)
        setHintTextColor(muted)
        background = rounded(surface, 16, line, 1)
        setPadding(dp(14), 0, dp(14), 0)
    }

    private fun showListDialog(title: String, body: String) {
        AlertDialog.Builder(this).setTitle(title).setMessage(body).setPositiveButton("Close", null).show()
    }

    private fun makeCard() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = rounded(surface, 22, line, 1)
        elevation = dp(2).toFloat()
    }

    private fun circleButton(symbol: String, size: Float): TextView = TextView(this).apply {
        text = symbol
        textSize = size
        gravity = Gravity.CENTER
        setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(text)
        background = rounded(soft, 50, line, 1)
        isClickable = true
        isFocusable = true
    }

    private fun themeGlyph(): String = when (loadThemeChoice()) {
        ThemeChoice.LIGHT -> "☀"
        ThemeChoice.DARK -> "☾"
        ThemeChoice.SYSTEM -> "◐"
    }

    private fun formatNumber(value: Double): String {
        if (!value.isFinite()) return "Not available"
        val bd = BigDecimal.valueOf(value).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }

    private fun rounded(fill: Int, radius: Int, stroke: Int? = null, width: Int = 0): GradientDrawable = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radius).toFloat()
        if (stroke != null && width > 0) setStroke(dp(width), stroke)
    }

    private fun wrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun beep() {
        if (soundOn()) tone?.startTone(ToneGenerator.TONE_PROP_BEEP, 55)
    }

    private fun prefs() = getSharedPreferences("number_identifier", Context.MODE_PRIVATE)
    private fun soundOn() = prefs().getBoolean("sound", true)
    private fun saveSound(value: Boolean) { prefs().edit().putBoolean("sound", value).apply() }
    private fun savedName(): String? = prefs().getString("name", null)?.takeIf { it.isNotBlank() }
    private fun saveName(value: String) { prefs().edit().putString("name", value).apply() }
    private fun loadNameMode(): NameMode = NameMode.values().getOrElse(prefs().getInt("name_mode", NameMode.REMEMBER.ordinal)) { NameMode.REMEMBER }
    private fun saveNameMode(mode: NameMode) { prefs().edit().putInt("name_mode", mode.ordinal).apply() }
    private fun loadThemeChoice(): ThemeChoice = ThemeChoice.values().getOrElse(prefs().getInt("theme", ThemeChoice.SYSTEM.ordinal)) { ThemeChoice.SYSTEM }
    private fun saveThemeChoice(choice: ThemeChoice) { prefs().edit().putInt("theme", choice.ordinal).apply() }
}
