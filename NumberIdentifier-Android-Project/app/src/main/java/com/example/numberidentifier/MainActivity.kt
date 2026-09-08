package com.example.numberidentifier

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "🔢 Number Identifier"
            textSize = 28f
            setTextColor(Color.rgb(20, 33, 61))
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())

        val subtitle = TextView(this).apply {
            text = "Enter an integer and discover its number classifications."
            textSize = 16f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(20))
        }
        root.addView(subtitle, matchWrap())

        val input = EditText(this).apply {
            hint = "Example: -25"
            textSize = 20f
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            setSingleLine(true)
        }
        root.addView(input, matchWrap())

        val analyzeButton = Button(this).apply {
            text = "ANALYZE"
            textSize = 16f
        }
        val buttonParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(16); bottomMargin = dp(16) }
        root.addView(analyzeButton, buttonParams)

        val results = TextView(this).apply {
            textSize = 18f
            setTextColor(Color.rgb(30, 30, 30))
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        val scroll = ScrollView(this).apply { addView(results) }
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        analyzeButton.setOnClickListener {
            val raw = input.text.toString().trim()
            val number = raw.toLongOrNull()

            if (raw.isEmpty()) {
                results.text = "⚠️ Please enter a number."
                return@setOnClickListener
            }

            if (number == null) {
                results.text = "⚠️ Please enter a valid integer.\n\nVersion 1 supports integers only."
                return@setOnClickListener
            }

            val r = NumberClassifier.classify(number)
            results.text = buildString {
                append("Result for $number\n\n")
                append(if (r.isNatural) "✓" else "✗").append(" Natural Number\n")
                append(if (r.isWhole) "✓" else "✗").append(" Whole Number\n")
                append(if (r.isInteger) "✓" else "✗").append(" Integer\n")
                append(if (r.isRational) "✓" else "✗").append(" Rational Number\n")
                append(if (r.isIrrational) "✓" else "✗").append(" Irrational Number\n")
                append(if (r.isReal) "✓" else "✗").append(" Real Number\n\n")
                append("Property\n")
                append("• Sign: ${r.sign}\n")
                append("• Parity: ${r.parity}\n")
                append("• Prime: ${if (r.isPrime) "Yes" else "No"}\n")
                append("• Composite: ${if (r.isComposite) "Yes" else "No"}")
            }
        }

        setContentView(root)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun matchWrap(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}
