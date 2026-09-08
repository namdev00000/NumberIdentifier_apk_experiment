package com.example.numberidentifier

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private val navy = Color.rgb(20, 52, 92)
    private val blue = Color.rgb(25, 103, 220)
    private val page = Color.rgb(247, 249, 252)
    private val darkText = Color.rgb(26, 39, 61)
    private val muted = Color.rgb(91, 105, 126)
    private val success = Color.rgb(30, 115, 72)
    private val danger = Color.rgb(180, 55, 55)
    private val resultBg = Color.rgb(241, 247, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this).apply { setBackgroundColor(page) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(24))
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val icon = TextView(this).apply {
            text = "π"
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(47, 102, 185), 16)
        }
        header.addView(icon, LinearLayout.LayoutParams(dp(48), dp(48)))

        val headerText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        headerText.addView(TextView(this).apply {
            text = "Number Identifier"
            textSize = 22f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(navy)
        }, wrap())
        headerText.addView(TextView(this).apply {
            text = "Find what type of number it is"
            textSize = 13f
            setTextColor(muted)
            setPadding(0, dp(2), 0, 0)
        }, wrap())
        header.addView(headerText, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(header)

        // Input card
        val inputCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(20))
            background = rounded(Color.WHITE, 20)
            elevation = dp(3).toFloat()
        }
        inputCard.addView(TextView(this).apply {
            text = "Enter an integer"
            textSize = 20f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(darkText)
        }, wrap())

        val input = EditText(this).apply {
            hint = "Example: -25"
            textSize = 20f
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            setSingleLine(true)
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(Color.WHITE, 12, Color.rgb(205, 214, 228), dp(1))
        }
        inputCard.addView(input, LinearLayout.LayoutParams(-1, dp(54)).apply { topMargin = dp(14) })

        val analyzeButton = Button(this).apply {
            text = "⌕  ANALYZE"
            textSize = 16f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(Color.WHITE)
            isAllCaps = false
            background = rounded(blue, 28)
            stateListAnimator = null
        }
        inputCard.addView(analyzeButton, LinearLayout.LayoutParams(-1, dp(54)).apply { topMargin = dp(16) })

        inputCard.addView(TextView(this).apply {
            text = "Version 1 supports integers such as -5, 0, 7 and 25."
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(12), dp(4), 0)
        }, wrap())
        root.addView(inputCard, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(26)
        })

        // Results card
        val resultsCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = rounded(resultBg, 20)
            visibility = android.view.View.GONE
        }
        val resultsTitle = TextView(this).apply {
            text = "Analysis"
            textSize = 20f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(darkText)
        }
        resultsCard.addView(resultsTitle, wrap())

        val resultsBody = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        resultsCard.addView(resultsBody, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        })
        root.addView(resultsCard, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        })

        // Explore panel
        val explore = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(20), dp(18), dp(20))
            background = rounded(Color.rgb(233, 241, 252), 20)
        }
        explore.addView(TextView(this).apply {
            text = "π   √   ∞   Σ"
            textSize = 24f
            setTextColor(blue)
            gravity = Gravity.CENTER
        }, wrap())
        explore.addView(TextView(this).apply {
            text = "Explore numbers"
            textSize = 19f
            setTypeface(Typeface.DEFAULT, Typeface.BOLD)
            setTextColor(darkText)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }, wrap())
        explore.addView(TextView(this).apply {
            text = "Learn what type of number it is and its properties."
            textSize = 14f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(4), dp(10), 0)
        }, wrap())
        root.addView(explore, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        })

        analyzeButton.setOnClickListener {
            resultsBody.removeAllViews()
            val raw = input.text.toString().trim()
            resultsCard.visibility = android.view.View.VISIBLE

            if (raw.isEmpty()) {
                resultsBody.addView(resultLine("⚠", "Please enter an integer.", false))
                return@setOnClickListener
            }

            val number = raw.toLongOrNull()
            if (number == null) {
                resultsBody.addView(resultLine("⚠", "Please enter a valid integer.", false))
                return@setOnClickListener
            }

            val result = NumberClassifier.classify(number)
            resultsBody.addView(sectionLine("Number: $number"))
            resultsBody.addView(resultLine(if (result.isNatural) "✓" else "✗", "Natural Number", result.isNatural))
            resultsBody.addView(resultLine(if (result.isWhole) "✓" else "✗", "Whole Number", result.isWhole))
            resultsBody.addView(resultLine("✓", "Integer", true))
            resultsBody.addView(resultLine("✓", "Rational Number", true))
            resultsBody.addView(resultLine("✗", "Irrational Number", false))
            resultsBody.addView(resultLine("✓", "Real Number", true))
            resultsBody.addView(sectionLine("Properties"))
            resultsBody.addView(resultLine("•", result.sign, true))
            resultsBody.addView(resultLine("•", result.parity, true))
            if (result.isPrime) resultsBody.addView(resultLine("✓", "Prime Number", true))
            else if (result.isComposite) resultsBody.addView(resultLine("✓", "Composite Number", true))
            else resultsBody.addView(resultLine("—", "Neither Prime nor Composite", false))
        }

        scrollView.addView(root)
        setContentView(scrollView)
    }

    private fun sectionLine(text: String): TextView = TextView(this).apply {
        this.text = text
        textSize = 15f
        setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(navy)
        setPadding(0, dp(8), 0, dp(6))
    }

    private fun resultLine(mark: String, text: String, positive: Boolean): TextView = TextView(this).apply {
        this.text = "$mark  $text"
        textSize = 16f
        setTextColor(if (positive) success else danger)
        setPadding(0, dp(6), 0, dp(6))
    }

    private fun wrap() = LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun rounded(
        fill: Int,
        radius: Int,
        strokeColor: Int? = null,
        strokeWidth: Int = 0
    ): GradientDrawable = GradientDrawable().apply {
        setColor(fill)
        cornerRadius = dp(radius).toFloat()
        if (strokeColor != null && strokeWidth > 0) setStroke(strokeWidth, strokeColor)
    }
}
