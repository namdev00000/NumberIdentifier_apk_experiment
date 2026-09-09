package com.example.numberidentifier

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.sqrt

class MainActivity : Activity() {

    private enum class ThemeChoice { SYSTEM, LIGHT, DARK }

    private lateinit var scrollView: ScrollView
    private lateinit var root: LinearLayout
    private lateinit var topBar: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var themeButton: TextView

    private var drawer: PopupWindow? = null
    private var lastSystemDark = false

    private var isDark = false
    private var bg = Color.WHITE
    private var surface = Color.WHITE
    private var text = Color.rgb(20, 20, 20)
    private var muted = Color.rgb(92, 92, 92)
    private var line = Color.rgb(220, 220, 220)
    private var soft = Color.rgb(246, 246, 246)

    private val mathContext = MathContext(18, RoundingMode.HALF_UP)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyThemeChoice(loadThemeChoice(), recreateUi = false)
        buildUi()
        lastSystemDark = systemIsDark()
    }

    override fun onResume() {
        super.onResume()
        if (loadThemeChoice() == ThemeChoice.SYSTEM) {
            val nowDark = systemIsDark()
            if (nowDark != lastSystemDark) recreate()
            lastSystemDark = nowDark
        }
    }

    private fun buildUi() {
        window.statusBarColor = bg
        window.navigationBarColor = bg
        updateSystemBarIcons()

        scrollView = ScrollView(this).apply {
            setBackgroundColor(bg)
            clipToPadding = false
        }
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }
        scrollView.addView(root)

        topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(10), dp(10))
            background = solid(surface)
            elevation = dp(2).toFloat()
        }

        val menuButton = button("☰", 28f).apply {
            contentDescription = "Open feature menu"
            setOnClickListener { showDrawer() }
        }
        topBar.addView(menuButton, LinearLayout.LayoutParams(dp(52), dp(52)))

        themeButton = button("◐", 24f).apply {
            contentDescription = "Choose light, dark, or system theme"
            setOnClickListener { showThemeDialog() }
        }
        topBar.addView(themeButton, LinearLayout.LayoutParams(dp(52), dp(52)).apply { leftMargin = dp(4) })

        val brandBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, dp(8), 0)
        }
        title = TextView(this).apply {
            text = "Number Identifier"
            textSize = 19f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
            maxLines = 1
        }
        subtitle = TextView(this).apply {
            text = "Learn • Calculate • Check"
            textSize = 11.5f
            setTextColor(muted)
        }
        brandBox.addView(title, wrap())
        brandBox.addView(subtitle, wrap())
        topBar.addView(brandBox, LinearLayout.LayoutParams(0, -2, 1f))

        root.addView(topBar)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(18), dp(16), dp(28))
            setBackgroundColor(bg)
        }
        root.addView(content)

        val welcome = makeCard().apply { setPadding(dp(20), dp(20), dp(20), dp(20)) }
        welcome.addView(TextView(this).apply {
            text = "Hello, Number Explorer!"
            textSize = 24f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
        }, wrap())
        welcome.addView(TextView(this).apply {
            text = "Pick a feature. Enter a number. Get a clear answer."
            textSize = 14f
            setTextColor(muted)
            setPadding(0, dp(6), 0, 0)
        }, wrap())
        content.addView(welcome)

        val heading = TextView(this).apply {
            text = "What do you want to explore?"
            textSize = 19f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
            setPadding(dp(2), dp(22), dp(2), dp(10))
        }
        content.addView(heading)

        val features = listOf(
            Feature("1", "↕", "Even or Odd", "Check whether a number is even or odd.") { showParityDialog() },
            Feature("2", "²", "Square", "Find n² for your number.") { showSingleInputDialog("Square", "Square means multiplying a number by itself: n² = n × n.") { n -> squareOf(n) } },
            Feature("3", "√", "Square Root", "Find √n when the real square root exists.") { showSingleInputDialog("Square Root", "The square root of n is the number that gives n when multiplied by itself.") { n -> squareRootOf(n) } },
            Feature("4", "∛", "Cube & Cube Root", "Find n³ and ∛n.") { showCubeDialog() },
            Feature("5", "×", "Multiplication Table", "Generate the table from ×1 to ×10.") { showTableDialog() },
            Feature("6", "C", "Composite Number", "Learn what composite means, then check a number.") { showPrimeCompositeDialog(isPrimeFeature = false) },
            Feature("7", "P", "Prime Number", "Learn what prime means, then check a number.") { showPrimeCompositeDialog(isPrimeFeature = true) },
            Feature("8", ".", "Decimal Number", "Check whether the entered value is a decimal number.") { showDecimalDialog() }
        )

        features.chunked(2).forEach { rowItems ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            rowItems.forEach { feature ->
                row.addView(featureCard(feature), LinearLayout.LayoutParams(0, dp(146), 1f).apply {
                    if (rowItems.indexOf(feature) == 0) rightMargin = dp(8) else leftMargin = dp(8)
                    bottomMargin = dp(12)
                })
            }
            content.addView(row)
        }

        val note = makeCard().apply {
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(this@MainActivity.soft, 20, this@MainActivity.line, dp(1))
        }
        note.addView(TextView(this).apply {
            text = "Tip  •  Open the ☰ menu anytime to jump to a feature. Use ◐ to choose System, Light, or Dark theme."
            textSize = 12.5f
            setTextColor(muted)
        }, wrap())
        content.addView(note, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(6) })

        setContentView(scrollView)
        applyInsets()
    }

    private data class Feature(val number: String, val icon: String, val title: String, val description: String, val action: () -> Unit)

    private fun featureCard(feature: Feature): LinearLayout {
        val card = makeCard().apply {
            isClickable = true
            isFocusable = true
            setOnClickListener { feature.action() }
            setPadding(dp(14), dp(14), dp(14), dp(12))
            background = rounded(this@MainActivity.surface, 22, this@MainActivity.line, dp(1))
        }
        val iconBox = TextView(this).apply {
            text = feature.icon
            textSize = 20f
            gravity = Gravity.CENTER
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
            background = rounded(this@MainActivity.soft, 50, this@MainActivity.line, dp(1))
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(40), dp(40)))
        card.addView(TextView(this).apply {
            text = feature.title
            textSize = 16f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
            setPadding(0, dp(10), 0, 0)
        }, wrap())
        card.addView(TextView(this).apply {
            this.text = feature.description
            textSize = 12f
            setTextColor(muted)
            setPadding(0, dp(4), 0, 0)
        }, wrap())
        return card
    }

    private fun showThemeDialog() {
        val names = arrayOf("System theme", "Light theme", "Dark theme")
        val current = loadThemeChoice().ordinal
        AlertDialog.Builder(this)
            .setTitle("Choose theme")
            .setSingleChoiceItems(names, current) { dialog, which ->
                val choice = ThemeChoice.values()[which]
                saveThemeChoice(choice)
                dialog.dismiss()
                recreate()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showDrawer() {
        drawer?.dismiss()
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            setBackgroundColor(surface)
        }
        panel.addView(TextView(this).apply {
            text = "Explore features"
            textSize = 22f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(this@MainActivity.text)
        }, wrap())
        panel.addView(TextView(this).apply {
            text = "Choose a tool"
            textSize = 13f
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(14))
        }, wrap())

        val menuItems = listOf(
            "1  •  Even or Odd" to { showParityDialog() },
            "2  •  Square" to { showSingleInputDialog("Square", "n² = n × n.") { n -> squareOf(n) } },
            "3  •  Square Root" to { showSingleInputDialog("Square Root", "√n is the number whose square is n.") { n -> squareRootOf(n) } },
            "4  •  Cube & Cube Root" to { showCubeDialog() },
            "5  •  Multiplication Table" to { showTableDialog() },
            "6  •  Composite Number" to { showPrimeCompositeDialog(false) },
            "7  •  Prime Number" to { showPrimeCompositeDialog(true) },
            "8  •  Decimal Number" to { showDecimalDialog() }
        )
        menuItems.forEach { (label, action) ->
            val item = TextView(this).apply {
                text = label
                textSize = 16f
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(this@MainActivity.text)
                setGravity(Gravity.CENTER_VERTICAL)
                background = rounded(this@MainActivity.soft, 16, this@MainActivity.line, dp(1))
                setPadding(dp(14), 0, dp(14), 0)
                isClickable = true
                setOnClickListener {
                    drawer?.dismiss()
                    action()
                }
            }
            panel.addView(item, LinearLayout.LayoutParams(-1, dp(52)).apply { bottomMargin = dp(8) })
        }
        val close = TextView(this).apply {
            text = "Close"
            textSize = 15f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            gravity = Gravity.CENTER
            setTextColor(this@MainActivity.surface)
            background = rounded(this@MainActivity.text, 16)
            isClickable = true
            setOnClickListener { drawer?.dismiss() }
        }
        panel.addView(close, LinearLayout.LayoutParams(-1, dp(50)).apply { topMargin = dp(6) })

        drawer = PopupWindow(panel, dp(310), ViewGroup.LayoutParams.MATCH_PARENT, true).apply {
            setBackgroundDrawable(solid(surface))
            elevation = dp(10).toFloat()
            isOutsideTouchable = true
            setOnDismissListener { drawer = null }
        }
        drawer?.showAtLocation(root, Gravity.START or Gravity.TOP, 0, 0)
    }

    private fun showParityDialog() {
        val input = numberInput("Try 2, 7, -4, or 0")
        val result = outputText()
        val box = verticalDialogContent("Even or Odd", "An even number can be divided by 2 with no remainder. An odd number leaves a remainder of 1.", input, result)
        result.text = "Enter a number and tap Check."
        val dialog = dialogWithContent("Even or Odd", box)
        dialog.setOnShowListener {
            val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positive.setOnClickListener {
                val n = input.text.toString().trim().toLongOrNull()
                result.text = if (n == null) "Please enter a whole number, such as 7 or -4." else "${if (n % 2L == 0L) "✓ Even" else "✓ Odd"}\n\n$n is ${if (n % 2L == 0L) "divisible" else "not divisible"} by 2."
            }
        }
        dialog.show()
    }

    private fun showSingleInputDialog(titleText: String, explanation: String, calculator: (Double) -> String) {
        val input = numberInput("Enter a number")
        val result = outputText()
        val box = verticalDialogContent(titleText, explanation, input, result)
        result.text = "Enter a number and tap Calculate."
        val dialog = AlertDialog.Builder(this)
            .setTitle(titleText)
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Calculate", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val n = input.text.toString().trim().toDoubleOrNull()
                result.text = if (n == null || !n.isFinite()) "Please enter a valid number." else calculator(n)
            }
        }
        dialog.show()
    }

    private fun showCubeDialog() {
        val input = numberInput("Enter a number")
        val result = outputText()
        val box = verticalDialogContent("Cube & Cube Root", "A cube is n × n × n. The cube root is the number that gives n when cubed. Negative numbers are allowed.", input, result)
        result.text = "Enter a number and tap Calculate."
        val dialog = AlertDialog.Builder(this)
            .setTitle("Cube & Cube Root")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Calculate", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val raw = input.text.toString().trim()
                val n = raw.toDoubleOrNull()
                if (n == null || !n.isFinite()) result.text = "Please enter a valid number."
                else result.text = "Cube: ${formatDouble(n * n * n)}\nCube root: ${formatDouble(cbrt(n))}"
            }
        }
        dialog.show()
    }

    private fun showTableDialog() {
        val input = numberInput("Example: 2")
        val result = outputText()
        val box = verticalDialogContent("Multiplication Table", "Enter a number and we will create its table from ×1 to ×10.", input, result)
        result.text = "Enter a number and tap Create table."
        val dialog = AlertDialog.Builder(this)
            .setTitle("Multiplication Table")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Create table", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val n = input.text.toString().trim().toBigDecimalOrNull()
                if (n == null) result.text = "Please enter a valid number."
                else {
                    result.text = (1..10).joinToString("\n") { i ->
                        "$n × $i = ${formatDecimal(n.multiply(BigDecimal(i), mathContext))}"
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showPrimeCompositeDialog(isPrimeFeature: Boolean) {
        val titleText = if (isPrimeFeature) "Prime Number" else "Composite Number"
        val explanation = if (isPrimeFeature) {
            "A prime number is a whole number greater than 1 with exactly two positive factors: 1 and itself."
        } else {
            "A composite number is a whole number greater than 1 that has more than two positive factors."
        }
        val input = numberInput("Enter a whole number")
        val result = outputText()
        val box = verticalDialogContent(titleText, explanation, input, result)
        result.text = "Now enter a whole number and tap Check."
        val dialog = AlertDialog.Builder(this)
            .setTitle(titleText)
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Check", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val n = input.text.toString().trim().toLongOrNull()
                result.text = if (n == null) {
                    "Please enter a whole number."
                } else {
                    when {
                        n <= 1L -> "$n is neither prime nor composite."
                        isPrime(n) -> if (isPrimeFeature) "$n is a PRIME number. ✓" else "$n is not composite; it is PRIME."
                        else -> if (isPrimeFeature) "$n is not prime; it is COMPOSITE. ✗" else "$n is a COMPOSITE number. ✓"
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showDecimalDialog() {
        val input = numberInput("Example: 3.14")
        val result = outputText()
        val box = verticalDialogContent("Decimal Number", "A decimal number is written using a decimal point, such as 2.5, 0.75, or -3.2.", input, result)
        result.text = "Enter a value and tap Check."
        val dialog = AlertDialog.Builder(this)
            .setTitle("Decimal Number")
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Check", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val raw = input.text.toString().trim()
                val value = raw.toDoubleOrNull()
                if (value == null || !value.isFinite()) {
                    result.text = "Please enter a valid number."
                } else {
                    val isDecimal = raw.contains('.')
                    result.text = if (isDecimal) "✓ This is written as a decimal number.\n\nValue: $raw" else "✗ This is not written as a decimal number.\n\nValue: $raw"
                }
            }
        }
        dialog.show()
    }

    private fun squareOf(n: Double): String = "Square: ${formatDouble(n * n)}\n\nn² = $n × $n"

    private fun squareRootOf(n: Double): String = if (n < 0) {
        "No real square root exists for a negative number.\n\nTry 0 or a positive number."
    } else {
        "Square root: ${formatDouble(sqrt(n))}\n\n√$n = ${formatDouble(sqrt(n))}"
    }

    private fun isPrime(n: Long): Boolean {
        if (n < 2) return false
        if (n == 2L) return true
        if (n % 2L == 0L) return false
        var d = 3L
        while (d <= n / d) {
            if (n % d == 0L) return false
            d += 2
        }
        return true
    }

    private fun numberInput(hintText: String): EditText = EditText(this).apply {
        hint = hintText
        textSize = 18f
        setTextColor(this@MainActivity.text)
        setHintTextColor(muted)
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_NUMBER_FLAG_DECIMAL
        setSingleLine(true)
        setPadding(dp(14), 0, dp(14), 0)
        background = rounded(this@MainActivity.surface, 16, this@MainActivity.line, dp(1))
    }

    private fun outputText() = TextView(this).apply {
        textSize = 15f
        setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(this@MainActivity.text)
        background = rounded(this@MainActivity.soft, 16, this@MainActivity.line, dp(1))
        setPadding(dp(14), dp(14), dp(14), dp(14))
    }

    private fun verticalDialogContent(titleText: String, explanation: String, input: EditText, result: TextView): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(6), dp(6), dp(6), 0)
            addView(TextView(this@MainActivity).apply {
                text = explanation
                textSize = 14f
                setTextColor(muted)
                setPadding(0, 0, 0, dp(12))
            }, wrap())
            addView(input, LinearLayout.LayoutParams(-1, dp(54)))
            addView(result, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })
        }
    }

    private fun dialogWithContent(titleText: String, box: LinearLayout): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle(titleText)
            .setView(box)
            .setNegativeButton("Close", null)
            .setPositiveButton("Check", null)
            .create()
    }

    private fun makeCard() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(surface)
        background = rounded(this@MainActivity.surface, 22, this@MainActivity.line, dp(1))
        elevation = dp(1).toFloat()
    }

    private fun button(symbol: String, size: Float) = TextView(this).apply {
        text = symbol
        textSize = size
        gravity = Gravity.CENTER
        setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(this@MainActivity.text)
        background = rounded(this@MainActivity.soft, 16, this@MainActivity.line, dp(1))
        isClickable = true
        isFocusable = true
    }

    private fun applyInsets() {
        root.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.getInsets(WindowInsets.Type.systemBars())
            topBar.setPadding(dp(12), bars.top + dp(8), dp(10), dp(10))
            content.setPadding(dp(16), dp(18), dp(16), bars.bottom + dp(28))
            insets
        }
        root.requestApplyInsets()
    }

    private fun applyThemeChoice(choice: ThemeChoice, recreateUi: Boolean) {
        val dark = when (choice) {
            ThemeChoice.DARK -> true
            ThemeChoice.LIGHT -> false
            ThemeChoice.SYSTEM -> systemIsDark()
        }
        isDark = dark
        bg = if (dark) Color.rgb(17, 17, 17) else Color.WHITE
        surface = if (dark) Color.rgb(27, 27, 27) else Color.WHITE
        text = if (dark) Color.WHITE else Color.rgb(20, 20, 20)
        muted = if (dark) Color.rgb(184, 184, 184) else Color.rgb(92, 92, 92)
        line = if (dark) Color.rgb(60, 60, 60) else Color.rgb(220, 220, 220)
        soft = if (dark) Color.rgb(35, 35, 35) else Color.rgb(246, 246, 246)
        if (recreateUi) recreate()
    }

    private fun updateSystemBarIcons() {
        val decor = window.decorView
        @Suppress("DEPRECATION")
        decor.systemUiVisibility = if (isDark) 0 else (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }

    private fun systemIsDark(): Boolean = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private fun loadThemeChoice(): ThemeChoice {
        val value = getPreferences(Context.MODE_PRIVATE).getInt("theme_choice", ThemeChoice.SYSTEM.ordinal)
        return ThemeChoice.values().getOrElse(value) { ThemeChoice.SYSTEM }
    }

    private fun saveThemeChoice(choice: ThemeChoice) {
        getPreferences(Context.MODE_PRIVATE).edit().putInt("theme_choice", choice.ordinal).apply()
    }

    private fun formatDouble(value: Double): String {
        if (!value.isFinite()) return "Not available"
        val rounded = BigDecimal.valueOf(value).round(mathContext).stripTrailingZeros()
        return rounded.toPlainString()
    }

    private fun formatDecimal(value: BigDecimal): String = value.round(mathContext).stripTrailingZeros().toPlainString()

    private fun rounded(fill: Int, radius: Int, strokeColor: Int? = null, strokeWidth: Int = 0): GradientDrawable = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radius).toFloat()
        if (strokeColor != null && strokeWidth > 0) setStroke(strokeWidth, strokeColor)
    }

    private fun solid(fill: Int): GradientDrawable = GradientDrawable().apply { setColor(fill) }

    private fun wrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
