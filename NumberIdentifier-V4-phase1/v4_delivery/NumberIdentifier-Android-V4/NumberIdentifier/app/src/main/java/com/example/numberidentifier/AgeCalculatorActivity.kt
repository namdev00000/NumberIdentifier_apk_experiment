package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 6 — date-of-birth based age calculation arrives in a later phase. */
class AgeCalculatorActivity : BaseActivity() {
    override fun screenTitle() = "Age Calculator"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "Pick your date of birth and see your exact age in years, months, and days as of today."
            ),
            wrap()
        )
    }
}
