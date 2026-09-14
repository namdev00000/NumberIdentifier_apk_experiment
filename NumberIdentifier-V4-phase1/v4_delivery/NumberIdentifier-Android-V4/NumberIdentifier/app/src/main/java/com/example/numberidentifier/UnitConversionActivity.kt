package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 5 — distance & weight conversion arrives in a later phase. */
class UnitConversionActivity : BaseActivity() {
    override fun screenTitle() = "Unit Conversion"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "Convert distance (mm, cm, m, km, feet, inches, miles) and weight (grams, kilograms, pounds, and more)."
            ),
            wrap()
        )
    }
}
