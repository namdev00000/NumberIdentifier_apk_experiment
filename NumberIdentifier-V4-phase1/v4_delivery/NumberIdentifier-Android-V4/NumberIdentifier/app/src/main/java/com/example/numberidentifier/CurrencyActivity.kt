package com.example.numberidentifier

import android.widget.LinearLayout

/** V4 feature 4 — Indian currency breakdown arrives in a later phase. */
class CurrencyActivity : BaseActivity() {
    override fun screenTitle() = "Currency Calculator"
    override fun buildContent(container: LinearLayout) {
        container.addView(
            placeholderCard(
                "Coming next",
                "Enter an amount in rupees and see the fewest coins (₹1, 2, 5, 10, 20) and notes " +
                    "(₹1, 5, 10, 20, 50, 100, 200, 500, 2000) that make it up."
            ),
            wrap()
        )
    }
}
