package com.example.numberidentifier

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * The floating calculator (V4 main feature 3). Shown as a popup anchored to
 * the round button that sits bottom-right on screen.
 *
 * Supports +, -, x, / with parentheses and decimals, evaluated with normal
 * operator precedence (multiply/divide before add/subtract).
 */
object CalculatorPopup {

    private val mathContext = MathContext(15, RoundingMode.HALF_UP)

    fun show(activity: Activity, theme: Theme, anchor: View) {
        var expression = ""

        fun dp(v: Int) = (v * activity.resources.displayMetrics.density).toInt()

        val panel = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = GradientDrawable().apply {
                setColor(theme.surface)
                cornerRadius = dp(24).toFloat()
                setStroke(dp(1), theme.line)
            }
            elevation = dp(10).toFloat()
        }

        val display = TextView(activity).apply {
            text = "0"
            textSize = 28f
            gravity = Gravity.END
            setTextColor(theme.text)
            maxLines = 1
            setPadding(dp(12), dp(18), dp(12), dp(18))
            background = GradientDrawable().apply {
                setColor(theme.surfaceAlt)
                cornerRadius = dp(16).toFloat()
            }
        }
        panel.addView(
            display,
            LinearLayout.LayoutParams(dp(260), ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
        )

        fun refreshDisplay() {
            display.text = if (expression.isEmpty()) "0" else expression
        }

        fun evaluate(): String = try {
            formatResult(evaluateExpression(expression))
        } catch (e: Exception) {
            "Error"
        }

        val grid = GridLayout(activity).apply {
            columnCount = 4
            rowCount = 5
        }

        val keys = listOf(
            "C", "(", ")", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "0", ".", "⌫", "="
        )

        keys.forEach { key ->
            val button = TextView(activity).apply {
                text = key
                textSize = 18f
                gravity = Gravity.CENTER
                setTypeface(Typeface.DEFAULT, Typeface.BOLD)
                setTextColor(if (key == "=") Color.WHITE else theme.text)
                background = GradientDrawable().apply {
                    setColor(if (key == "=") Theme.ACCENT_CALCULATOR else theme.surfaceAlt)
                    cornerRadius = dp(14).toFloat()
                }
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    when (key) {
                        "C" -> {
                            expression = ""
                            refreshDisplay()
                        }
                        "⌫" -> {
                            if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            refreshDisplay()
                        }
                        "=" -> {
                            val result = evaluate()
                            expression = if (result == "Error") "" else result
                            display.text = result
                        }
                        else -> {
                            expression += key
                            refreshDisplay()
                        }
                    }
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = dp(56)
                height = dp(56)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED)
                setMargins(dp(4), dp(4), dp(4), dp(4))
            }
            grid.addView(button, params)
        }
        panel.addView(grid)

        val popup = PopupWindow(
            panel,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = dp(12).toFloat()
        }
        popup.showAtLocation(anchor, Gravity.BOTTOM or Gravity.END, dp(16), dp(100))
    }

    // ---- Minimal expression evaluator: +, -, x(*), /(÷), parentheses, decimals ----

    private fun evaluateExpression(expr: String): BigDecimal {
        val tokens = tokenize(expr)
        if (tokens.isEmpty()) return BigDecimal.ZERO
        var pos = 0

        fun parseFactor(): BigDecimal {
            if (pos < tokens.size && tokens[pos] == "-") {
                pos++
                return parseFactor().negate()
            }
            if (pos < tokens.size && tokens[pos] == "(") {
                pos++
                val value = parseExpr()
                if (pos < tokens.size && tokens[pos] == ")") pos++
                return value
            }
            val token = tokens.getOrElse(pos) { "0" }
            pos++
            return token.toBigDecimalOrNull() ?: BigDecimal.ZERO
        }

        fun parseTerm(): BigDecimal {
            var value = parseFactor()
            while (pos < tokens.size && (tokens[pos] == "*" || tokens[pos] == "/")) {
                val op = tokens[pos]
                pos++
                val rhs = parseFactor()
                value = if (op == "*") value.multiply(rhs) else value.divide(rhs, mathContext)
            }
            return value
        }

        fun parseExpr(): BigDecimal {
            var value = parseTerm()
            while (pos < tokens.size && (tokens[pos] == "+" || tokens[pos] == "-")) {
                val op = tokens[pos]
                pos++
                val rhs = parseTerm()
                value = if (op == "+") value.add(rhs) else value.subtract(rhs)
            }
            return value
        }

        return parseExpr()
    }

    private fun tokenize(expr: String): List<String> {
        val normalized = expr.replace("×", "*").replace("÷", "/").replace("−", "-")
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < normalized.length) {
            val c = normalized[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    while (i < normalized.length && (normalized[i].isDigit() || normalized[i] == '.')) i++
                    tokens.add(normalized.substring(start, i))
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' -> {
                    tokens.add(c.toString())
                    i++
                }
                else -> i++
            }
        }
        return tokens
    }

    private fun formatResult(value: BigDecimal): String =
        value.round(mathContext).stripTrailingZeros().toPlainString()
}
